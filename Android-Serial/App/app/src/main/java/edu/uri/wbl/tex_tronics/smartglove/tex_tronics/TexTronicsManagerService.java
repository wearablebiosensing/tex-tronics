package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattServices;
import edu.uri.wbl.tex_tronics.smartglove.io.IOUtil;
import edu.uri.wbl.tex_tronics.smartglove.mqtt.MqttConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.mqtt.MqttUpdateReceiver;
import edu.uri.wbl.tex_tronics.smartglove.mqtt.MqttUpdate;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.devices.SmartGlove;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.devices.SmartSock;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.devices.TexTronicsDevice;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by mcons on 2/27/2018.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class TexTronicsManagerService extends Service {
    /**
     * The tag used to log messages to the LogCat.
     *
     * @since 1.0
     */
    private static final String TAG = "TexTronics Service";

    /**
     * Used to identity the device address to connect to.
     *
     * @since 1.0
     */
    private static final String EXTRA_DEVICE = "tex_tronics.wbl.uri.ble.device";

    /**
     * Used to identify the transmit mode.
     *
     * @since 1.0
     */
    private static final String EXTRA_MODE = "tex_tronics.wbl.uri.ble.mode";

    /**
     * Used to identify the device type.
     *
     * @since 1.0
     */
    private static final String EXTRA_TYPE = "tex_tronics.wbl.uri.ble.type";
    /**
     * The packet ID for the first packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     *
     * @since 1.0
     */
    private static final byte PACKET_ID_1 = 0x01;
    /**
     * The packet ID for the second packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     *
     * @since 1.0
     */
    private static final byte PACKET_ID_2 = 0x02;

    /**
     * The value to return in onStartCommand
     *
     * https://developer.android.com/reference/android/app/Service.html
     *
     * @since 1.0
     */
    private static final int INTENT_RETURN_POLICY = START_STICKY;

    /**
     * This static method is provided for other components to use in order to interact with this
     * service. The connect method requests this service attempts to connect to the BLE device
     * with the given device address.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     * @param deviceAddress Device Address of BLE Device to connect to.
     *
     * @since 1.0
     */
    public static void connect(Context context, String deviceAddress, ExerciseMode exerciseMode, DeviceType deviceType) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.putExtra(EXTRA_MODE, exerciseMode);
        intent.putExtra(EXTRA_TYPE, deviceType);
        intent.setAction(TexTronicsAction.connect.toString());
        context.startService(intent);
    }

    /**
     * This static method is provided for other components to use in order to interact with this
     * service. The disconnect method requests this service attempts to disconnect from the
     * previously connected BLE device with the given device address. This method will only work
     * if the given devicehas already been successfully connected to using the connect method
     * provided by this service.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     * @param deviceAddress Device Address of BLE Device to disconnect from.
     *
     * @since 1.0
     */
    public static void disconnect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(TexTronicsAction.disconnect.toString());
        context.startService(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(TexTronicsAction.start.toString());
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(TexTronicsAction.stop.toString());
        context.startService(intent);
    }

    public static void publish(Context context, String deviceAddress) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(TexTronicsAction.publish.toString());
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        context.startService(intent);
    }

    private final int SERVICE_ID = 111;
    private final String CHANNEL_ID = "uri.wbl.tex_tronics.notification";

    /**
     * Used by inner classes to refer to this Service's Context. Weak Reference should not be needed
     * unless this Service implements multi-threading in future.
     */
    private Context mContext;
    private boolean mBleServiceBound = false;
    private BluetoothLeConnectionService mBleService;
    private ServiceConnection mBleServiceConnection;

    /**
     * Contains reference to each connected Tex-Tronics Device.
     */
    private HashMap<String, TexTronicsDevice> mTexTronicsList;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.presence_online)
                .setContentTitle("TexTronics Service")
                .setContentText("TexTronics is Active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(SERVICE_ID, notification);

        /* Store reference to this Service's context (good practice so that all methods/inner
         *  classes use a consistent Context object). This will need to be converted to a
         *  WeakReference object if multi-threading is implemented.
         *
         *  https://developer.android.com/reference/java/lang/ref/WeakReference.html
         *  https://community.oracle.com/blogs/enicholas/2006/05/04/understanding-weak-references
         *
         *  Possible Alternatives to WeakReference: https://medium.com/google-developer-experts/weakreference-in-android-dd1e66b9be9d
         */
        mContext = this;

        // Initialize the Connection Service to interface to BluetoothLeService
        mBleServiceConnection = new BleServiceConnection();

        // Initialize Container for Tex-Tronic Connected Devices (set the initial capacity to 4 - 2 gloves, 2 socks)
        mTexTronicsList = new HashMap<>(4);

        // Register BLE Update Receiver to Receive Information back from BluetoothLeService
        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        registerReceiver(mMqttUpdateReceiver, MqttUpdateReceiver.INTENT_FILTER);
        // Bind to BluetoothLeService. This Service provides the methods required to interact with BLE devices.
        bindService(new Intent(this, BluetoothLeConnectionService.class), mBleServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding. All components should interact with this Service via the static methods provided.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Initial Check of TexTronicsAction Packet to make sure it contains the device address and TexTronicsAction
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Invalid TexTronicsAction Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // TexTronicsAction to be performed on the TexTronics Device
        TexTronicsAction texTronicsAction = TexTronicsAction.getAction(intent.getAction());

        // Device Address of the BLE Device corresponding to this TexTronicsAction Packet
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);

        // Make sure it is a valid TexTronicsAction
        if(texTronicsAction == null) {
            Log.w(TAG, "Invalid TexTronicsAction Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // Execute TexTronicsAction Packet (this can be done with multi-threading to be able to Service multiple TexTronicsAction Packets at once)
        switch (texTronicsAction) {
            case start:
                MqttConnectionService.connect(mContext, "kaya/patient/data", false, false,"patientid" + System.currentTimeMillis());
                break;
            case connect: {
                // Attempt to connect to BLE Device (Device Type and Transmitting Mode should be obtained during scan)
                if (!intent.hasExtra(EXTRA_TYPE) || !intent.hasExtra(EXTRA_MODE)) {
                    Log.w(TAG, "Invalid connect TexTronicsAction Packet Received");
                    return INTENT_RETURN_POLICY;
                }
                ExerciseMode exerciseMode = (ExerciseMode) intent.getSerializableExtra(EXTRA_MODE);
                DeviceType deviceType = (DeviceType) intent.getSerializableExtra(EXTRA_TYPE);
                connect(deviceAddress, exerciseMode, deviceType);
            }
            break;
            case publish:
                publish(deviceAddress);
                break;
            case disconnect:
                // Attempt to disconnect from a currently connected BLE Device
                disconnect(deviceAddress);
                break;
            case stop:
                // TODO Disconnect from Connected Devices First
                stopSelf();
        }

        return INTENT_RETURN_POLICY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBLEUpdateReceiver);
        unregisterReceiver(mMqttUpdateReceiver);
        unbindService(mBleServiceConnection);

        Log.d(TAG,"Service Destroyed");

        super.onDestroy();
    }

    private void connect(String deviceAddress, ExerciseMode exerciseMode, DeviceType deviceType) {
        if (mBleServiceBound) {
            // TODO Modify TexTronicsDevice to have static method to determine DeviceType to Use
            switch (deviceType) {
                case SMART_GLOVE:
                    // TODO Assume connection will be successful, if connection fails we must remove it from list.
                    SmartGlove smartGlove = new SmartGlove(deviceAddress, exerciseMode);
                    mTexTronicsList.put(deviceAddress, smartGlove);
                    break;
                // Add Different Devices Here
                case SMART_SOCK:
                    // TODO Assume connection will be successful, if connection fails we must remove it from list.
                    SmartSock smartSock = new SmartSock(deviceAddress, exerciseMode);
                    mTexTronicsList.put(deviceAddress, smartSock);
                    break;
                default:

                    break;
            }

            mBleService.connect(deviceAddress);
        } else {
            Log.w(TAG,"Cannot Connect - BLE Connection Service is not bound yet!");
        }
    }

    private void publish(String deviceAddress) {
        TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
        if(device != null) {
            try {
                byte[] buffer = IOUtil.readFile(device.getCsvFile());
                String json = MqttConnectionService.generateJson(device.getDate(), device.getDeviceAddress(), new String(buffer));
                Log.d(TAG, "Publishing to " + deviceAddress);
                MqttConnectionService.publish(mContext, json, "/kaya/patient/data");
                TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.mqtt_published);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnect(String deviceAddress) {
        if (mBleServiceBound) {
            mBleService.disconnect(deviceAddress);
        } else {
            Log.w(TAG,"Could not Disconnect - BLE Connection Service is not bound!");
        }
    }

    private class BleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleServiceBound = true;
            mBleService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleServiceBound = false;
        }
    }

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received BLE Update");
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            switch (action) {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTING:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_connecting);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_connected);
                    mBleService.discoverServices(deviceAddress);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTING:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_disconnecting);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_disconnected);

                    TexTronicsDevice disconnectDevice = mTexTronicsList.get(deviceAddress);
                    if(disconnectDevice == null) {
                        Log.w(TAG, "Device not Found");
                        return;
                    }

                    // Automatically Publish Data on Disconnect
                    publish(deviceAddress);

                    mTexTronicsList.remove(deviceAddress);

                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                    if (characteristic != null) {
                        mBleService.enableNotifications(deviceAddress, characteristic);
                    }

                    BluetoothGattCharacteristic txChar = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.TX_CHARACTERISTIC);
                    mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x02});

                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                        Log.d(TAG, "Data Received");
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
                        if(device == null) {
                            Log.w(TAG,"Device Not Connected, Invalid Update");
                            break;
                        }
                        ExerciseMode exerciseMode = device.getExerciseMode();

                        try {
                            switch (exerciseMode) {
                                case FLEX_IMU:
                                    // Move data processing into Data Model?
                                    if (data[0] == PACKET_ID_1) {
                                        device.clear();
                                        device.setTimestamp(((data[1] & 0x00FF) << 24) | ((data[2] & 0x00FF) << 16) | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
                                        device.setThumbFlex((((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                        device.setIndexFlex((((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
                                        // TODO: Add rest of fingers
                                    } else if (data[0] == PACKET_ID_2) {
                                        device.setAccX(((data[1] & 0x00FF) << 8) | ((data[2] & 0x00FF)));
                                        device.setAccY(((data[3] & 0x00FF) << 8) | ((data[4] & 0x00FF)));
                                        device.setAccZ(((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF)));
                                        device.setGyrX(((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF)));
                                        device.setGyrY(((data[9] & 0x00FF) << 8) | ((data[10] & 0x00FF)));
                                        device.setGyrZ(((data[11] & 0x00FF) << 8) | ((data[12] & 0x00FF)));
                                        device.setMagX(((data[13] & 0x00FF) << 8) | ((data[14] & 0x00FF)));
                                        device.setMagY(((data[15] & 0x00FF) << 8) | ((data[16] & 0x00FF)));
                                        device.setMagZ(((data[17] & 0x00FF) << 8) | ((data[18] & 0x00FF)));

                                        device.logData(mContext);
                                    } else {
                                        Log.w(TAG, "Invalid Data Packet");
                                        return;
                                    }
                                    break;
                                case FLEX_ONLY:
                                    // First Data Set
                                    device.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                                    device.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                                    device.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));

                                    device.logData(mContext);

                                    // Second Data Set
                                    device.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                                    device.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                                    device.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                                    device.logData(mContext);

                                    // Third Data Set
                                    device.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                                    device.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                                    device.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                                    device.logData(mContext);
                                    break;
                            }
                        } catch (IllegalDeviceType | IOException e) {
                            Log.e(TAG, e.toString());
                            // TODO Handle Error Event
                            return;
                        }
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_READ:
                    break;
                case BluetoothLeConnectionService.GATT_DESCRIPTOR_WRITE:
                    break;
                case BluetoothLeConnectionService.GATT_NOTIFICATION_TOGGLED:
                    break;
                case BluetoothLeConnectionService.GATT_DEVICE_INFO_READ:
                    break;
            }
        }
    };

    private MqttUpdateReceiver mMqttUpdateReceiver = new MqttUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Received MQTT Update");
            if(!intent.hasExtra(UPDATE_TYPE)) {
                Log.w(TAG, "Update is empty");
                return;
            }

            MqttUpdate mqttUpdate = (MqttUpdate)intent.getSerializableExtra(UPDATE_TYPE);
            switch (mqttUpdate) {
                case connected:
                    Log.d(TAG, "MQTT Connected");
                    TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.mqtt_connected);
                    break;
                case disconnected:
                    Log.d(TAG, "MQTT Disconnected");
                    TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.mqtt_disconnected);
                    break;
                default:
                    Log.d(TAG, "Unknown MQTT Update");
                    break;
            }
        }
    };
}
