package edu.uri.wbl.tex_tronics.smartglove.mqtt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * TexTronicsAction Called (static method) --> onStartCommand --> Corresponding TexTronicsAction Method
 * Example:
 *      MqttConnectionService.publish()
 *      onStartCommand()
 *          ...
 *          switch action
 *              ...
 *              case publish:
 *                  publishMessage()
 *      publishMessage()
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class MqttConnectionService extends Service {
    private static final String TAG = "MQTT Service";
    private static final String EXTRA_ACTION = "uri.wbl.tex_tronics.mqtt.action";
    private static final String EXTRA_DATA = "uri.wbl.tex_tronics.mqtt.data";
    private static final String EXTRA_URI = "uri.wbl.tex_tronics.mqtt.uri";
    private static final String EXTRA_CLIENT_ID = "uri.wbl.tex_tronics.mqtt.client_id";
    private static final String EXTRA_PUBLISH_TOPIC = "uri.wbl.tex_tronics.mqtt.publish_topic";
    private static final String EXTRA_RECONNECT = "uri.wbl.tex_tronics.mqtt.reconnect";
    private static final String EXTRA_CLEAN_SESSION = "uri.wbl.tex_tronics.mqtt.clean_session";

    public static void start(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        intent.putExtra(EXTRA_ACTION, MqttAction.START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        intent.putExtra(EXTRA_ACTION, MqttAction.STOP);
        context.startService(intent);
    }

    public static void connect(Context context, String uri, boolean reconnect, boolean cleanSession, String clientId) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        intent.putExtra(EXTRA_ACTION, MqttAction.CONNECT);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_CLIENT_ID, clientId);
        intent.putExtra(EXTRA_RECONNECT, reconnect);
        intent.putExtra(EXTRA_CLEAN_SESSION, cleanSession);
        context.startService(intent);
    }

    public static void publish(Context context, @NonNull String data, @NonNull String publishTopic) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        intent.putExtra(EXTRA_ACTION, MqttAction.PUBLISH);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_PUBLISH_TOPIC, publishTopic);
        context.startService(intent);
    }

    public static void disconnect(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        intent.putExtra(EXTRA_ACTION, MqttAction.DISCONNECT);
        context.startService(intent);
    }

    public static String generateJson(String date, String sensorId, String data) {
        JsonData jsonData = new JsonData(date, sensorId, data);
        Log.d(TAG,"JSON Data: " + jsonData.toString());
        return jsonData.toString();
    }

    private MqttAndroidClient mMqttAndroidClient;
    private boolean mConnected;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

        mConnected = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }

        MqttAction action = (MqttAction) intent.getSerializableExtra(EXTRA_ACTION);
        if(action == null) {
            Log.e(TAG, "NULL TexTronicsAction Received");
            return START_STICKY;
        }

        switch (action) {
            case START:
                // Do Nothing
                sendUpdate(MqttUpdate.started);
                break;
            case STOP:
                sendUpdate(MqttUpdate.stopped);
                stopSelf();
                break;
            case CONNECT:
                String uri = intent.getStringExtra(EXTRA_URI);
                String clientId = intent.getStringExtra(EXTRA_CLIENT_ID);
                boolean reconnect = intent.getBooleanExtra(EXTRA_RECONNECT, false);
                boolean cleanSession = intent.getBooleanExtra(EXTRA_CLEAN_SESSION, false);

                if(uri == null || clientId == null) {
                    Log.e(TAG, "Received Invalid TexTronicsAction Packet");
                    return START_STICKY;
                }

                Log.d(TAG,"Connecting to " + uri + "(Client ID: " + clientId + ")");

                connect(uri, clientId, reconnect, cleanSession);
                break;
            case PUBLISH:
                String publishTopic = intent.getStringExtra(EXTRA_PUBLISH_TOPIC);
                String data = intent.getStringExtra(EXTRA_DATA);
                if(publishTopic == null || data == null) {
                    Log.e(TAG, "Received Invalid TexTronicsAction Packet");
                    break;
                }

                publishMessage(publishTopic, data);
                break;
            case DISCONNECT:
                disconnect();
                break;
            default:
                Log.e(TAG, "Invalid TexTronicsAction Received");
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service Destroyed");

        super.onDestroy();
    }

    private void connect(String uri, String clientId, boolean reconnect, boolean cleanSession) {
        mMqttAndroidClient = new MqttAndroidClient(getApplicationContext(), uri, clientId);
        mMqttAndroidClient.setCallback(mMqttCallback);

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(reconnect);
        mqttConnectOptions.setCleanSession(cleanSession);

        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Successfully Connected");
                    sendUpdate(MqttUpdate.connected);
                    mConnected = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to Connect (" + exception.toString() + ")");
                    sendUpdate(MqttUpdate.disconnected);
                    mConnected = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String publishTopic, String data){
        if(mConnected) {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(data.getBytes());
                mMqttAndroidClient.publish(publishTopic, message);
                if (!mMqttAndroidClient.isConnected()) {
                    Log.w(TAG, "MQTT Not Connected");
                    return;
                }
            } catch (MqttException e) {
                Log.w(TAG, "Error Publishing: " + e.getMessage());
                e.printStackTrace();
            } finally {
                Log.w(TAG, "Published");
                sendUpdate(MqttUpdate.published);
            }
        } else {
            Log.w(TAG, "Not Connected Yet!");
        }
    }

    private void disconnect() {
        try {
            mMqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
            sendUpdate(MqttUpdate.disconnected);
        }
    }

    private void sendUpdate(MqttUpdate mqttUpdate) {
        Intent intent = new Intent(MqttUpdateReceiver.INTENT_FILTER_STRING);
        intent.putExtra(MqttUpdateReceiver.UPDATE_TYPE, mqttUpdate);
        sendBroadcast(intent);
    }

    private MqttCallback mMqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.w(TAG,"MQTT Connection Lost (" + cause.getMessage() + ")");
            sendUpdate(MqttUpdate.disconnected);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
