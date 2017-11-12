package edu.uri.wbl.smartglove.ble.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import edu.uri.wbl.smartglove.ble.enums.BleActions;
import edu.uri.wbl.smartglove.ble.enums.BleStatus;
import edu.uri.wbl.smartglove.ble.models.BluetoothCharacteristicModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothLeModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothServiceModel;
import edu.uri.wbl.smartglove.ble.receivers.BleUpdateReceiver;

/**
 * Created by mcons on 11/12/2017.
 */

public class BleConnectionService extends Service {
    private static String EXTRA_ACTION = "wbl.tex_tronics.extra_action";
    private static String EXTRA_DEVICE = "wbl.tex_tronics.extra_device";
    private static String EXTRA_SERVICE = "wbl.tex_tronics.extra_service";
    private static String EXTRA_CHARACTERISTIC = "wbl.tex_tronics.extra_characteristic";

    public static void START(Context context) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.START.getAction());
        context.startService(intent);
    }

    public static void STOP(Context context) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.STOP.getAction());
        context.startService(intent);
    }

    public static void CONNECT(Context context, BluetoothLeModel bluetoothLeModel) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.CONNECT.getAction());
        intent.putExtra(EXTRA_DEVICE, bluetoothLeModel.getBluetoothDeviceAddress());
        context.startService(intent);
    }

    public static void DISCONNECT(Context context, BluetoothLeModel bluetoothLeModel) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.DISCONNECT.getAction());
        intent.putExtra(EXTRA_DEVICE, bluetoothLeModel.getBluetoothDeviceAddress());
        context.startService(intent);
    }

    public static void DISCOVER_SERVICES(Context context, BluetoothLeModel bluetoothLeModel) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.DISCOVER_SERVICES.getAction());
        intent.putExtra(EXTRA_DEVICE, bluetoothLeModel.getBluetoothDeviceAddress());
        context.startService(intent);
    }

    public static void REQUEST_READ(Context context, BluetoothLeModel bluetoothLeModel, BluetoothServiceModel bluetoothServiceModel, BluetoothCharacteristicModel bluetoothCharacteristicModel) {
        Intent intent = new Intent(context, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.READ_CHARACTERISTIC.getAction());
        intent.putExtra(EXTRA_DEVICE, bluetoothLeModel.getBluetoothDeviceAddress());
        intent.putExtra(EXTRA_SERVICE, bluetoothServiceModel.getUUID().toString());
        intent.putExtra(EXTRA_CHARACTERISTIC, bluetoothCharacteristicModel.getUUID().toString());
        context.startService(intent);
    }

    private final int NOTIFICATION_ID = 34;

    private Context mContext;
    private HashMap<String, BluetoothGatt> mConnectedDevices;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mConnectedDevices = new HashMap<>();

        // Initialize Bluetooth Adapter
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null) {
            Log.d(this.getClass().getSimpleName(), "Failed Initializing BT Adapter!");
            stopSelf();
        }

        startForeground(NOTIFICATION_ID, getNotification(BleStatus.INITIALIZING.getStatus()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent == null || !intent.hasExtra(EXTRA_ACTION))
            return START_NOT_STICKY;

        BleActions action = BleActions.GET(intent.getStringExtra(EXTRA_ACTION));
        if(action == null)
            return START_NOT_STICKY;

        switch (action) {
            case START:
                Log.d(this.getClass().getSimpleName(), "ACTION: Start");
                // Nothing to do
                break;
            case STOP:
                Log.d(this.getClass().getSimpleName(), "ACTION: Stop");
                // Stop service
                // TODO: Gracefully disconnect from all peripherals
                Log.d(this.getClass().getSimpleName(), "Stopping Service");
                stopSelf();
                break;
            case CONNECT:
                Log.d(this.getClass().getSimpleName(), "ACTION: Connect");
                if(!intent.hasExtra(EXTRA_DEVICE)) {
                    Log.d(this.getClass().getSimpleName(), "Invalid Action Intent");
                    return START_NOT_STICKY;
                }
                String address = intent.getStringExtra(EXTRA_DEVICE);
                Log.d(this.getClass().getSimpleName(), "\tDEVICE: " + address);
                connect(address);
                break;
            case DISCONNECT:
                Log.d(this.getClass().getSimpleName(), "ACTION: Disconnect");
                if(!intent.hasExtra(EXTRA_DEVICE)) {
                    Log.d(this.getClass().getSimpleName(), "Invalid Action Intent");
                    return START_NOT_STICKY;
                }
                String disconnectAddress = intent.getStringExtra(EXTRA_DEVICE);
                Log.d(this.getClass().getSimpleName(), "\tDEVICE: " + disconnectAddress);
                disconnect(disconnectAddress);
                break;
            case DISCOVER_SERVICES:
                Log.d(this.getClass().getSimpleName(), "ACTION: Discover Services");
                if(!intent.hasExtra(EXTRA_DEVICE)) {
                    Log.d(this.getClass().getSimpleName(), "Invalid Action Intent");
                    return START_NOT_STICKY;
                }
                String discoverAddress = intent.getStringExtra(EXTRA_DEVICE);
                Log.d(this.getClass().getSimpleName(), "\tDEVICE: " + discoverAddress);
                discoverServices(discoverAddress);
                break;
            case READ_CHARACTERISTIC:
                Log.d(this.getClass().getSimpleName(), "ACTION: Request Read");
                if(!intent.hasExtra(EXTRA_DEVICE) || !intent.hasExtra(EXTRA_CHARACTERISTIC)) {
                    Log.d(this.getClass().getSimpleName(), "Invalid Action Intent");
                    return START_NOT_STICKY;
                }
                requestRead(intent.getStringExtra(EXTRA_DEVICE), UUID.fromString(intent.getStringExtra(EXTRA_SERVICE)), UUID.fromString(intent.getStringExtra(EXTRA_CHARACTERISTIC)));
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
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);
        if(bluetoothGatt != null) {
            Log.d(this.getClass().getSimpleName(), "Connecting to " + bluetoothGatt.getDevice().getName() + "...");
        }
    }

    private void disconnect(String bluetoothDeviceAddress) {
        if(!mConnectedDevices.containsKey(bluetoothDeviceAddress)) {
            Log.d(this.getClass().getSimpleName(), "Not connected to device " + bluetoothDeviceAddress);
            return;
        }

        BluetoothGatt gatt = mConnectedDevices.get(bluetoothDeviceAddress);
        gatt.disconnect();
        Log.d(this.getClass().getSimpleName(), "Disconnecting from " + bluetoothDeviceAddress + "...");
    }

    private void discoverServices(String bluetoothDeviceAddress) {
        if(!mConnectedDevices.containsKey(bluetoothDeviceAddress)) {
            Log.d(this.getClass().getSimpleName(), "Not connected to device " + bluetoothDeviceAddress);
            return;
        }

        BluetoothGatt gatt = mConnectedDevices.get(bluetoothDeviceAddress);
        gatt.discoverServices();
        Log.d(this.getClass().getSimpleName(), "Discovering Services...");
    }

    private void requestRead(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        if(!mConnectedDevices.containsKey(bluetoothDeviceAddress)) {
            Log.d(this.getClass().getSimpleName(), "Not connected to device " + bluetoothDeviceAddress);
            return;
        }

        BluetoothGatt gatt = mConnectedDevices.get(bluetoothDeviceAddress);
        BluetoothGattService service = gatt.getService(serviceUUID);
        if(service == null) {
            Log.d(this.getClass().getSimpleName(), "Could not find Service");
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.d(this.getClass().getSimpleName(), "Could not find Characteristic");
            return;
        }
        gatt.readCharacteristic(characteristic);
        Log.d(this.getClass().getSimpleName(), "Reading Characteristic...");
    }

    private Notification getNotification(String status) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setContentTitle("SmartGlove is Active");
        notificationBuilder.setContentText("Connection Status: " + status);
        notificationBuilder.setSmallIcon(android.R.drawable.presence_online);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setOngoing(true);

        Intent intent = new Intent(mContext, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.STOP);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 347, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        return notificationBuilder.build();
    }

    private void updateNotification(String status) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationBuilder.setContentTitle("SmartGlove is Active");
        notificationBuilder.setContentText("Connection Status: " + status);
        notificationBuilder.setSmallIcon(android.R.drawable.presence_online);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setOngoing(true);

        Intent intent = new Intent(mContext, BleConnectionService.class);
        intent.putExtra(EXTRA_ACTION, BleActions.STOP);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 347, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
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
                Log.d(this.getClass().getSimpleName(), "Bluetooth GATT Error");
                return;
            }

            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
                    Log.d(this.getClass().getSimpleName(), "Connected to " + gatt.getDevice().getName());
                    mConnectedDevices.put(gatt.getDevice().getAddress(), gatt);
                    updateNotification(BleStatus.CONNECTED.getStatus());
                    sendBroadcast(BleUpdateReceiver.UPDATE_CONNECTED, null);
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    Log.d(this.getClass().getSimpleName(), "Disconnected from " + gatt.getDevice().getName());
                    mConnectedDevices.remove(gatt.getDevice().getAddress());
                    if(mConnectedDevices.isEmpty()) {
                        updateNotification(BleStatus.DISCONNECTED.getStatus());
                    }
                    sendBroadcast(BleUpdateReceiver.UPDATE_DISCONNECTED, null);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(this.getClass().getSimpleName(), "Bluetooth GATT Error!");
                return;
            }

            if(gatt.getServices() == null) {
                Log.d(this.getClass().getSimpleName(), "Services returned NULL");
                return;
            }

            if(gatt.getDevice().getAddress() == null) {
                Log.d(this.getClass().getSimpleName(), "Device Address return NULL");
                return;
            }

            Log.d(this.getClass().getSimpleName(), "Services Discovered (Device: " + gatt.getDevice().getAddress() + ")");
            for (BluetoothGattService service : gatt.getServices()) {
                Log.d(this.getClass().getSimpleName(),"\tService: " + service.getUuid());
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.d(this.getClass().getSimpleName(),"\t\tCharacteristic: " + characteristic.getUuid());
                    for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        Log.d(this.getClass().getSimpleName(),"\t\t\tDescriptor: " + descriptor.getUuid());
                    }
                }
            }

            BluetoothLeModel bluetoothLeModel = BluetoothLeModel.CREATE(gatt.getDevice().getAddress(), gatt.getServices());
            sendBroadcast(BleUpdateReceiver.UPDATE_SERVICES_DISCOVERED, bluetoothLeModel);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothLeModel bluetoothLeModel = BluetoothLeModel.CREATE(gatt.getDevice().getAddress(), gatt.getServices());
            bluetoothLeModel.getCharacteristic(characteristic.getUuid()).setValue(characteristic.getValue());
            sendBroadcast(BleUpdateReceiver.UPDATE_CHARACTERISTIC_READ, bluetoothLeModel);
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
