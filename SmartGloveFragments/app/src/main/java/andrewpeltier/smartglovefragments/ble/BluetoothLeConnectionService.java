package andrewpeltier.smartglovefragments.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mcons on 2/8/2018.
 */

public class BluetoothLeConnectionService extends Service {
    public static final String INTENT_DEVICE = "uri.egr.biosensign.intent_device";
    public static final String INTENT_EXTRA = "uri.egr.biosensing.intent_extra";
    public static final String INTENT_DATA = "uri.egr.biosensing.intent_data";
    public static final String INTENT_CHARACTERISTIC = "uri.egr.biosensing.intent_characteristic";
    public static final String INTENT_FILTER_STRING = "uri.egr.biosensing.flexglove.ble_updates";
    public static final String GATT_STATE_CONNECTED = "gatt_state_connected";
    public static final String GATT_STATE_CONNECTING = "gatt_state_connecting";
    public static final String GATT_STATE_DISCONNECTED = "gatt_state_disconnected";
    public static final String GATT_STATE_DISCONNECTING = "gatt_state_disconnecting";
    public static final String GATT_DISCOVERED_SERVICES = "gatt_discovered_services";
    public static final String GATT_CHARACTERISTIC_READ = "gatt_characteristic_read";
    public static final String GATT_CHARACTERISTIC_NOTIFY = "gatt_characteristic_notify";
    public static final String GATT_DESCRIPTOR_READ = "gatt_descriptor_read";
    public static final String GATT_DESCRIPTOR_WRITE = "gatt_descriptor_write";
    public static final String GATT_NOTIFICATION_TOGGLED = "gatt_notification_toggled";
    public static final String GATT_DEVICE_INFO_READ = "gatt_device_info_read";
    public static final String GATT_INFORMATION_STORE = "gatt_information_store";

    private static final String DEBUG_LOG_TAG = BluetoothLeConnectionService.class.getSimpleName();

    private IBinder mBinder = new BLEConnectionBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String,BluetoothGatt> mBluetoothGattList;

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Bluetooth Gatt Error (" + status + ")");
                return;
            }

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    log("Connected to " + gatt.getDevice().getName());
                    mBluetoothGattList.put(gatt.getDevice().getAddress(), gatt);
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_STATE_CONNECTED));
                    String[] deviceInfo = {gatt.getDevice().getName(), gatt.getDevice().getAddress()};

                    // Two devices
