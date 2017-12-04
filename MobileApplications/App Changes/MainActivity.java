package edu.uri.wbl.bledemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import edu.uri.wbl.bledemo.ble.GattAttributes.GattCharacteristics;
import edu.uri.wbl.bledemo.ble.GattAttributes.GattDevices;
import edu.uri.wbl.bledemo.ble.GattAttributes.GattServices;
import edu.uri.wbl.bledemo.ble.BleUpdate;
import edu.uri.wbl.bledemo.ble.BleUpdateReceiver;
import edu.uri.wbl.bledemo.ble.BluetoothLeService;
import edu.uri.wbl.bledemo.io.DataLogService;
import edu.uri.wbl.bledemo.mqtt.MqttManager;
import edu.uri.wbl.bledemo.mqtt.MqttSettings;

public class MainActivity extends AppCompatActivity implements MqttManager.MqttManagerListener {
    private static final int    MQTT_QOS = 1;
    private final static String SERVER_URI = "fog.wbl.cloud:1883";
    private final static String MQTT_TOPIC = "wbl/smartglove/kiya";
    private final static String MQTT_TEST_MESSAGE = "Hello from Andy P!";

    private final static String MQTT_HOST = "fog.wbl.cloud";
    private final static int    MQTT_PORT = 1883;
    private final static String MQTT_USERNAME = "kabuki";
    private final static String MQTT_PASSWORD = "vfghjkm4774";
    private final static boolean MQTT_CLEAN_SESSION = false;
    private final static boolean MQTT_SSL = false;

    private static final String HEADER = "date,time,x,y,z";
    private static final File ACCEL_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/accel.csv");
    private static final File GYRO_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/gyro.csv");
    private static final File MAG_FILE = new File(Environment.DIRECTORY_DOCUMENTS, "sg/mag.csv");

    private Context mContext;
    private Button mLeftHandConnectBtn, mLeftLegConnectBtn, mRightHandConnectBtn, mRightLegConnectBtn;
    private Button mLeftHandDisconnectBtn, mLeftLegDisconnectBtn, mRightHandDisconnectBtn, mRightLegDisconnectBtn;

    private MqttManager mMqttManager;
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mMqttManager = MqttManager.getInstance(mContext);
        if (MqttSettings.getInstance(mContext).isConnected()) {
            mMqttManager.connectFromSavedSettings(this);
        } else {
            // TODO Need info from Nick/Nate.
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
        Intent intent = new Intent(mContext, BluetoothLeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(mBleUpdateReceiver, BleUpdateReceiver.INTENT_FILTER);

        /*
         * TODO: Check for Permissions Here
         */
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBleUpdateReceiver);

        unbindService(mConnection);
        mBound = false;
    }

    private void mqttPublish(String data){
        mMqttManager.publish(MQTT_TOPIC, data, MQTT_QOS);
    }

    private BleUpdateReceiver mBleUpdateReceiver = new BleUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Every Update will include the Device Address and Corresponding Update
            String bd_addr = intent.getStringExtra(EXTRA_DEVICE);
            BluetoothGatt gatt = mBluetoothLeService.getGatt(bd_addr);
            BleUpdate update = (BleUpdate) intent.getSerializableExtra(BleUpdateReceiver.EXTRA_UPDATE);
            switch (update) {
                case CONNECTED:
                    Toast.makeText(mContext, "Connected to " + gatt.getDevice().getName(), Toast.LENGTH_LONG).show();
                    mBluetoothLeService.discoverServices(gatt);
                    break;
                case DISCONNECTED:
                    // AndyP
                    // Changed gatt.getDevice().getName() to bd_addr for now to stop disconnect crash
                    Toast.makeText(mContext, "Disconnected from " + bd_addr, Toast.LENGTH_LONG).show();
                    break;
                case SERVICES_DISCOVERED:
                    Toast.makeText(mContext, "Discovered Services on " + gatt.getDevice().getName(), Toast.LENGTH_LONG).show();

                    BluetoothGattService smartService = gatt.getService(UUID.fromString(GattServices.SMART));
                    BluetoothGattCharacteristic dataCharacteristic =
                            smartService.getCharacteristic(UUID.fromString(GattCharacteristics.DATA_READY));
                    mBluetoothLeService.enableNotify(gatt, dataCharacteristic);
                    break;
                case CHARACTERISTIC_READ:

                    break;
                case CHARACTERISTIC_WRITTEN:

                    break;
                case CHARACTERISTIC_UPDATED:
                    String charUuid = intent.getStringExtra(EXTRA_CHARACTERISTIC);
                    byte[] value = intent.getByteArrayExtra(EXTRA_VALUE);

                    BluetoothGattService imuService = gatt.getService(UUID.fromString(GattServices.IMU));

                    switch (charUuid) {
                        case GattCharacteristics.DATA_READY:
                            mqttPublish("Data Ready!");

                            BluetoothGattCharacteristic accelCharacteristic =
                                    imuService.getCharacteristic(UUID.fromString(GattCharacteristics.ACCEL));
                            mBluetoothLeService.readCharacteristic(gatt, accelCharacteristic);
                            break;
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
            }
        }
    };

    @Override
    public void onMqttConnected() {

    }

    @Override
    public void onMqttDisconnected() {

    }

    @Override
    public void onMqttMessageArrived(String topic, final MqttMessage mqttMessage) {
        final String message = new String(mqttMessage.getPayload());

        Log.d("MainActivity", "Mqtt messageArrived from topic: " +topic+ " message: "+message);
    }

    /** Defines callbacks for service binding, passed to bindService() */
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
