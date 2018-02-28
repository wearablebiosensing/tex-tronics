package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

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

import java.util.HashMap;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattServices;

import static edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsData.TexTronicsMode.FLEX_IMU;

/**
 * Created by mcons on 2/27/2018.
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
    public static void connect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(TexTronicsData.TexTronicsAction.connect.toString());
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
        intent.setAction(TexTronicsData.TexTronicsAction.disconnect.toString());
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
    private HashMap<String, TexTronicsData> mTexTronicsList;

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
        mTexTronicsList = new HashMap<>(4);

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
        TexTronicsData.TexTronicsAction action = TexTronicsData.TexTronicsAction.getAction(intent.getAction());

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
                TexTronicsData.TexTronicsMode smartGloveMode = (TexTronicsData.TexTronicsMode) intent.getSerializableExtra(EXTRA_MODE);
                TexTronicsData.TexTronicsDevice texTronicsDevice = (TexTronicsData.TexTronicsDevice) intent.getSerializableExtra(EXTRA_TYPE);
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

    private void connect(String deviceAddress, TexTronicsData.TexTronicsMode texTronicsMode, TexTronicsData.TexTronicsDevice texTronicsDevice) {
        if (mServiceBound) {
            TexTronicsData texTronicsData = new TexTronicsData(deviceAddress, texTronicsDevice, texTronicsMode);
            // TODO Assume connection will be successful, if connection fails we must remove it from list.
            mTexTronicsList.put(deviceAddress, texTronicsData);
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
            Log.d(TAG, "Received BLE Update");
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            switch (action) {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    mService.discoverServices(deviceAddress);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:
                    mTexTronicsList.remove(deviceAddress);
                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    BluetoothGattCharacteristic characteristic = mService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                    if (characteristic != null) {
                        mService.enableNotifications(deviceAddress, characteristic);
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    /*UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                        Log.d(TAG, "Data Received");
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        TexTronicsData texTronicsData = mTexTronicsList.get(deviceAddress);

                        if(texTronicsData.getTexTronicsMode() == FLEX_IMU) {
                            if(data[0] == PACKET_ID_1) {
                                texTronicsData.clear();
                                texTronicsData.setTimestamp(((data[1] & 0x00FF) << 24) | ((data[2] & 0x00FF) << 16) | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
                                texTronicsData.setThumbFlex((((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                texTronicsData.setIndexFlex((((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
                                // TODO: Add rest of fingers
                            } else if(data[0] == PACKET_ID_2) {
                                texTronicsData.setAccX(((data[1] & 0x00FF) << 8) | ((data[2] & 0x00FF)));
                                texTronicsData.setAccY(((data[3] & 0x00FF) << 8) | ((data[4] & 0x00FF)));
                                texTronicsData.setAccZ(((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF)));
                                texTronicsData.setGyrX(((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF)));
                                texTronicsData.setGyrY(((data[9] & 0x00FF) << 8) | ((data[10] & 0x00FF)));
                                texTronicsData.setGyrZ(((data[11] & 0x00FF) << 8) | ((data[12] & 0x00FF)));
                                texTronicsData.setMagX(((data[13] & 0x00FF) << 8) | ((data[14] & 0x00FF)));
                                texTronicsData.setMagY(((data[15] & 0x00FF) << 8) | ((data[16] & 0x00FF)));
                                texTronicsData.setMagZ(((data[17] & 0x00FF) << 8) | ((data[18] & 0x00FF)));
                            } else {
                                Log.w(TAG,"Invalid Data Packet");
                                return;
                            }
                        } else {
                            // First Data Set
                            texTronicsData.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                            texTronicsData.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                            texTronicsData.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));

                            texTronicsData.log(mContext);

                            // Second Data Set
                            texTronicsData.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                            texTronicsData.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                            texTronicsData.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                            texTronicsData.log(mContext);

                            // Third Data Set
                            texTronicsData.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                            texTronicsData.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                            texTronicsData.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                            texTronicsData.log(mContext);
                        }
                    }*/
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
