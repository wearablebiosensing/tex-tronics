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

/** ======================================
 *
 *   BluetoothLeConnectionService Class
 *
 *  ======================================
 *
 *  This service allows for our BLE connections to run smoothly as a background service
 *  while the application is writing. Here we provide the means to connect, disconnect, read, and enable
 *  notifications from our BLE devices.
 *
 * Created by mcons on 2/8/2018.
 * @version 1.0
 */

public class BluetoothLeConnectionService extends Service
{
    /**
     * Each String is a tag used to identify when the corresponding BLE update has occured.
     * These are mainly used in this class, the TexTronics Manager, and the Device Exercise
     * Fragment.
     */
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

    /**
     * Tag used to debug connection process
     */
    private static final String DEBUG_LOG_TAG = BluetoothLeConnectionService.class.getSimpleName();

    private IBinder mBinder = new BLEConnectionBinder();
    private BluetoothManager mBluetoothManager;                 // Manages Bluetooth connections
    private BluetoothAdapter mBluetoothAdapter;                 // Adapter used to connect to devices
    private HashMap<String,BluetoothGatt> mBluetoothGattList;   // List of gatt devices to connect to

    /** BluetoothGattCallback
     *
     * For our purposes, our gatt callback is used to detect a change in connection
     * with any of our devices.
     *
     */
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback()
    {
        /** onConnectionStateChange()
         *
         * Handles a change in connection
         *
         * @param gatt              -Device that has a change in state
         * @param status            -Whether or not the change is successful
         * @param newState          -The new state, either being connected or disconnected
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            // Error occurs when the status is unsuccessful
            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Bluetooth Gatt Error (" + status + ")");
                return;
            }
            // Handles the change in connection
            switch (newState)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    log("Connected to " + gatt.getDevice().getName());
                    // Put the newly connected device in our list of connected gatts
                    mBluetoothGattList.put(gatt.getDevice().getAddress(), gatt);
                    // Broadcast to TexTronics manager that we've connected to this device
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_STATE_CONNECTED));
                    // Send a broadcast looking to read data from this device
                    String[] deviceInfo = {gatt.getDevice().getName(), gatt.getDevice().getAddress()};
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_DEVICE_INFO_READ, deviceInfo));
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    log("Disconnected from " + gatt.getDevice().getName());
                    // Remove the gatt from our connected gatt list
                    mBluetoothGattList.remove(gatt.getDevice().getAddress());
                    // Notify the TexTronics manager that we've disconnected from this device
                    sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_STATE_DISCONNECTED));
                    gatt.close();
                    break;
            }
        }

        /** onServicesDiscovered()
         *
         * Discovers the services in our bluetooth gatt, which are sets of similar
         * characteristics. It then broadcasts that our services have been discovered
         *
         * @param gatt              -Gatt which services have just been discovered
         * @param status            -Determines whether or not the gatt is successful
         */
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

        /** The following are simple operation notifications that are launched when
         * we read and write from a characteristic, which is a component
         * inside of a bluetooth gatt service that holds a value. In our case, this value could be the data we
         * receive from our devices.
         *
         * We simply log our information to the console and broadcast the change
         *
         * @param gatt                  -Gatt device that notifies an operation
         * @param characteristic        -Characteristic that triggered event
         * @param status                -Status of the gatt
         */

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
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            log("Characteristic Updated (" + characteristic.getUuid() + ")");
            /**
             * mac address
             * action
             * byte array (data packet from device)
             * uuid
             */
            sendBroadcast(generateIntent(gatt.getDevice().getAddress(), GATT_CHARACTERISTIC_NOTIFY, characteristic.getValue(), characteristic.getUuid()));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        /** onDescriptorWrite()
         *
         * We need to write to the notification descriptor in order to be notified when our data
         * characteristics change.
         *
         * @param gatt                  -Gatt device that notifies an operation
         * @param descriptor            -Descriptor that triggered event
         * @param status                -Status of the gatt
         */
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

        /** Unchanged methods that are a part of implementation
         *
         * @param gatt
         * @param status
         */

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

    /** onCreate()
     *
     * When this service is created, we just need to set up our Bluetooth manager, adapter
     * and gatt list which will store up to 7 bluetooth devices.
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        log("BLEConnectionService Created");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            log("Stopping Service (Device does not support BLE)");
            stopSelf();
        }

        // Get our system's Bluetooth manager
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if(mBluetoothManager == null) {
            log("Stopping Service (Could not retrieve Bluetooth Manager");
            stopSelf();
        }
        // Get our system's Bluetooth adapter that allows us to connect to devices
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


    /** connect()
     *
     * Adds a new gatt to our list if we can connect to another device. We use our bluetooth adapter
     * to connect to the new device.
     *
     * @param bluetoothDeviceAddress        -MAC address of device to connect to
     * @return                          Whether or not we have connected to the new device
     */
    public boolean connect(String bluetoothDeviceAddress)
    {
        // Make sure we have a functioning bluetooth adapter
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        if(mBluetoothGattList.size() >= 7)
        {
            // Too many devices connected
            log("Too many Devices connected");
            return false;
        }

        // Get the device from our bluetooth adapter using its MAC address
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);
        if (bluetoothDevice == null) {
            log("Could not find device");
            return false;
        }

        // Connect to the gatt, and send with it our callback
        BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(this, true, mBluetoothGattCallback);
        if (bluetoothGatt == null) {
            log("Could not connect to device's Gatt Server");
            return false;
        }

        // If it hasn't returned false at this point, we can assume that the connection was successful
        log("Establishing connection to " + bluetoothDeviceAddress + "...");
        sendBroadcast(generateIntent(bluetoothDeviceAddress, GATT_STATE_CONNECTING));
        return true;
    }

