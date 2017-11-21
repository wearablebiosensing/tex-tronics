package edu.uri.wbl.smartglove.ble.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import edu.uri.wbl.smartglove.ble.enums.BleAction;
import edu.uri.wbl.smartglove.ble.enums.BleStatus;
import edu.uri.wbl.smartglove.ble.models.BluetoothCharacteristicModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothLeModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothServiceModel;
import edu.uri.wbl.smartglove.ble.receivers.BleUpdateReceiver;

/**
 * Created by Matthew Constant on 11/12/2017.
 *
 * The BluetoothLeService provides an abstraction layer which sits above the Android BLE API.
 * This Service can be used by any other Service or Activity to connect to and communicate with
 * BLE peripherals (Android does not support its devices to be peripherals, however emulation is
 * possible and not implemented in this Service). This Service runs on a separate process than that
 * of the caller, allowing this Service to stay alive even after the caller has been destroyed.
 * This allows for [smooth](https://www.youtube.com/watch?v=7tzc-dB8Xuk) data collection in the
 * background as well as for multiple callers to use the same BluetoothLeService instance without
 * conflict.
 *
 * In order to interact with this Service, users send "actions", i.e. CONNECT, to this Service.
 * This Service then carries out this action using the Android BLE API. If information is expected
 * in return from the action requested, the caller must implement a BleUpdateReceiver and wait for
 * the expected update, i.e. UPDATE_CONNECTED. Each update broadcast's Intent contains two extras,
 * an int indicating the update being broadcast (the corresponding update to each int is found in
 * the BleUpdateReceiver Class) and a BluetoothLeModel containing information about the
 * corresponding BLE device - such as Bluetooth Device Address, Services, and Characteristics.
 *
 * By using this Service, the caller only needs to interact with four simple classes:
 * BluetoothLeService, BleUpdateReceiver, BluetoothLeModel (as well as the Service and
 * Characteristic models), and BluetoothLeModelManager.
 */

public class BluetoothLeService extends Service {
    /**
     * Specifies the action to taken. Should be one of the values found in the BleAction enum.
     */
    private static String EXTRA_ACTION = "wbl.tex_tronics.extra_action";
    /**
     * Specifies the Bluetooth Device Address corresponding to the action.
     */
    private static String EXTRA_DEVICE = "wbl.tex_tronics.extra_device";
    /**
     * Specifies the Service to be modified or viewed.
     */
    private static String EXTRA_SERVICE = "wbl.tex_tronics.extra_service";
    /**
     * Specifies the Characteristic to be modified or viewed.
     */
    private static String EXTRA_CHARACTERISTIC = "wbl.tex_tronics.extra_characteristic";

    private static ConcurrentHashMap<String, BluetoothLeModel> sConnections;

    public static BluetoothLeModel GET_DEVICE(String address) {
        if(sConnections == null) {
            sConnections = new ConcurrentHashMap<>(7);
            BluetoothLeModel bluetoothLeModel = new BluetoothLeModel(address);
            sConnections.put(address, bluetoothLeModel);
            return bluetoothLeModel;
        } else {
            if(sConnections.containsKey(address)) {
                return sConnections.get(address);
            } else {
                BluetoothLeModel bluetoothLeModel = new BluetoothLeModel(address);
                sConnections.put(address, bluetoothLeModel);
                return bluetoothLeModel;
            }
        }
    }

    public static void REMOVE_DEVICE(String address) {
        if(sConnections != null && sConnections.containsKey(address)) {
            sConnections.remove(address);
        }
    }

    /**
     * The START action starts and initializes the BluetoothLeService. This action can be bypassed
     * if the next action will be CONNECT.
     * @param context The Caller's Context.
     */
    public static void START(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        intent.putExtra(EXTRA_ACTION, BleAction.START.getAction());
        context.startService(intent);
    }

    /**
     * The STOP action forces the BluetoothLeService to stop, if it is currently running. The
     * Service will disconnect from all connected devices and then stop itself (stopSelf()). This
     * should only be used if the Service is "out of whack".
     * @param context The Caller's Context.
     */
    public static void STOP(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        intent.putExtra(EXTRA_ACTION, BleAction.STOP.getAction());
        context.startService(intent);
    }

