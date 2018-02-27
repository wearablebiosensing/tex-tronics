package edu.uri.wbl.tex_tronics.smartglove.smart_glove;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattServices;
import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;

/**
 * The SmartGlove Manager Service interfaces with WBL's BLE Services to connect to the Tex-Tronics
 * devices, such as the Smart Glove and Smart Socks (In-Sole). This service provides an abstraction
 * for the User so that he/she only needs to use the BLE functions necessary to interface to the
 * desired Tex-Tronics device.
 *
 * This service binds to the BluetoothLeService, which it uses to requests actions from the BLE
 * device (such as connect, disconnect, and enable notifications). This service also utilizes a
 * BLEUpdate Broadcast Receiver to receive updates back from the BluetoothLeService.
 *
 * Refer to the BluetoothLeService to see what data is available from the various BLE Updates.
 *
 * @author Matthew Constant
 * @version 1.0, 02/27/2018
 */

public class SmartGloveManagerService extends Service {
    private static final int INTENT_RETURN_POLICY = START_REDELIVER_INTENT;
    private static final String TAG = "Tex-Tronics Service";
    /**
     * Used to identity the device address to connect to.
     */
    private static final String EXTRA_DEVICE = "tex_tronics.wbl.uri.ble.sg.device";
    private static final String EXTRA_MODE = "tex_tronics.wbl.uri.ble.sg.mode";
    private static final String EXTRA_TYPE = "tex_tronics.wbl.uri.ble.sg.type";
    /**
     * The packet ID for the first packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     */
    private static final byte PACKET_ID_1 = 0x01;
    /**
     * The packet ID for the second packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     */
    private static final byte PACKET_ID_2 = 0x02;
    /**
     * This static method is provided for other components to use in order to interact with this
     * service. The connect method requests this service attempts to connect to the BLE device
     * with the given device address.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     * @param deviceAddress Device Address of BLE Device to connect to.
     */
    public static void connect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, SmartGloveManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(SmartGloveAction.connect.toString());
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
     */
    public static void disconnect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, SmartGloveManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(SmartGloveAction.disconnect.toString());
        context.startService(intent);
    }

    /**
     * Used by inner classes to refer to this Service's Context. Weak Reference should not be needed
     * unless this Service implements multi-threading in future.
     */
    private Context mContext;
    private boolean mServiceBound;
    private BluetoothLeConnectionService mService;
    private ServiceConnection mServiceConnection;

    /**
     * Contains reference to each connected Tex-Tronics Device.
     */
    private HashMap<String, SmartGloveData> mSmartGloveDataList;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

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
        mServiceConnection = new BleServiceConnection();

        // Initialize Container for Tex-Tronic Connected Devices (set the initial capacity to 4 - 2 gloves, 2 socks)
        mSmartGloveDataList = new HashMap<>(4);

        // Register BLE Update Receiver to Receive Information back from BluetoothLeService
        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        // Bind to BluetoothLeService. This Service provides the methods required to interact with BLE devices.
        bindService(new Intent(this, BluetoothLeConnectionService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding. All components should interact with this Service via the static methods provided.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Initial Check of Action Packet to make sure it contains the device address and action
        if (intent == null || !intent.hasExtra(EXTRA_DEVICE) || intent.getAction() == null) {
            Log.w(TAG, "Invalid Action Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // Device Address of the BLE Device corresponding to this Action Packet
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);
        // Action to be performed on the BLE Device
        SmartGloveAction action = SmartGloveAction.getAction(intent.getAction());

        // Make sure it is a valid action
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
                SmartGloveMode smartGloveMode = (SmartGloveMode)intent.getSerializableExtra(EXTRA_MODE);
                TexTronicsDevice texTronicsDevice = (TexTronicsDevice) intent.getSerializableExtra(EXTRA_TYPE);
                connect(deviceAddress, smartGloveMode, texTronicsDevice);
            }
                break;
            case disconnect:
                // Attempt to disconnect from a currently connected BLE Device
                disconnect(deviceAddress);
                break;
        }

        return INTENT_RETURN_POLICY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBLEUpdateReceiver);
        unbindService(mServiceConnection);

        super.onDestroy();
    }

    private void connect(String deviceAddress, SmartGloveMode smartGloveMode, TexTronicsDevice texTronicsDevice) {
        if (mServiceBound) {
            SmartGloveData smartGloveData = new SmartGloveData(deviceAddress, texTronicsDevice, smartGloveMode);
            // TODO Assume connection will be successful, if connection fails we must remove it from list.
            mSmartGloveDataList.put(deviceAddress, smartGloveData);
            mService.connect(deviceAddress);
        } else {
            Log.w(TAG,"Cannot Connect - BLE Connection Service is not bound yet!");
        }
    }

    private void disconnect(String deviceAddress) {
        if (mServiceBound) {
            mService.disconnect(deviceAddress);
        } else {
            Log.w(TAG,"Could not Disconnect - BLE Connection Service is not bound!");
        }
    }

    private class BleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBound = true;
            mService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    }

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received Update");
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            switch (action) {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    mService.discoverServices(GattDevices.SMART_GLOVE_DEVICE);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:
                    mSmartGloveDataList.remove(deviceAddress);
                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    BluetoothGattCharacteristic characteristic = mService.getCharacteristic(GattDevices.SMART_GLOVE_DEVICE, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                    if (characteristic != null) {
                        mService.enableNotifications(GattDevices.SMART_GLOVE_DEVICE, characteristic);
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                        Log.d(TAG, "Data Received");
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        SmartGloveData smartGloveData = mSmartGloveDataList.get(deviceAddress);

                        if(smartGloveData.getSmartGloveMode() == SmartGloveMode.FLEX_IMU) {
                            if(data[0] == PACKET_ID_1) {
                                smartGloveData.clear();
                                smartGloveData.setTimestamp(((data[1] & 0x00FF) << 24) | ((data[2] & 0x00FF) << 16) | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
                                smartGloveData.setThumbFlex((((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                smartGloveData.setIndexFlex((((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
                                // TODO: Add rest of fingers
                            } else if(data[0] == PACKET_ID_2) {
                                smartGloveData.setAccX(((data[1] & 0x00FF) << 8) | ((data[2] & 0x00FF)));
                                smartGloveData.setAccY(((data[3] & 0x00FF) << 8) | ((data[4] & 0x00FF)));
                                smartGloveData.setAccZ(((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF)));
                                smartGloveData.setGyrX(((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF)));
                                smartGloveData.setGyrY(((data[9] & 0x00FF) << 8) | ((data[10] & 0x00FF)));
                                smartGloveData.setGyrZ(((data[11] & 0x00FF) << 8) | ((data[12] & 0x00FF)));
                                smartGloveData.setMagX(((data[13] & 0x00FF) << 8) | ((data[14] & 0x00FF)));
                                smartGloveData.setMagY(((data[15] & 0x00FF) << 8) | ((data[16] & 0x00FF)));
                                smartGloveData.setMagZ(((data[17] & 0x00FF) << 8) | ((data[18] & 0x00FF)));
                            } else {
                                Log.w(TAG,"Invalid Data Packet");
                                return;
                            }
                        } else {
                            // First Data Set
                            smartGloveData.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                            smartGloveData.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                            smartGloveData.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));

                            smartGloveData.log(mContext);

                            // Second Data Set
                            smartGloveData.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                            smartGloveData.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                            smartGloveData.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                            smartGloveData.log(mContext);

                            // Third Data Set
                            smartGloveData.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                            smartGloveData.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                            smartGloveData.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                            smartGloveData.log(mContext);
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
}
