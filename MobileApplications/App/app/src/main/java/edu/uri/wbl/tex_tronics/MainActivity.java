package edu.uri.wbl.tex_tronics;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.ble.GattAttributes.GattCharacteristics;
import edu.uri.wbl.tex_tronics.ble.GattAttributes.GattDevices;
import edu.uri.wbl.tex_tronics.ble.GattAttributes.GattServices;
import edu.uri.wbl.tex_tronics.ble.BleUpdate;
import edu.uri.wbl.tex_tronics.ble.BleUpdateReceiver;
import edu.uri.wbl.tex_tronics.ble.BluetoothLeService;
import edu.uri.wbl.tex_tronics.io.DataLogService;
import edu.uri.wbl.tex_tronics.mqtt.MqttManager;
import edu.uri.wbl.tex_tronics.mqtt.MqttManagerListener;
import edu.uri.wbl.tex_tronics.mqtt.MqttSettings;

/**
 * This Activity is meant to serve as a demostration to illustrate to the application user the
 * features made available by this app, as well as to contributing developers to understand how to
 * use the BluetoothLeService, MqttManager, and other Services/components contains in this library.
 * Specifically, this application connects to multiple Tex-Tronics products (SmartGloves and
 * SmartSocks), streams sensor data from each of them and relays this information to the cloud
 * via an MQTT connection. Since this demo does not include a BLE scanner, device addresses must
 * be manually inputted to the GattDevices class (as a String).
 *
 * @author Matthew Constant, Andrew Peltier, Nathan Mensah, Nick Constant
 * @version 1.0, 12/04/2017
 * @see <a href="https://github.com/wearablebiosensing/tex-tronics">Tex-Tronics GitHub Repository</a>
 * @see <a href="https://www.hivemq.com/mqtt-essentials/">MQTT Essentials</a>
 * @see <a href="https://www.amazon.com/Bluetooth-Low-Energy-Developers-Handbook/dp/013288836X">Bluetooth Low Energy: The Developer's Handbook</a>
 */