    /**
     * The CONNECT action initiates a connect request to the given BLE device. When the BLE
     * device has been connected, an UPDATE_CONNECTED update will be broadcast by the
     * BleUpdateReceiver.
     *
     * NOTE: The corresponding UPDATE_CONNECTED update will include a BluetoothLeModel, however the
     * Services List will (read: should) be empty. This list is only populated through a call to
     * DISCOVER_SERVICES on the given BLE Device each time it is connected.
     *
     * @param context The Caller's Context
     * @param bluetoothLeModel The BLE Device to connect to.
     */
    public static void CONNECT(Context context, BluetoothLeModel bluetoothLeModel) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        intent.putExtra(EXTRA_ACTION, BleAction.CONNECT.getAction());
        intent.putExtra(EXTRA_DEVICE, bluetoothLeModel);
        context.startService(intent);
    }

    /**
     * The DISCONNECT action disconnects a BLE device. If the device is not currently connected,
     * no action is taken. When the BLE device is disconnected, an UPDATE_DISCONNECTED update will
     * be broadcast by the BleUpdateReceiver.
     *
     * NOTE: The corresponding UPDATE_DISCONNECTED update will contain a BluetoothLeModel, however
     * it should not be used for anything other than identifying which device was disconnected.
     *
     * @param context The Caller's Context.
     * @param bluetoothLeModel The BLE Device to disconnect from.
     */
    public static void DISCONNECT(Context context, BluetoothLeModel bluetoothLeModel) {

    }

    /**
     * The DISCOVER_SERVICES action attempts to read the Services, Characteristics, and Descriptors
     * available on the given BLE device. The given BLE Device better be connected already through
     * the CONNECT action or the BluetoothLeService will be very upset (read: do nothing).
     *
     * NOTE: The corresponding UPDATE_DISCOVERED_SERVICES update will contain a BluetoothLeModel,
     * and it will contain an updated list of Services. When a BLE Device is connected, this
     * action is the only way to initialize the Services list.
     *
     * @param context The Caller's Context.
     * @param bluetoothLeModel The CONNECTED BLE Device to discover services from.
     */
    public static void DISCOVER_SERVICES(Context context, BluetoothLeModel bluetoothLeModel) {

    }

    public static void REQUEST_READ(Context context, BluetoothLeModel bluetoothLeModel, BluetoothServiceModel bluetoothServiceModel, BluetoothCharacteristicModel bluetoothCharacteristicModel) {

    }

    public static void REQUEST_WRITE(Context context, BluetoothLeModel bluetoothLeModel, BluetoothServiceModel bluetoothServiceModel, BluetoothCharacteristicModel bluetoothCharacteristicModel) {

    }

    public static void ENABLE_NOTIFICATION(Context context, BluetoothLeModel bluetoothLeModel, BluetoothServiceModel bluetoothServiceModel, BluetoothCharacteristicModel bluetoothCharacteristicModel) {

    }

    public static void DISABLE_NOTIFICATION(Context context, BluetoothLeModel bluetoothLeModel, BluetoothServiceModel bluetoothServiceModel, BluetoothCharacteristicModel bluetoothCharacteristicModel) {

    }

    private final int NOTIFICATION_ID = 69;                 // Nice.

    private Context mContext;                               // Used by inner classes to use Service resources
    private BluetoothAdapter mBluetoothAdapter;             // Bluetooth Adapter Reference

    @Override
    public void onCreate() {
        super.onCreate();

        // Store reference to Service's context (used by inner classes)
        mContext = this;

        // Initialize Bluetooth Adapter
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null) {
            Log.e(this.getClass().getSimpleName(), "Failed Initializing BT Adapter!");
            stopSelf();
        }

        // Declare Foreground Service to keep Service alive as long as needed
        startForeground(NOTIFICATION_ID, getNotification(BleStatus.INITIALIZING.getStatus()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent == null || !intent.hasExtra(EXTRA_ACTION))
            return START_NOT_STICKY;

        BleAction action = BleAction.GET(intent.getStringExtra(EXTRA_ACTION));

        if(action == null)
            return START_NOT_STICKY;

        switch (action) {
            case START:
                Log.d(this.getClass().getSimpleName(), "ACTION: Start");
                // Do nothing
                break;
            case STOP:
                Log.d(this.getClass().getSimpleName(), "ACTION: Stop");
                // TODO: Gracefully disconnect from all connected devices
                stopSelf();
                break;
            case CONNECT:
                Log.d(this.getClass().getSimpleName(), "ACTION: Connect");
                if(!intent.hasExtra(EXTRA_DEVICE)) {
                    Log.w(this.getClass().getSimpleName(), "Invalid Action Packet");
                    break;
                }
                BluetoothLeModel bluetoothLeModel = (BluetoothLeModel) intent.getSerializableExtra(EXTRA_DEVICE);
                if(bluetoothLeModel == null) {
                    Log.w(this.getClass().getSimpleName(), "NULL BT Model");
                    break;
                }
                String bluetoothDeviceAddress = bluetoothLeModel.getBluetoothDeviceAddress();
                if(bluetoothDeviceAddress == null) {
                    Log.w(this.getClass().getSimpleName(), "NULL BT_ADDR");
                    break;
                }
                connect(bluetoothDeviceAddress);
                break;
            case DISCONNECT:
                Log.d(this.getClass().getSimpleName(), "ACTION: Disconnect");
                break;
            case DISCOVER_SERVICES:
                Log.d(this.getClass().getSimpleName(), "ACTION: Discover Services");
                break;
            case REQUEST_READ:
                Log.d(this.getClass().getSimpleName(), "ACTION: Request Read");
                break;
            case REQUEST_WRITE:
                Log.d(this.getClass().getSimpleName(), "ACTION: Request Write");
                break;
            case ENABLE_NOTIFICATION:
                Log.d(this.getClass().getSimpleName(), "ACTION: Enable Notification");
                break;
            case DISABLE_NOTIFICATION:
                Log.d(this.getClass().getSimpleName(), "ACTION: Disable Notifications");
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "ACTION: Unknown");
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

    }

    private void connect(String bluetoothDeviceAddress) {
        if(bluetoothDeviceAddress == null) {
            Log.w(this.getClass().getSimpleName(), "Could not Connect: NULL BT ADDR");
            return;
        }

        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
        if(bluetoothDevice == null) {
            Log.w(this.getClass().getSimpleName(), "Could not Connect: NULL BT Device");
            return;
        }
        BluetoothGatt gatt = bluetoothDevice.connectGatt(mContext, true, mBluetoothGattCallback);
        if(gatt == null) {
            Log.w(this.getClass().getSimpleName(), "Could not Connect: NULL GATT");
            return;
        }

        Log.d(this.getClass().getSimpleName(), "Connecting to " + bluetoothDevice.getName() + " (" + bluetoothDevice.getAddress() + ")");
    }

    private void disconnect(String bluetoothDeviceAddress) {

    }

    private void discoverServices(String bluetoothDeviceAddress) {

    }

    private void requestRead(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {

    }

    private void requestWrite(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {

    }

    private void enableNotification(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {

    }

    private void disableNotification(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {

    }

    private Notification getNotification(String status) {
        String CHANNEL_ID = "ble_channel";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.presence_online)
                        .setContentTitle("SmartGlove is Running")
                        .setContentText("Status: " + status);
        // Clicking the Notification will send the STOP action
        Intent resultIntent = new Intent(this, BluetoothLeService.class);
        resultIntent.putExtra(EXTRA_ACTION, BleAction.STOP);
        PendingIntent resultPendingIntent = PendingIntent.getService(mContext, 347, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder.mNotification;
    }

    private void updateNotification(String status) {
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setContentTitle("SmartGlove is Active");
        notificationBuilder.setContentText("Connection Status: " + status);
        notificationBuilder.setSmallIcon(android.R.drawable.presence_online);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setOngoing(true);

        Intent intent = new Intent(mContext, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleAction.STOP);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 347, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());*/
    }

    private void sendBroadcast(int BleUpdate, BluetoothLeModel device) {
        Intent intent = new Intent(BleUpdateReceiver.INTENT_FILTER.getAction(0));
        intent.putExtra(BleUpdateReceiver.EXTRA_UPDATE, BleUpdate);
        intent.putExtra(BleUpdateReceiver.EXTRA_DEVICE, device);
        sendBroadcast(intent);
    }

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(this.getClass().getSimpleName(), "GATT Error: Status " + status);
                return;
            }

            BluetoothLeModel bluetoothLeModel = GET_DEVICE(gatt.getDevice().getAddress());
            if(bluetoothLeModel == null) {
                Log.e(this.getClass().getSimpleName(), "GATT Error: Bluetooth Model not Found");
                return;
            }

            int bleUpdate;

            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    bleUpdate = BleUpdateReceiver.UPDATE_CONNECTED;
                    updateNotification(BleStatus.CONNECTED.getStatus());
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    bleUpdate = BleUpdateReceiver.UPDATE_DISCONNECTED;
                    updateNotification(BleStatus.DISCONNECTED.getStatus());
                    break;
                default:
                    Log.w(this.getClass().getSimpleName(), "GATT New State: Unknown");
                    return;
            }

            sendBroadcast(bleUpdate, bluetoothLeModel);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
}
