package andrewpeltier.smarttrousers.tex_tronics;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import andrewpeltier.smarttrousers.MainActivity;
import andrewpeltier.smarttrousers.ble.BluetoothLeConnectionService;
import andrewpeltier.smarttrousers.ble.GattCharacteristics;
import andrewpeltier.smarttrousers.ble.GattServices;
import andrewpeltier.smarttrousers.fragments.DataStreamFrag;
import andrewpeltier.smarttrousers.io.IOUtil;
import andrewpeltier.smarttrousers.mqtt.MqttConnectionService;
import andrewpeltier.smarttrousers.mqtt.MqttUpdateReceiver;
import andrewpeltier.smarttrousers.mqtt.UpdateType;
import andrewpeltier.smarttrousers.tex_tronics.devices.SmartGlove;
import andrewpeltier.smarttrousers.tex_tronics.devices.TexTronicsDevice;
import andrewpeltier.smarttrousers.tex_tronics.enums.Action;
import andrewpeltier.smarttrousers.tex_tronics.enums.DeviceType;
import andrewpeltier.smarttrousers.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smarttrousers.tex_tronics.exceptions.IllegalDeviceType;
import andrewpeltier.smarttrousers.visualize.Choice;


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

    /**
     * The choice of exercise being done
     */
    private static final String EXTRA_CHOICE = "tex_tronics.wbl.uri.ble.choice";

    /**
     * The UUID of the current exercise
     */
    private static final String EXTRA_EX_ID = "tex_tronics.wbl.uri.ble.ex_id";

    /**
     * The UUID of the routine
     */
    private static final String EXTRA_ROUTINE_ID  = "tex_tronics.wbl.uri.ble.routine_id";

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
    private static Context context;
    private static String deviceAddress;
    private static Choice choice;
    private static ExerciseMode exerciseMode;
    private static DeviceType deviceType;

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
    public static void connect(Context context, String deviceAddress, Choice choice, ExerciseMode exerciseMode, DeviceType deviceType, UUID exerciseID, UUID routineID) {
        TexTronicsManagerService.context = context;
        TexTronicsManagerService.deviceAddress = deviceAddress;
        TexTronicsManagerService.choice = choice;
        TexTronicsManagerService.exerciseMode = exerciseMode;
        TexTronicsManagerService.deviceType = deviceType;
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.putExtra(EXTRA_MODE, exerciseMode);
        intent.putExtra(EXTRA_TYPE, deviceType);
        intent.putExtra(EXTRA_CHOICE, choice);
        intent.putExtra(EXTRA_EX_ID, exerciseID.toString());
        intent.putExtra(EXTRA_ROUTINE_ID, routineID.toString());
        intent.setAction(Action.connect.toString());
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
        Log.v(TAG, "Disconnecting!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(Action.disconnect.toString());
        context.startService(intent);
    }

    public static void publish(Context context, String deviceAddress)
    {
        Log.w(TAG, "publish: Publishing!!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(Action.publish.toString());
        context.startService(intent);
    }

    public static void start(Context context) {
        Log.v(TAG, "Starting!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(Action.start.toString());
        context.startService(intent);
    }

    public static void stop(Context context) {
        Log.v(TAG, "Stopping!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(Action.stop.toString());
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
    private boolean mMqttServiceBound = false;
    private BluetoothLeConnectionService mBleService;
    private MqttConnectionService mMqttService;
    private ServiceConnection mBleServiceConnection, mMqttServiceConnection;

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

        mMqttServiceConnection = new MqttServiceConnection();

        // Initialize Container for Tex-Tronic Connected Devices (set the initial capacity to 4 - 2 gloves, 2 socks)
        mTexTronicsList = new HashMap<>(4);

        // Register BLE Update Receiver to Receive Information back from BluetoothLeService
        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        registerReceiver(mMqttUpdateReceiver, MqttUpdateReceiver.INTENT_FILTER);
        // Bind to BluetoothLeService. This Service provides the methods required to interact with BLE devices.
        bindService(new Intent(this, BluetoothLeConnectionService.class), mBleServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, MqttConnectionService.class), mMqttServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding. All components should interact with this Service via the static methods provided.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Initial Check of Action Packet to make sure it contains the device address and Action
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Invalid Action Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // Action to be performed on the TexTronics Device
        Action action = Action.getAction(intent.getAction());

        // Device Address of the BLE Device corresponding to this Action Packet
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);

        // Make sure it is a valid Action
        if(action == null) {
            Log.w(TAG, "Invalid Action Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // Execute Action Packet (this can be done with multi-threading to be able to Service multiple Action Packets at once)
        switch (action) {
            case connect: {
                // Attempt to connect to BLE Device (Device Type and Transmitting Mode should be obtained during scan)
                if (!intent.hasExtra(EXTRA_TYPE) || !intent.hasExtra(EXTRA_MODE)) {
                    Log.w(TAG, "Invalid connect Action Packet Received");
                    return INTENT_RETURN_POLICY;
                }
                ExerciseMode exerciseMode = (ExerciseMode) intent.getSerializableExtra(EXTRA_MODE);
                DeviceType deviceType = (DeviceType) intent.getSerializableExtra(EXTRA_TYPE);
                Choice choice = (Choice) intent.getSerializableExtra(EXTRA_CHOICE);
                String exerciseID = (String) intent.getSerializableExtra(EXTRA_EX_ID);
                String routineID = (String) intent.getSerializableExtra(EXTRA_ROUTINE_ID);
                connect(deviceAddress, exerciseMode, deviceType, choice, exerciseID, routineID);
            }
            break;
            case disconnect:
                // Attempt to disconnect from a currently connected BLE Device
                disconnect(deviceAddress);
                break;
            case publish:
                publish(deviceAddress);
                break;
            case stop:
                // TODO Check for multiple devices
                stopSelf();
        }

        return INTENT_RETURN_POLICY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBLEUpdateReceiver);
        unregisterReceiver(mMqttUpdateReceiver);
        unbindService(mBleServiceConnection);
        unbindService(mMqttServiceConnection);

        Log.d(TAG,"Manager Service Destroyed");

        super.onDestroy();
    }



    private void connect(String deviceAddress, ExerciseMode exerciseMode, DeviceType deviceType, Choice choice, String exerciseID, String routineID) {
        if (mBleServiceBound) {
            SmartGlove smartGlove;
            // TODO Modify TexTronicsDevice to have static method to determine DeviceType to Use
            switch (deviceType) {
                case SMART_GLOVE:
                    // TODO Assume connection will be successful, if connection fails we must remove it from list.
                    smartGlove = new SmartGlove(deviceAddress, exerciseMode, choice, exerciseID, routineID);
                    mTexTronicsList.put(deviceAddress, smartGlove);
                    break;
                // Add Different Devices Here
                case SMART_SOCK:
                    // Added the Smart Sock code, just copied from above
                    smartGlove = new SmartGlove(deviceAddress, exerciseMode, choice, exerciseID, routineID);
                    mTexTronicsList.put(deviceAddress, smartGlove);
                    break;
                default:

                    break;
            }

            mBleService.connect(deviceAddress);
        } else {
            Log.w(TAG,"Cannot Connect - BLE Connection Service is not bound yet!");
        }
    }

    private void disconnect(String deviceAddress) {
        if (mBleServiceBound) {
            mBleService.disconnect(deviceAddress);

        } else {
            Log.w(TAG,"Could not Disconnect - BLE Connection Service is not bound!");
        }
    }

    private void publish(String deviceAddress)
    {
        if(mMqttServiceBound) {
            try {
                TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
                if(device != null)
                {
                    byte[] buffer = IOUtil.readFile(device.getCsvFile());
                    String json = MqttConnectionService.generateJson(device.getDate(),
                            device.getDeviceAddress(),
                            Choice.toString(device.getChoice()),
                            device.getExerciseID(),
                            device.getRoutineID(),
                            new String(buffer));
                    Log.d("SmartGlove", "JSON: " + json);
                    mMqttService.publishMessage(json);
                }
                else
                    Log.d(TAG, "publish: Publish failed. Device is null");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class BleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleServiceBound = true;
            mBleService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();
            TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.started);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleServiceBound = false;
        }
    }

    private class MqttServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMqttServiceBound = true;
            mMqttService = ((MqttConnectionService.MqttConnectionBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMqttServiceBound = false;
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

                    TexTronicsDevice disconnectingDevice = mTexTronicsList.get(deviceAddress);

                    //TODO: DEBUG ME!!!!!

                    // Send to Server via MQTT
                    if(mMqttServiceBound) {
                        try {
                            byte[] buffer = IOUtil.readFile(disconnectingDevice.getCsvFile());
                            String json = MqttConnectionService.generateJson(disconnectingDevice.getDate(),
                                    disconnectingDevice.getDeviceAddress(),
                                    Choice.toString(disconnectingDevice.getChoice()) ,
                                    disconnectingDevice.getExerciseID(),
                                    disconnectingDevice.getRoutineID(),
                                    new String(buffer));
                            Log.d("SmartGlove", "JSON: " + json);
                            mMqttService.publishMessage(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_disconnected);

                    TexTronicsDevice disconnectDevice = mTexTronicsList.get(deviceAddress);
                    if(disconnectDevice == null) {
                        Log.w(TAG, "Device not Found");
                        return;
                    }

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
                        ExerciseMode exerciseMode = null;
                        if(device != null)
                            exerciseMode = device.getExerciseMode();

                        if(exerciseMode != null)
                        {
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

                                            if(DataStreamFrag.START_LOG)
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

                                        if(DataStreamFrag.START_LOG)
                                            device.logData(mContext);

                                        // Second Data Set
                                        device.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                                        device.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                                        device.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                                        if(DataStreamFrag.START_LOG)
                                            device.logData(mContext);

                                        // Third Data Set
                                        device.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                                        device.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                                        device.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                                        if(DataStreamFrag.START_LOG)
                                            device.logData(mContext);
                                        break;
                                }
                            } catch (IllegalDeviceType | IOException e) {
                                Log.e(TAG, e.toString());
                                // TODO Handle Error Event
                                return;
                            }
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

            UpdateType updateType = (UpdateType)intent.getSerializableExtra(UPDATE_TYPE);
            switch (updateType) {
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