public class MainActivity extends AppCompatActivity implements MqttManagerListener {
    /**
     * These are the permissions required by this application to run.
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // Store BLE Data as CSV file locally
            Manifest.permission.BLUETOOTH                   // Use Android's BLE API
    };


    // MQTT Constants
    private static final String     MQTT_HOST = "fog.wbl.cloud";            // Host Name
    private static final int        MQTT_PORT = 1883;                       // Port Number
    private static final String     MQTT_USERNAME = "kabuki";               // Username
    private static final String     MQTT_PASSWORD = "vfghjkm4774";          // Password
    private static final boolean    MQTT_CLEAN_SESSION = false;             // Clean Session?
    private static final boolean    MQTT_SSL = false;                       // SSL Connection?
    private static final int        MQTT_QOS = 1;                           // QOS Level
    private static final String     MQTT_TOPIC = "wbl/smartglove/kiya";     // Topic to Publish to

    // CSV Logging Constants
    private static final String     HEADER = "date,time,x,y,z";
    private static final File       ACCEL_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/accel.csv");
    private static final File       GYRO_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/gyro.csv");
    private static final File       MAG_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/mag.csv");

    private Context mContext;
    private Button mLeftHandConnectBtn, mLeftLegConnectBtn, mRightHandConnectBtn, mRightLegConnectBtn;
    private Button mLeftHandDisconnectBtn, mLeftLegDisconnectBtn, mRightHandDisconnectBtn, mRightLegDisconnectBtn;

    private MqttManager mMqttManager;                   // Handles MQTT Connections
    private BluetoothLeService mBluetoothLeService;     // Handles BLE Connections
    private boolean mBound;                             // Must be true to use mBluetoothLeService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        // Connect to MQTT
        mMqttManager = MqttManager.getInstance(mContext);
        if (MqttSettings.getInstance(mContext).isConnected()) {
            mMqttManager.connectFromSavedSettings(this);
        } else {
            mMqttManager.connect(
                    mContext,
                    MQTT_HOST,                  // wbl
                    MQTT_PORT,                  // 1880
                    MQTT_USERNAME,              // kabuki
                    MQTT_PASSWORD,
                    MQTT_CLEAN_SESSION,         // false
                    MQTT_SSL                    // false
            );
        }
        mMqttManager.setListener(this);

        // Wire up UI
        mLeftHandConnectBtn = findViewById(R.id.connect_lh_btn);
        mLeftHandDisconnectBtn = findViewById(R.id.disconnect_lh_btn);
        mRightHandConnectBtn = findViewById(R.id.connect_rh_btn);
        mRightHandDisconnectBtn = findViewById(R.id.disconnect_rh_btn);
        mLeftLegConnectBtn = findViewById(R.id.connect_ll_btn);
        mLeftLegDisconnectBtn = findViewById(R.id.disconnect_ll_btn);
        mRightLegConnectBtn = findViewById(R.id.connect_rl_btn);
        mRightLegDisconnectBtn = findViewById(R.id.disconnect_rl_btn);

        mLeftHandConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    BluetoothDevice bluetoothDevice = mBluetoothLeService.getDevice(GattDevices.SMART_GLOVE_LEFT);
                    mBluetoothLeService.connect(bluetoothDevice);
                }
            }
        });

        mLeftHandDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    BluetoothGatt gatt = mBluetoothLeService.getGatt(GattDevices.SMART_GLOVE_LEFT);
                    mBluetoothLeService.disconnect(gatt);
                }
            }
        });

        mRightHandConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    BluetoothDevice bluetoothDevice = mBluetoothLeService.getDevice(GattDevices.SMART_GLOVE_RIGHT);
                    mBluetoothLeService.connect(bluetoothDevice);
                }
            }
        });

        mRightHandDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    BluetoothGatt gatt = mBluetoothLeService.getGatt(GattDevices.SMART_GLOVE_RIGHT);
                    mBluetoothLeService.disconnect(gatt);
                }
            }
        });

        mLeftLegConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mLeftLegDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRightLegConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRightLegDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to mBluetoothLeService, must be bound to use
        Intent intent = new Intent(mContext, BluetoothLeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // Register BleUpdateReceiver to receive BLE related updates
        registerReceiver(mBleUpdateReceiver, BleUpdateReceiver.INTENT_FILTER);

        // Check and Request Required Permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, 4037);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBleUpdateReceiver);

        unbindService(mConnection);
        mBound = false;
    }

    /**
     * This method publishes a message to the topic specified above. The MQTT Manager
     * must have already connected to use this method.
     *
     * @param data String to be published via MQTT
     */
    private void mqttPublish(String data){
        mMqttManager.publish(MQTT_TOPIC, data, MQTT_QOS);
    }

    /**
     * This Broadcast Receiver will receive all updates related to BLE. This includes devices
     * connecting/disconnecting and characteristics being read/written.
     */
    private BleUpdateReceiver mBleUpdateReceiver = new BleUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Every Update will include the Device Address and Corresponding Update
            String bd_addr = intent.getStringExtra(EXTRA_DEVICE);
            BleUpdate update = (BleUpdate) intent.getSerializableExtra(BleUpdateReceiver.EXTRA_UPDATE);

            //The BluetoothLeService provides a method to get a connected GATT from its Address
            BluetoothGatt gatt = mBluetoothLeService.getGatt(bd_addr);