//                    if(mBluetoothGattList.size() < 2)
//                        connect();
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_DEVICE_INFO_READ, deviceInfo));
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    log("Disconnected from " + gatt.getDevice().getName());
                    mBluetoothGattList.remove(gatt.getDevice().getAddress());
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_STATE_DISCONNECTED));
                    gatt.close();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Services Discovered");
                for (BluetoothGattService service : gatt.getServices()) {
                    log("\t" + service.getUuid());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        log("\t\t" + characteristic.getUuid() + " Permissions: " + characteristic.getPermissions());
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                            log("\t\t\t" + descriptor.getUuid() + " Permissions: " + descriptor.getPermissions());
                        }
                    }
                }
                sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_DISCOVERED_SERVICES));
            } else {
                log("Bluetooth Gatt Error (" + status + ")");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic Read (" + characteristic.getUuid() + ")");
            }
            sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_CHARACTERISTIC_READ, characteristic.getValue(), characteristic.getUuid()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic Written (" + characteristic.getUuid() + ")");
            } else {
                log("Error Writing Characteristic (" + characteristic.getUuid() + ")");
            }
        }

        // This is where it characteristic gets updated. You need to figure how to get the value over to manager
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            log("Characteristic Updated (" + characteristic.getUuid() + ")");
            sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_CHARACTERISTIC_NOTIFY, characteristic.getValue(), characteristic.getUuid()));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                log("Descriptor Written (" + descriptor.getUuid() + ")");
                sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_DESCRIPTOR_WRITE));
                if (descriptor.getUuid().equals(GattDescriptors.NOTIFICATION_DESCRIPTOR)) {
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_NOTIFICATION_TOGGLED));
                }
            } else {
                log("Error Writing Descriptor (" + descriptor.getUuid() + ")");
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            log("Device: " + gatt.getDevice().getName() + "\tRSSI: " + rssi);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        log("BLEConnectionService Created");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            log("Stopping Service (Device does not support BLE)");
            stopSelf();
        }

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if(mBluetoothManager == null) {
            log("Stopping Service (Could not retrieve Bluetooth Manager");
            stopSelf();
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            log("Stopping Service (Could not retrieve Bluetooth Adapter");
            stopSelf();
        } else {
            log("Successfully Initialized Bluetooth Adapter");
        }
        //Initialize Hash Map of Connected Gatt Servers (Currently, only 7 BLE peripheral devices can be connected to a single Central device at a time
        mBluetoothGattList = new HashMap<>(7);
    }

    @Override
    public IBinder onBind(Intent intent) {
        log("Service Bound");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("BLEConnectionService Destroyed");
    }

    public class BLEConnectionBinder extends Binder {
        public BluetoothLeConnectionService getService() {
            return BluetoothLeConnectionService.this;
        }
    }

    private void log(String message) {
        Log.d(DEBUG_LOG_TAG, message);
    }


    public boolean connect(String bluetoothDeviceAddress) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        if(mBluetoothGattList.size() >= 7) {
            // Too many devices connected
            log("Too many Devices connected");
            return false;
        }

        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
        if (bluetoothDevice == null) {
            log("Could not find device");
            return false;
        }
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(this, true, mBluetoothGattCallback);
        if (bluetoothGatt == null) {
            log("Could not connect to device's Gatt Server");
            return false;
        }
        log("Establishing connection to " + bluetoothDeviceAddress + "...");
        sendBroadcast(generateIntent(bluetoothDeviceAddress, GATT_STATE_CONNECTING));
        return true;
    }

    public boolean disconnect(String bluetoothDeviceAddress) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        bluetoothGatt.disconnect();
        log("Disconnecting from " + bluetoothDeviceAddress + "...");
        sendBroadcast(generateIntent(bluetoothDeviceAddress, GATT_STATE_DISCONNECTING));
        return true;
    }

    public boolean discoverServices(String bluetoothDeviceAddress)
    {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }
        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        log("Discovering Services on " + bluetoothDeviceAddress + "...");
        return bluetoothGatt.discoverServices();
    }

    public boolean readCharacteristic(String bluetoothDeviceAddress, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        if (characteristic == null) {
            log("Invalid Characteristic");
            return false;
        }

        log("Reading Characteristic (" + characteristic.getUuid() + ") on " + bluetoothDeviceAddress + "...");
        return bluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(String bluetoothDeviceAddress, BluetoothGattCharacteristic characteristic, byte[] value) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        if (characteristic == null) {
            log("Invalid Characteristic");
            return false;
        }

        log("Writing Characteristic (" + characteristic.getUuid() + ")...");
        if (!characteristic.setValue(value)) {
            log("Error setting Characteristic Value");
            return false;
        }
        return bluetoothGatt.writeCharacteristic(characteristic);
    }

    public BluetoothGattService getService(String bluetoothDeviceAddress, UUID serviceUUID) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return null;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return null;
        }

        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);
        if (service == null){
            log("Could not find Service");
            return null;
        }
        return service;
    }

    public BluetoothGattCharacteristic getCharacteristic(String bluetoothDeviceAddress, UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGattService service = getService(bluetoothDeviceAddress, serviceUUID);
        if (service == null) {
            return null;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            log("Could not find Characteristic");
            return null;
        }
        return characteristic;
    }

    public boolean enableNotifications(String bluetoothDeviceAddress, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        if (characteristic == null) {
            log("Invalid Characteristic");
            return false;
        }

        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattDescriptors.NOTIFICATION_DESCRIPTOR);
        if (descriptor == null) {
            log("Characteristic does not have Notification Descriptor");
            return false;
        } else {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!bluetoothGatt.writeDescriptor(descriptor)) {
                log("Could not write to descriptor");
                return false;
            } else {
                log("Writing to descriptor...");
            }
        }
        return true;
    }

    public boolean disableNotifications(String bluetoothDeviceAddress, BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        if (characteristic == null) {
            log("Invalid Characteristic");
            return false;
        }

        bluetoothGatt.setCharacteristicNotification(characteristic, false);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattDescriptors.NOTIFICATION_DESCRIPTOR);
        if (descriptor == null) {
            log("Characteristic does not have Notification Descriptor");
            return false;
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            if (!bluetoothGatt.writeDescriptor(descriptor)) {
                log("Could not write to descriptor");
                return false;
            } else {
                log("Writing to descriptor...");
            }
        }
        return true;
    }

    private Intent generateIntent(String bluetoothDeviceAddress, String action) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        return intent;
    }

    private Intent generateIntent(String bluetoothDeviceAddress, String action, byte[] data) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        return intent;
    }

    private Intent generateIntent(String bluetoothDeviceAddress, String action, String[] data) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        return intent;
    }

    private Intent generateIntent(String bluetoothDeviceAddress, String action, byte[] data, UUID uuid) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        intent.putExtra(INTENT_CHARACTERISTIC, uuid.toString());
        return intent;
    }
}
