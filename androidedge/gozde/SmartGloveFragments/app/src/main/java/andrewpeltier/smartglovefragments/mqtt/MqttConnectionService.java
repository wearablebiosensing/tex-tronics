package andrewpeltier.smartglovefragments.mqtt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/** ======================================
 *
 *      MqttConnectionService Class
 *
 *  ======================================
 *
 *  Service that allows us to establish and maintain a connection to our MQTT server. Here we provide the means
 *  to connect and publish data to our server.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class MqttConnectionService extends Service
{
    private static final String TAG = "MQTT Service";
    private static final String EXTRA_DATA = "uri.wbl.tex_tronics.mqtt.data";
    int counter = 0;                // Used for debugging. Find out how many times we've tried to reconnect

    /** start()
     *
     * Starts the MQTT server connection as a background service. This is started
     * by the TexTronics Manager once the application starts.
     *
     * @param context           -State of the application
     */
    public static void start(Context context)
    {
        Intent intent = new Intent(context, MqttConnectionService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MqttConnectionService.class);
        context.stopService(intent);
    }

    /** generateJson()
     *
     * Takes as parameters each field required by JsonData to create a json-styled entry to our MQTT server, then
     * creates that entry in the form of a string.
     *
     * @param date
     * @param sensorId
     * @param choice
     * @param exerciseID
     * @param routineID
     * @param data
     * @return
     */
    public static String generateJson(String date, String sensorId, String choice, String exerciseID, String routineID, String data) {
        JsonData jsonData = new JsonData(date, sensorId, choice, exerciseID, routineID, data);
        Log.d(TAG,"JSON Data: " + jsonData.toString());
        return jsonData.toString();
    }

    /**
     * Hardcoded IP Address of our raspberry pie, which hosts the MQTT server
     */
    /*MQTT tcp://131.128.53.90:1883*/
    private final String SERVER_URI = "tcp://131.128.53.90:1883"; //tcp://131.128.51.42:1883
    /**
     * Location to publish data to the MQTT server
     */
    private final String PUBLISH_TOPIC = "kaya/patient/data";

    private MqttAndroidClient mMqttAndroidClient;           // Client that facilitates MQTT - Android connection
    private String mClientId;                               // Identifier for said client

    private IBinder mBinder = new MqttConnectionBinder();   // Binds the MQTT connection to service

    /** onCreate()
     *
     * Called when the service is created. This sets up a callback system for our MQTT client connection,
     * as well as begins to connect our client.
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

        mClientId = "Patient";

        // Create and set up MQTT-Android Client
        mMqttAndroidClient = new MqttAndroidClient(getApplicationContext(), SERVER_URI, mClientId);
        mMqttAndroidClient.setCallback(new MqttCallback()
        {
            /** connectionLost()
             *
             * Tells us why we lost connection to the MQTT server, then tries to reconnect to it
             *
             * @param cause     -Error identifying why we've lost connection
             */
            @Override
            public void connectionLost(Throwable cause)
            {
                Log.d(TAG, "Connection Lost! Error: " + cause);
                connectClient();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {
                // Do Nothing, for now
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token)
            {
                // Do Nothing, for now
            }
        });

        // Connect client to MQTT server
        connectClient();
    }

    /** connectClient
     *
     * Called once created. Creates MQTT options and connects client
     *
     */
    public void connectClient()
    {
        // Increment the counter and print to console to see how many reconnection attempts we've made
        Log.v(TAG, "Connect client envoked...");
        counter++;
        Log.v(TAG, "Counter: " + counter);

        // Set options for our MQTT connection client
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try
        {
            // Try to connect to MQTT. Sets up action listener to handle outcome
            mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener()
            {
                /**
                 * The connection was a success. We let the TexTronics Manager service know this. Update is
                 * sent and received by UpdateReceiver.
                 */
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    if(mMqttAndroidClient != null)
                    {
                        Log.d(TAG, "Successfully Connected");
                        sendUpdate(UpdateType.connected);
                    }
                }

                /**
                 * The connection was a failure. We let the TexTronics Manager service know this. Update is
                 * sent and received by UpdateReceiver.
                 */
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Log.d(TAG, "Failed to Connect (" + exception.toString() + ")");
                    sendUpdate(UpdateType.disconnected);
                }
            });
        }
        catch (MqttException e)
        {
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

    /** onDestory()
     *
     * Called when the application is killed. We just end the client communication from the device
     * to the MQTT server
     *
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "MQTT Service Destroyed");

        mMqttAndroidClient = null;
        super.onDestroy();
    }

    // Binds the MQTT connection to this service
    public class MqttConnectionBinder extends Binder
    {
        public MqttConnectionService getService() {
            return MqttConnectionService.this;
        }
    }

    /** publishMessage()
     *
     * Called by the TexTronics Manager either when we leave an exercise or disconnect from a device during
     * an exercise. We publish a message to the MQTT server through our client to the topic location.
     *
     * @param data              -The data (now is JSON format) from the exercise the user finished / was doing when
     *                          this method was called.
     */
    public void publishMessage(final String data)
    {
        if(mMqttAndroidClient.isConnected())
        {
            try
            {
                // Create a new message using the bytes of the data string
                MqttMessage message = new MqttMessage();
                message.setPayload(data.getBytes());
                // Try to publish the message to the topic
                mMqttAndroidClient.publish(PUBLISH_TOPIC, message);
                if (!mMqttAndroidClient.isConnected())
                {
                    Log.w(TAG, "MQTT Not Connected");
                    return;
                }
            }
            // Message could not be published
            catch (MqttException e)
            {
                Log.w(TAG, "Error Publishing: " + e.getMessage());
                e.printStackTrace();
            }
            // Message was published
            finally
            {
                Log.w(TAG, "Published");
            }
        }
        else {
            Log.w(TAG, "Not Connected Yet!");
            try
            {
                /*
                 * If we were not connected to the MQTT server, we first try to connect. If we
                 * can connect, then we publish the message after we do so.
                 */
                mMqttAndroidClient.connect(getApplicationContext(), new IMqttActionListener()
                {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken)
                    {
                        publishMessage(data);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                    {
                        exception.printStackTrace();
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** sendUpdate
     *
     * Broadcasts an update to the TexTronics Update Receiver telling it (primarily the main activity)
     * when we have connected or disconnected from the MQTT-Android client. This is called by the result
     * of the connectClient() method above.
     *
     * @param updateType
     */
    private void sendUpdate(UpdateType updateType)
    {
        Intent intent = new Intent(MqttUpdateReceiver.INTENT_FILTER_STRING);
        intent.putExtra(MqttUpdateReceiver.UPDATE_TYPE, updateType);
        sendBroadcast(intent);
    }
}