            switch (update) {
                case CONNECTED:
                    // A BLE Device has been Connected
                    Toast.makeText(mContext, "Connected to " + gatt.getDevice().getName(), Toast.LENGTH_LONG).show();
                    mBluetoothLeService.discoverServices(gatt);
                    break;
                case DISCONNECTED:
                    // A BLE Device has been disconnected
                    Toast.makeText(mContext, "Disconnected from " + bd_addr, Toast.LENGTH_LONG).show();
                    break;
                case SERVICES_DISCOVERED:
                    // A Connected BLE Device's Services have been queried and are now available to read/use
                    Toast.makeText(mContext, "Discovered Services on " + gatt.getDevice().getName(), Toast.LENGTH_LONG).show();

                    // Enable Notifications to the Data Ready characteristic
                    BluetoothGattService smartService = gatt.getService(UUID.fromString(GattServices.SMART));
                    BluetoothGattCharacteristic dataCharacteristic =
                            smartService.getCharacteristic(UUID.fromString(GattCharacteristics.DATA_READY));
                    mBluetoothLeService.enableNotify(gatt, dataCharacteristic);

                    break;
                case CHARACTERISTIC_READ:
                    // A Connected BLE Device has returned the value of a characteristic

                    // This update will include two additional extras: the UUID and value of the characteristic
                    String charUuid = intent.getStringExtra(EXTRA_CHARACTERISTIC);
                    byte[] value = intent.getByteArrayExtra(EXTRA_VALUE);

                    BluetoothGattService imuService = gatt.getService(UUID.fromString(GattServices.IMU));

                    switch (charUuid) {
                        case GattCharacteristics.ACCEL:
                            int accel_x = value[1] | value[2] << 8;
                            int accel_y = value[3] | value[4] << 8;
                            int accel_z = value[5] | value[6] << 8;
                            String accel_data = accel_x + "," + accel_y + "," + accel_z;
                            DataLogService.log(mContext, ACCEL_FILE, accel_data, HEADER);


                            BluetoothGattCharacteristic gyroCharacteristic =
                                    imuService.getCharacteristic(UUID.fromString(GattCharacteristics.GYRO));
                            mBluetoothLeService.readCharacteristic(gatt, gyroCharacteristic);
                            break;
                        case GattCharacteristics.GYRO:
                            int gyro_x = value[1] | value[2] << 8;
                            int gyro_y = value[3] | value[4] << 8;
                            int gyro_z = value[5] | value[6] << 8;
                            String gyro_data = gyro_x + "," + gyro_y + "," + gyro_z;
                            DataLogService.log(mContext, GYRO_FILE, gyro_data, HEADER);

                            BluetoothGattCharacteristic magCharacteristic =
                                    imuService.getCharacteristic(UUID.fromString(GattCharacteristics.MAG));
                            mBluetoothLeService.readCharacteristic(gatt, magCharacteristic);
                            break;
                        case GattCharacteristics.MAG:
                            int mag_x = value[1] | value[2] << 8;
                            int mag_y = value[3] | value[4] << 8;
                            int mag_z = value[5] | value[6] << 8;
                            String mag_data = mag_x + "," + mag_y + "," + mag_z;
                            DataLogService.log(mContext, MAG_FILE, mag_data, HEADER);
                            break;
                    }

                    break;
                case CHARACTERISTIC_WRITTEN:
                    // A Connected BLE Device has been successfully written to
                    break;
                case CHARACTERISTIC_UPDATED:
                    // A Connected BLE Device has notified of an updated characteristic (this must be requested via enableNotifications)

                    // Only the Data Ready Characteristic should be getting notified
                    mqttPublish("Data Ready!");

                    BluetoothGattService service = gatt.getService(UUID.fromString(GattServices.IMU));
                    BluetoothGattCharacteristic accelCharacteristic =
                            service.getCharacteristic(UUID.fromString(GattCharacteristics.ACCEL));
                    mBluetoothLeService.readCharacteristic(gatt, accelCharacteristic);
                    break;
            }
        }
    };

    @Override
    public void onMqttConnected() {
        Log.d("MainActivity", "Connected to MQTT");
    }

    @Override
    public void onMqttDisconnected() {
        Log.d("MainActivity", "Disconnected from MQTT");
    }

    @Override
    public void onMqttMessageArrived(String topic, final MqttMessage mqttMessage) {
        final String message = new String(mqttMessage.getPayload());

        Log.d("MainActivity", "Mqtt messageArrived from topic: " + topic + " message: " + message);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothLeService.BluetoothLeBinder binder = (BluetoothLeService.BluetoothLeBinder) service;
            mBluetoothLeService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
