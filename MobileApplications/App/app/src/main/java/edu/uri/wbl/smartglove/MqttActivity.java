package edu.uri.wbl.smartglove;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

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

import java.util.ArrayList;

/**
 * Created by mcons on 11/20/2017.
 */

public class MqttActivity extends AppCompatActivity {
    private final static String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private final static String MQTT_TOPIC = "wbl/smartglove/kiya";
    private final static String MQTT_TEST_MESSAGE = "Hello from Andy P!";
    private final static String CLIENT_BASE_ID = "smartglove";

    private Context mContext;
    private MqttAndroidClient mMqttAndroidClient;
    private String mClientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);

        mContext = this;

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
