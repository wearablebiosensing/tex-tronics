package edu.uri.wbl.tex_tronics.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.ble.GattAttributes.GattDescriptors;

/**
 * This Service is responsible for serving as a generic abstraction layer between the user and the
 * Android BLE API. This allows the user to quickly develop BLE enabled applications without having
 * to learn the details of BLE and/or spend the time debugging the nuances of the BLE API. Once a
 * component binds to this Service, it can interact with the desired BLE device via calls to the
 * public methods made available by this Service. In order to receive information back from the BLE
 * device, the caller must implement a BleUpdateReceiver. The BleUpdateReceiver will provide the
 * caller with connection updates as well as data received from the desired BLE device.
 *
 * @author Matthew Constant, Andrew Peltier, Nick Constant
 * @version 1.0, 12/04/2017
 */

public class BluetoothLeService extends Service {
    private final String TAG = "BluetoothLeService";
    private final IBinder mBinder = new BluetoothLeBinder();

    private Context mContext;
    private HashMap<String, BluetoothGatt> mConnectedDevices;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mConnectedDevices = new HashMap<>();
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null) {
            Log.e(TAG, "Could not obtain Bluetooth Manager");
            stopSelf();
            return;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null) {
            Log.e(TAG, "Could not obtain BT Adapter");
            stopSelf();
            return;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;        // Restart Service if killed by OS
    }

    @Override
    public IBinder onBind(Intent intent) {
        // IBinder allows other processes to call public methods in this class (IPC)
        return mBinder;
    }

    @Override
    public void onDestroy() {
        // Disconnect from all connected devices
        if(mConnectedDevices != null) {
            for(BluetoothGatt gatt : mConnectedDevices.values()) {
                gatt.disconnect();
                gatt.close();
            }
        }

        super.onDestroy();
    }

    /**
     * This method returns the nearby device corresponding to the given Bluetooth Device Address.
     * @param bluetoothDeviceAddress Nearby BLEDevice to retrieve
     * @return Bluetooth Device of requested BLE device, or null if invalid address
     */
    public BluetoothDevice getDevice(String bluetoothDeviceAddress) {
        if(!BluetoothAdapter.checkBluetoothAddress(bluetoothDeviceAddress)) {
            return null;
        }

        return mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
    }

    /**
     * This method returns the GATT of the corresponding BLE device. The Device must already have
     * been connected via a successful call to connect().
     * @param bluetoothDeviceAddress BD_ADDR of connected BLE Device
     * @return BluetoothGatt of BLE device, null if not connected
     */
    public BluetoothGatt getGatt(String bluetoothDeviceAddress) {
        if(mConnectedDevices.containsKey(bluetoothDeviceAddress)) {
            return mConnectedDevices.get(bluetoothDeviceAddress);
        } else {
            Log.w(TAG, "Requested Device not Connected");
            return null;
        }
    }

    /**
     * This method sends a request to the BluetoothDevice to connect. The Caller must wait for
     * a BLE Update (via the BleUpdateReceiver) to get a response from this request. If the
     * device is already connected (contained in the mConnectedDevices HashMap), no update will
     * be sent.
     *
     * NOTE: The Android BLE API has a known issue with the BluetoothDevice.connectGatt() method
     * where the autoConnect parameter must be true for some devices, false for some and can be
     * either true or false for all others. The only way to find out for the particular device
     * (BT radio, not phone model) is to try one and then the other if the first fails.
     *
     * @param device The BLE device to connect to.
     */
    public void connect(BluetoothDevice device) {
        if(device == null) {
            Log.w(TAG, "Device Null");
            return;
        }
        if(mConnectedDevices.containsKey(device.getAddress())) {
            Log.w(TAG, "Device Already Connected");
            return;
        }

        device.connectGatt(mContext, false, mBluetoothGattCallback);
    }

    /**
     * This method sends a request to the connected BLE device to discover its services. The Caller
     * must wait for a BLE Update (via the BleUpdateReceiver) to get a response from this request.
     * If the BLE device is not already connected (contained in the mConnectedDevices HashMap), no
     * update will be sent.
     * @param gatt The GATT to discover services from.
     */
    public void discoverServices(BluetoothGatt gatt) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        gatt.discoverServices();
    }

    /**
     * This method requests to update the characteristic value from the specified GATT. The Caller
     * must wait for a BLE Update (via the BleUpdateReceiver) to get a response from this request.
     * If the BLE device is not already connected (contained in the mConnectedDevices HashMap), no
     * update will be sent.
     * @param gatt The GATT to update characteristic value from
     * @param characteristic The Characteristic containing the value
     */
    public void readCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        gatt.readCharacteristic(characteristic);
    }

    /**
     * This method tells the specified GATT that this device has changed the Characteristic's value.
     * The Caller must wait for a BLE Update (via the BleUpdateReceiver) to get a response from this
     * request. If the BLE device is not already connected (contained in the mConnectedDevices
     * HashMap), no update will be sent.
     * @param gatt The GATT to update
     * @param characteristic The Characteristic containing the value
     */
    public void writeCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        characteristic.setValue(value);
        gatt.writeCharacteristic(characteristic);
    }

    /**
     * This method enables notifications (automatically reads characteristic when changed) on the
     * specified characteristic. No response is generated from this request, however when this
     * device is notified that the characteristic has been updated, it will send an update (via
     * BLE Update Receiver).
     * @param gatt The BLE device
     * @param characteristic The characteristic to be notified of
     */
    public void enableNotify(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        gatt.setCharacteristicNotification(characteristic, true);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattDescriptors.NOTIFICATION_DESCRIPTOR));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }
    }

    /**
     * This method disables notifications for the specified characteristic. If not currently enabled,
     * this method does nothing.
     * @param gatt The BLE device
     * @param characteristic The characteristic to stop being notified from
     */
    public void disableNotify(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        gatt.setCharacteristicNotification(characteristic, false);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattDescriptors.NOTIFICATION_DESCRIPTOR));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
    }

    /**
     * This method disconnects from the GATT specified. If the GATT is not currently connected,
     * this method does nothing.
     * @param gatt The BLE device
     */
    public void disconnect(BluetoothGatt gatt) {
        if(gatt == null) {
            Log.w(TAG, "GATT null");
            return;
        }
        if(!mConnectedDevices.containsKey(gatt.getDevice().getAddress())) {
            Log.w(TAG, "GATT Not Connected");
            return;
        }

        gatt.disconnect();
        gatt.close();
    }

    /**
     * This method generates the Update Packet to be sent to all listening BLE Update Receivers.
     * This method should be used for all updates that do not require the listener to know the
     * specific Characteristic (if applicable) being updated (for example, CONNECTED or possibly
     * CHARACTERISTIC_WRITTEN).
     * @param update The BleUpdate to be sent
     * @param bluetoothDeviceAddress The address of the corresponding device.
     */
    private void sendUpdate(BleUpdate update, String bluetoothDeviceAddress) {
        Intent intent = new Intent(BleUpdateReceiver.INTENT_FILTER.getAction(0));
        intent.putExtra(BleUpdateReceiver.EXTRA_UPDATE, update);    // Serializable Extra
        intent.putExtra(BleUpdateReceiver.EXTRA_DEVICE, bluetoothDeviceAddress);
        sendBroadcast(intent);
    }

    /**
     * This method generates the Update Packet to be sent to all listening BLE Update Receivers.
     * This method should be used for all updates that require the listener to know the specific
     * charactistic that is being updated (for example, CHARACTERISTIC_READ or CHARACTERISTIC_UPDATED).
     * @param update The BleUpdate to be sent
     * @param bluetoothDeviceAddress The address of the corresponding BLE device.
     * @param characteristicUuid The Characteristic being updated
     * @param value The value of the characteristic
     */
    private void sendUpdate(BleUpdate update, String bluetoothDeviceAddress, String characteristicUuid, byte[] value) {
        Intent intent = new Intent(BleUpdateReceiver.INTENT_FILTER.getAction(0));
        intent.putExtra(BleUpdateReceiver.EXTRA_UPDATE, update);    // Serializable Extra
        intent.putExtra(BleUpdateReceiver.EXTRA_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(BleUpdateReceiver.EXTRA_CHARACTERISTIC, characteristicUuid);
        intent.putExtra(BleUpdateReceiver.EXTRA_VALUE, value);
        sendBroadcast(intent);
    }

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "GATT Error: " + status);
                return;
            }

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "Connected to GATT");
                    mConnectedDevices.put(gatt.getDevice().getAddress(), gatt);
                    sendUpdate(BleUpdate.CONNECTED, gatt.getDevice().getAddress());
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    mConnectedDevices.remove(gatt.getDevice().getAddress());
                    sendUpdate(BleUpdate.DISCONNECTED, gatt.getDevice().getAddress());
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "GATT Error: " + status);
                return;
            }

            sendUpdate(BleUpdate.SERVICES_DISCOVERED, gatt.getDevice().getAddress());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "GATT Error: " + status);
                return;
            }

            sendUpdate(BleUpdate.CHARACTERISTIC_READ, gatt.getDevice().getAddress(), characteristic.getUuid().toString(), characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "GATT Error: " + status);
                return;
            }

            sendUpdate(BleUpdate.CHARACTERISTIC_WRITTEN, gatt.getDevice().getAddress(), characteristic.getUuid().toString(), characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            sendUpdate(BleUpdate.CHARACTERISTIC_UPDATED, gatt.getDevice().getAddress(), characteristic.getUuid().toString(), characteristic.getValue());
        }
    };

    public class BluetoothLeBinder extends Binder {
        public BluetoothLeService getService() {
            // Allows for IPC (taken care of with onBind())
            return BluetoothLeService.this;
        }
    }
}
