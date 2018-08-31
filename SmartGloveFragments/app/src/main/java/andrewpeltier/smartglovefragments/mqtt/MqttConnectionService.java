package andrewpeltier.smartglovefragments.mqtt;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
 * Created by mcons on 2/28/2018.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class MqttConnectionService extends Service {
    private static final String TAG = "MQTT Service";
    private static final String EXTRA_DATA = "uri.wbl.tex_tronics.mqtt.data";
    int counter = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        context.stopService(intent);
    }

    public static String generateJson(String date, String sensorId, String choice, String exerciseID, String routineID, String data) {
        JsonData jsonData = new JsonData(date, sensorId, choice, exerciseID, routineID, data);
        Log.d(TAG,"JSON Data: " + jsonData.toString());
        return jsonData.toString();
    }

    private final String SERVER_URI = "tcp://131.128.51.213:1883"; //tcp://131.128.51.42:1883
    private final String PUBLISH_TOPIC = "kaya/patient/data";

    private MqttAndroidClient mMqttAndroidClient;
    private String mClientId;

    private IBinder mBinder = new MqttConnectionBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

        mClientId = "Patient";

        mMqttAndroidClient = new MqttAndroidClient(getApplicationContext(), SERVER_URI, mClientId);
        mMqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection Lost!");
                connectClient();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Do Nothing, for now
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Do Nothing, for now
            }
        });

        connectClient();

    }

    /**
     * creates mqtt options and connects client
     */
    public void connectClient() {
        Log.v(TAG, "Connect client envoked...");
        counter++;
        Log.v(TAG, "Counter: " + counter);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    if(mMqttAndroidClient != null)
                    {
                        Log.d(TAG, "Successfully Connected");
                        sendUpdate(UpdateType.connected);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to Connect (" + exception.toString() + ")");
                    sendUpdate(UpdateType.disconnected);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MQTT Service Destroyed");

        mMqttAndroidClient = null;
        super.onDestroy();
    }

    public class MqttConnectionBinder extends Binder {
        public MqttConnectionService getService() {
            return MqttConnectionService.this;
        }
    }

    public void publishMessage(final String data) {
        if(mMqttAndroidClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(data.getBytes());
                mMqttAndroidClient.publish(PUBLISH_TOPIC, message);
                if (!mMqttAndroidClient.isConnected()) {
                    Log.w(TAG, "MQTT Not Connected");
                    return;
                }
            } catch (MqttException e) {
                Log.w(TAG, "Error Publishing: " + e.getMessage());
                e.printStackTrace();
            } finally {
                Log.w(TAG, "Published");
            }
        } else {
            Log.w(TAG, "Not Connected Yet!");
            try {
                mMqttAndroidClient.connect(getApplicationContext(), new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        publishMessage(data);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        exception.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendUpdate(UpdateType updateType) {
        Intent intent = new Intent(MqttUpdateReceiver.INTENT_FILTER_STRING);
        intent.putExtra(MqttUpdateReceiver.UPDATE_TYPE, updateType);
        sendBroadcast(intent);
    }
}