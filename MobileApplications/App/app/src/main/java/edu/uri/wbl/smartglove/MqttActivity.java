package edu.uri.wbl.smartglove;

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
    MqttAndroidClient mqttAndroidClient;
    Button mPublishButton;

        final String serverUri = "tcp://test.mosquitto.org:1883";

        String clientId = "SmartGlove";
        final String publishTopic = "wbl";
        final String publishMessage = "Hello World!";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mqtt);

            mPublishButton = (Button) findViewById(R.id.publish_btn);
            mPublishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    publishMessage();
                }
            });

            clientId = clientId + System.currentTimeMillis();

            mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {

                }

                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);







            try {
                //addToHistory("Connecting to " + serverUri);
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        //subscribeToTopic();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(this.getClass().getSimpleName(), "Failed to Connect");
                    }
                });


            } catch (MqttException ex){
                ex.printStackTrace();
            }

        }

        private void addToHistory(String mainText){
            Log.d("MQTT","LOG: " + mainText);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement

            return super.onOptionsItemSelected(item);
        }

//        public void subscribeToTopic(){
//            try {
//                mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//                        addToHistory("Subscribed!");
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        addToHistory("Failed to subscribe");
//                    }
//                });
//
//                // THIS DOES NOT WORK!
//                mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                    @Override
//                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        // message Arrived!
//                        System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
//                    }
//                });
//
//            } catch (MqttException ex){
//                System.err.println("Exception whilst subscribing");
//                ex.printStackTrace();
//            }
//        }

        public void publishMessage(){

            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(publishMessage.getBytes());
                if(mqttAndroidClient == null) {
                    return;
                }
                mqttAndroidClient.publish(publishTopic, message);
                addToHistory("Message Published");
                if(!mqttAndroidClient.isConnected()){
                    addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
                }
            } catch (MqttException e) {
                System.err.println("Error Publishing: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        };
}
