package edu.uri.wbl.smartglove;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import edu.uri.wbl.smartglove.ble.models.BluetoothCharacteristicModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothLeModel;
import edu.uri.wbl.smartglove.ble.receivers.BleUpdateReceiver;
import edu.uri.wbl.smartglove.ble.services.BleConnectionService;
import edu.uri.wbl.smartglove.ble.services.BluetoothLeService;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;




public class MainActivity extends AppCompatActivity {
    private final String BT_ADDR = "F6:D7:BF:73:72:D3";

    private final static String SERVER_URI = "tcp://fog.wbl.cloud:1883";
    private final static String MQTT_TOPIC = "test";//"wbl/smartglove/kiya";
    private final static String MQTT_TEST_MESSAGE = "Hello from Andy P!";
    private final static String CLIENT_BASE_ID = "smartglove";

    private MqttAndroidClient mMqttAndroidClient;
    private String mClientId;

    Context mContext;
    //BluetoothLeModel mBluetoothLeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        //mBluetoothLeModel = BluetoothLeModel.CREATE(BT_ADDR);
        registerReceiver(mBleUpdateReceiver, BleUpdateReceiver.INTENT_FILTER);

        Button startButton = (Button) findViewById(R.id.start_btn);
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        Button connectButton = (Button) findViewById(R.id.connect_btn);
        Button disconnectButton = (Button) findViewById(R.id.disconnect_btn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothLeService.START(mContext);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothLeService.STOP(mContext);
            }
        });

        // Initialize MQTT Client
        mClientId = CLIENT_BASE_ID + System.currentTimeMillis();    // Client ID must be unique
        mMqttAndroidClient = new MqttAndroidClient(mContext, SERVER_URI, mClientId);
        mMqttAndroidClient.setCallback(mMqttCallbackExtended);

        // Setup MQTT Connection Options
        /**
         * TODO:
         *      Verify and finalize the MQTT Connection Options
         */
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        // Attempt to connect to Broker
        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, mIMqttActionListener);
        } catch (MqttException e) {
            Log.w("MQTT Demo", e.getMessage());
        }
    }

    private BleUpdateReceiver mBleUpdateReceiver = new BleUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(BleUpdateReceiver.EXTRA_UPDATE, -1);
            switch(update) {
                case BleUpdateReceiver.UPDATE_CONNECTED:

                    //BleConnectionService.DISCOVER_SERVICES(mContext, mBluetoothLeModel);
                    break;
                case BleUpdateReceiver.UPDATE_DISCONNECTED:

                    break;
                case BleUpdateReceiver.UPDATE_SERVICES_DISCOVERED:
                    /*BluetoothLeModel bluetoothLeModel = (BluetoothLeModel) intent.getSerializableExtra(BleUpdateReceiver.EXTRA_DEVICE);
                    if(bluetoothLeModel == null) {
                        Log.d("BLE Update Receiver", "Error Retrieving BluetoothLeModel");
                        return;
                    }
                    Log.d(this.getClass().getSimpleName(), "Discovered Services on " + bluetoothLeModel.getBluetoothDeviceAddress());
                    BluetoothCharacteristicModel bluetoothCharacteristicModel = bluetoothLeModel.getCharacteristic(UUID.fromString("00004003-0000-1000-8000-00805f9b34fb"));
                    if(bluetoothCharacteristicModel == null) {
                        Log.d("BLE Update Receiver", "Error finding Characteristic");
                        return;
                    }
                    byte[] value = bluetoothCharacteristicModel.getValue();
                    Log.d("BLE Update Receiver", value.toString());*/
                    break;
                default:

                    break;
            }
        }
    };

    public void publishMessage(String messageText){
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(messageText.getBytes());
            mMqttAndroidClient.publish(MQTT_TOPIC, message);
            if(!mMqttAndroidClient.isConnected()){
                Log.w(this.getClass().getSimpleName(), "MQTT Not Connected! Message has been buffered");
            }
        } catch (MqttException e) {
            Log.e(this.getClass().getSimpleName(),"Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d("MQTT Listener", "Action Success");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.d("MQTT Listener", "Action Failure");
        }
    };

    private MqttCallbackExtended mMqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.d("MQTT Callback", "Connect Complete");
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.d("MQTT Callback", "Connection Lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.d("MQTT Callback", "Message Received");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.d("MQTT Callback", "Delivery Complete");
        }
    };
}
