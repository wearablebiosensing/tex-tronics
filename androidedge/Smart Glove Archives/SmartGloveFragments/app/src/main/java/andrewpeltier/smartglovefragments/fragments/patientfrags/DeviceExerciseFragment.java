package andrewpeltier.smartglovefragments.fragments.patientfrags;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.UUID;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.ble.BluetoothLeConnectionService;
import andrewpeltier.smartglovefragments.ble.GattCharacteristics;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdate;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdateReceiver;
import andrewpeltier.smartglovefragments.visualize.GenerateGraph;
import pl.droidsonroids.gif.GifImageView;

/** ======================================
 *
 *      DeviceExerciseFragment Class
 *
 *  ======================================
 *
 *      This fragment holds the view that launches when the user is completing
 *  an exercise. Currently, a graph on the screen visualizes the incoming data
 *  from each device, with each of the two lines on the graph either representing
 *  each of the shoe devices or the index finger and thumb of our one glove.
 *
 *      In addition to this, we have buttons that can disconnect from the devices
 *  and move to the instructions for the next exercise once the demo is complete.
 *  After the exercise is complete, the data from the current exercise is published.
 *
 *  @author Andrew Peltier
 *  @version 1.0
 *
 */
public class DeviceExerciseFragment extends Fragment implements SmartGloveInterface
{
    private static final String TAG = "DeviceExerciseFragment";
    /**
     * Determines when the data from the exercise should start logging, namely when
     * the required devices are connected and the user is ready.
     */
    public static boolean START_LOG = false;
    private String exerciseName;                            // Name of the exercise currently in session
    private Button disconnectBtn, nextButton;               // View buttons
    private GifImageView sideImage;                         // Animated GIF specific to exercise
    private TextView loadingText;