    /** disconnect()
     *
     * Disconnects from the input device. We simply get the device from our list by looking for its
     * MAC address, then we disconnect from it.
     *
     * @param bluetoothDeviceAddress        -MAC address of device to disconnect from
     * @return                          Whether or not we have disconnected from the input device
     */
    public boolean disconnect(String bluetoothDeviceAddress) {
        if (mBluetoothAdapter == null) {
            log("Bluetooth Adapter not initialized");
            return false;
        }

        // Get the device to disconnect to
        BluetoothGatt bluetoothGatt = mBluetoothGattList.get(bluetoothDeviceAddress);
        if (bluetoothGatt == null) {
            log("Bluetooth Device's Gatt Server not Connected");
            return false;
        }

        // Disconnect, then broadcast
        bluetoothGatt.disconnect();
        log("Disconnecting from " + bluetoothDeviceAddress + "...");
        sendBroadcast(generateIntent(bluetoothDeviceAddress, GATT_STATE_DISCONNECTING));
        return true;
    }

    /** discoverServices()
     *
     * Looks for services in our newly connected gatt. We need to discover the services in
     * order to read from their characteristics.
     *
     * @param bluetoothDeviceAddress        -MAC address of device to discover services from
     * @return                          Whether or not services have been discovered. Triggers onServicesDiscovered()
     */
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

    // Not currently used. We get our info from the characteristics through their notifications, not through explicitly reading
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

    /** writeCharacteristic()
     *
     * Writes a value to a characteristic. In our case, it will only be used to write a value to the
     * TX characteristic.
     *
     * @param bluetoothDeviceAddress        -MAC address of device
     * @param characteristic                -TX Characteristic
     * @param value                         -Value to write to the characteristic
     * @return                          Whether or not we wrote to the characteristic
     */
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

    /** getService()
     *
     * Gets a particular service from a bluetooth gatt. This will be used so we can
     * access a characteristic inside of this service
     *
     * @param bluetoothDeviceAddress        -MAC address of the bluetooth gatt
     * @param serviceUUID                   -UUID or identification name of the service
     * @return                          The service that we were looking for
     */
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

    /** getCharacteristic()
     *
     * Gets a characteristic based on the UUIDs (identifiers) that we pass through the method. This characteristic
     * needs to be inside of the correct service that the bluetooth gatt contains
     *
     * @param bluetoothDeviceAddress            -MAC address of the gatt
     * @param serviceUUID                       -Service identifier that allows us to get service
     * @param characteristicUUID                -Characteristic identifier that allows us to get characteristic
     * @return                              The characteristic that we were looking for
     */
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

    /** enableNotifications()
     *
     * Enables a characteristic to notify the application when its value has changed. This is the key feature we
     * use to collect real-time data from our devices, meaning we enable notifications for every characteristic that
     * stores collectable sensor information.
     *
     * @param bluetoothDeviceAddress            -MAC address of bluetooth gatt
     * @param characteristic                    -Characteristic that we want to enable notifications for
     * @return                              Whether or not notifications have been enabled
     */
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

        /* First, we need to enable notifications, then we need to write to the gatt's notification
         * descriptor in order to make sure that the notifications have been registered.
         */
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

    // We never want to disable notifications once enabled
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

    /** ==========================================
     *
     *              Intent Generation
     *
     *      The following methods generate and send
     *   intents, which basically sends an operation
     *   to another service.
     *
     *  ==========================================
     */

    /** Intent used to broadcast the state of a bluetooth gatt
     *
     * @param bluetoothDeviceAddress    -MAC address of the bluetooth gatt
     * @param action                    -String identifying the state of the gatt
     * @return
     */
    private Intent generateIntent(String bluetoothDeviceAddress, String action) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        return intent;
    }

    // Not currently in use
    private Intent generateIntent(String bluetoothDeviceAddress, String action, byte[] data) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        return intent;
    }

    /** Sends broadcast to TexTronics Manager after a new gatt has been connected
     *
     * @param bluetoothDeviceAddress
     * @param action
     * @param data
     * @return
     */
    private Intent generateIntent(String bluetoothDeviceAddress, String action, String[] data) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        return intent;
    }

    /** Sends broadcast to both read from and enable notifications for a characteristic
     *
     * @param bluetoothDeviceAddress    -MAC address of bluetooth gatt
     * @param action                    -Action performed, either READ or NOTIFY
     * @param data                      -Data packet that contains readable information
     * @param uuid                      -Identifier of the characteristic
     * @return
     */
    private Intent generateIntent(String bluetoothDeviceAddress, String action, byte[] data, UUID uuid) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(INTENT_DEVICE, bluetoothDeviceAddress);
        intent.putExtra(INTENT_EXTRA, action);
        intent.putExtra(INTENT_DATA, data);
        intent.putExtra(INTENT_CHARACTERISTIC, uuid.toString());
        return intent;
    }
}