    /** onCreateView()
     *
     * Called when the view is first created. We use the fragment_device_exercise XML file to load the view and its
     * properties into the fragment, which is then given to the Main Activity. For the device exercise screen,
     * we set up the buttons, graph, and side image here.
     *
     * @param inflater                      -Used to "inflate" or load the layout inside the main activity
     * @param container                     -Object containing the fragment layout (from MainActivity XML)
     * @param savedInstanceState            -State of the application
     * @return                          The intractable home fragment view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_device_exercise, container, false);
        loadingText = view.findViewById(R.id.loadingText);

        //TODO: Countdown timer
        // Starts logging if the devices are connected
        if(MainActivity.CONNECTED)
        {
            startTimer();
        }

        // Gets the exercise name from the Main Activity
        if(MainActivity.exercise_name != null)
            exerciseName = MainActivity.exercise_name;

        // Using the exercise name, set the animated gif to the image corresponding to
        // the current exercise
        sideImage = view.findViewById(R.id.stexercise_side_image);
        if(MainActivity.exercise_name != null)
        {
            setSideViews(exerciseName);
            sideImage.setVisibility(View.INVISIBLE);
        }

        // Sets up the "Next" button
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            /** onClick() for "Next" button
             *
             * Once clicked, the "Next" button will publish the data collected from this exercise,
             * meaning all data collected since START_LOG was set to true, to the MQTT server and
             * local CSV files. The START_LOG is then set to false to prevent further data logging,
             * and we move to the next exercise.
             *
             * @param view          -DeviceExerciseFragment View
             */
            @Override
            public void onClick(View view)
            {
                START_LOG = false;
                ((MainActivity)getActivity()).publish();
                ((MainActivity)getActivity()).startExercise();
            }
        });

        // Sets up the "Disconnect" button
        disconnectBtn = view.findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener(new View.OnClickListener()
        {
            /** onClick() for "Disconnect" button
             *
             * Once clicked, the "Disconnect" button disconnects all BLE devices. Upon disconnection,
             * the devices will still publish their data to the MQTT server and CSV files.
             *
             * @param view          -DeviceExerciseFragment View
             */
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).disconnect();
            }
        });

        // Set up loading text

        Log.d(TAG, "onCreateView: Started.");
        return view;
    }

    /** setSideViews()
     *
     * Takes the name of the exercise as a parameter, and sets the animated gif view with
     * the image that corresponds with the exercise name.
     *
     * @param name          -Name of the exercise
     */
    private void setSideViews(String name)
    {
        if (name.equals("Finger Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed Grip"))
        {
            sideImage.setBackgroundResource(InstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand Flip"))
        {
            sideImage.setBackgroundResource(InstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Heel Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Toe Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Foot Stomp"))
        {
            sideImage.setBackgroundResource(InstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Walk Steps"))
        {
            sideImage.setBackgroundResource(InstructionsImage.WALK_STEPS_GIF);
        }
    }

    private void startTimer()
    {
        Log.d(TAG, "Starting data logging.");
        final CountDownTimer startTimer = new CountDownTimer(3000, 980) {
            int countdown = 3;

            @Override
            public void onTick(long l)
            {
                Log.v(TAG, "Tick: " + countdown);
                loadingText.setText("" + countdown);
                countdown--;
            }

            @Override
            public void onFinish()
            {
                START_LOG = true;
//                graph.setVisibility(View.VISIBLE);
                loadingText.setText("Collecting data...");
                sideImage.setVisibility(View.VISIBLE);
            }
        };
        startTimer.start();
    }

    /**
     *  ========= BLE Update Receiver =========
     *
     *  We have a BLE update receiver in the DeviceExerciseFragment so that we can parse the
     *  data received from our data packets and use the values for our graph.
     *
     */
    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver()
    {
        /** onReceive()
         *
         * Called whenever a data packet is received from a connected BLE device. The packet is parsed by
         * bit shifting in order to get the integer values for each of the data points collected.
         *
         * @param context           -Application's current state
         * @param intent            -Operation to be performed. In this case, it contains information regarding the
         *                          device MAC address, characteristic, BLE data, and operation or action to be performed
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received BLE Update: " + exerciseName);
            // Get the information from the intent
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            Log.v(TAG, "DeviceAddress: " + deviceAddress);
            Log.v(TAG, "BLE Action: " + action);

            // Need to check if our intent was sent by a characteristic notification. If so, then it contains the data
            // from our devices.
            if(action.equals(BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY))
            {
                // Get the characteristic from the intent, then check to see if it is the correct characteristic
                UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC))
                {
                    // Get the data from the intent
                    Log.d(TAG, "Data Received");
                    byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                    /**
                     * Get the information from the byte array by bit shifting
                     *
                     * Since the data packet will be sent to the update receiver here regardless if
                     * the user is ready or not, we need to check to see if the START_LOG boolean is
                     * set to true to make sure that the user is ready and exercsing before we log
                     * the data and display it on our graph
                     */

                    // First Data Set
                    int thumb = (((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF)));
                    int index = (((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF)));


                    // Second Data Set
                    thumb = (((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                    index = (((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));


                    // Third Data Set
                    thumb = (((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));
                    index = (((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF)));


                    Log.d(TAG, "onReceive: Start Log = " + START_LOG);
                }
            }
            else if(action.equals(BluetoothLeConnectionService.GATT_STATE_CONNECTED))
            {
                startTimer();
            }
            else if(action.equals(BluetoothLeConnectionService.GATT_STATE_DISCONNECTED))
            {
                START_LOG = false;
                loadingText.setText("Disconnected");
                sideImage.setVisibility(View.INVISIBLE);
            }
        }
    };

    /** onStart()
     *
     * Called when the DeviceExerciseFragment is loaded into the Main Activity. This registers a BLE update receiver
     * to the fragment so that we can visualize incoming data.
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: started " + exerciseName);

        getActivity().registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
    }

    /** onStop()
     *
     * Called when the application is stopped, which happens if the application is killed or stopped during
     * this fragment. Most likely, this is called when the fragment is replaced with the next fragment, either being the
     * instructions, exercise selection, or finish screen fragments. We simply unregister our BLE update receiver here.
     *
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: stopped");

        getActivity().unregisterReceiver(mBLEUpdateReceiver);
    }

    /** onPause() / onDestroy()
     *
     * Logs to the console when the application here has paused or has been killed / destroyed
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Paused");
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroying exercise fragment...");
        super.onDestroy();
    }
}
