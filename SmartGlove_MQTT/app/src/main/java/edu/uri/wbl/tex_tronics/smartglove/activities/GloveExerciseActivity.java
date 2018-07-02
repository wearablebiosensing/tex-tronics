package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.io.SmartGloveInterface;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsExerciseManager;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsManagerService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdate;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdateReceiver;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;
import edu.uri.wbl.tex_tronics.smartglove.visualize.GenerateGraph;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GloveExerciseActivity extends AppCompatActivity implements SmartGloveInterface
{
    private static final String TAG = "GloveExerciseActivity";
    private static final String COUNTER_TEXT = "Repetitions: ";

    private static final String EXERCISE_NAME = "uri.wbl.tex_tronics.name";
    private static final String EXTRA_DEVICE_ADDRS = "uri.wbl.tex_tronics.devices";
    private static final String EXTRA_DEVICE_TYPES = "uri.wbl.tex_tronics.device_types";
    private static final String EXTRA_EXERCISE_MODES = "uri.wbl.tex_tronics.exercise_modes";

    private static int selectedExercise;

    private static final int REQUIRED_REPS = 10;
    private static final float VISIBLE_VALUES = 20f;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series1, series2;
    private DataPoint[] dataPoints1;
    private DataPoint[] dataPoints2;

    private Handler handler;
    private int count;
    private boolean halfway;

    private ArrayList<String> xAxis = new ArrayList<>();
    private boolean recording = false;
    private long startTime;

    private String[] deviceAddresses;
    private String[] deviceTypes;
    private String[] exerciseModes;

    private Button disconnectBtn, nextButton;
    private TextView sideInstructionsText;
    private GifImageView sideImage;

    private Context mContext;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceBundle)
    {
        // Sets up screen
        super.onCreate(savedInstanceBundle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_glove_exercise);
        mContext = this;

        TexTronicsManagerService.start(mContext);

        // Set screen name
        Intent intent = getIntent();
        if(intent == null) {
            setTitle("Exercise");
            return;
        }
        String name = intent.getStringExtra(EXERCISE_NAME);
        setTitle(name);

        sideInstructionsText = findViewById(R.id.exercise_side_text);
        sideImage = findViewById(R.id.exercise_side_image);
        if(intent != null)
            setSideViews(name);

        deviceAddresses = TexTronicsExerciseManager.getmDeviceAddressList();
        deviceTypes = TexTronicsExerciseManager.getmDeviceTypeList();
        exerciseModes = TexTronicsExerciseManager.getmExerciseModes();

//        deviceAddresses = intent.getStringArrayExtra(EXTRA_DEVICE_ADDRS);
//        deviceTypes = intent.getStringArrayExtra(EXTRA_DEVICE_TYPES);
//        exerciseModes = intent.getStringArrayExtra(EXTRA_EXERCISE_MODES);

        if(deviceAddresses == null) {
            return;
        }

        // Sets up the repetition counter on the GUI
        count = 0;
        graph = findViewById(R.id.graph);
        graph = GenerateGraph.makeGraph(graph);
        series1 = new LineGraphSeries<>();
        // Thumb
        series1.setColor(Color.RED);
        graph.addSeries(series1);
        series2 = new LineGraphSeries<>();
        // Index
        series2.setColor(Color.GREEN);
        graph.addSeries(series2);

        // Gets the current time for graph timer
        startTime = System.currentTimeMillis();

        // Initializes the data processing, which allows the GATT notification to start
        // displaying the GATT values on the graph
        handler = new Handler()
        {
            public void handleMessage(Message message)
            {
                switch (message.what)
                {
                    case Graph.READY2:
                        // Receives values from the ProcessData class
                        if(recording)
                        {
//                            gatherCSVData.logJunkData(new float[]{processData.returnXVal(), processData.returnYVal()},"junk_data");
                        }
                        // Updates the graph with those values
                        break;
                    default:
                        Log.v(TAG, "Message handle error");
                }
            }
        };
//        processData = new ProcessData(handler);

        disconnectBtn = findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String address : deviceAddresses)
                {
                    TexTronicsManagerService.disconnect(mContext, address);
                }
            }
        });

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = getIntent();
                finish();
                TexTronicsExerciseManager.startExercise(intent, mContext);
            }
        });
    }

    /** setSideViews
     *  Sets the side instruction views to correspond with the active exercise
     * @param name: Uses the name of the exercise to check what the views should be set to
     */
    private void setSideViews(String name)
    {
        if (name.equals("Finger Tap"))
        {
            sideInstructionsText.setText(InstructionsText.FINGER_TAP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed Grip"))
        {
            sideInstructionsText.setText(InstructionsText.CLOSED_GRIP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand Flip"))
        {
            sideInstructionsText.setText(InstructionsText.HAND_FLIP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Heel Tap"))
        {
            sideInstructionsText.setText(InstructionsText.HEEL_TAP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Toe Tap"))
        {
            sideInstructionsText.setText(InstructionsText.TOE_TAP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Foot Stomp"))
        {
            sideInstructionsText.setText(InstructionsText.FOOT_STOMP_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Walk Steps"))
        {
            sideInstructionsText.setText(InstructionsText.WALK_STEPS_TEXT);
            sideImage.setBackgroundResource(InstructionsImage.WALK_STEPS_GIF);
        }
    }



    /** setExerciseSelection(int selection)
     *
     * Called by the ExerciseSelection activity.
     * Identifies to this activity which of the three glove exercises the user has chosen.
     * The conditions that indicate a repetition and the construction of the graph depend on
     * which activity has been chosen in order to ensure that the user is completing the
     * exercise correctly.
     *
     * @param selection
     * 1 = Finger Tap
     * 2 = Closed Grip
     * 3 = Hand Flip
     */
    public static void setExerciseSelection(int selection)
    {
        selectedExercise = selection;
    }

    /** updateGraph()
     *
     * Called from the class' handler, which is shared with the ProcessData class.
     * Updates the graph to show the latest values gathered from the glove.
     *
     * Values that are received from the glove.
     * @param indexFloat
     * @param thumbFloat
     */


    /** checkRep()
     *
     * Called by the updateGraph() method.
     * Checks to see if the user has completed a repetition based on the exercise
     * that they are currently doing.
     *
     * Values that are received from the glove.
     * @param indexFloat
     * @param thumbFloat
     */
    private void checkRep(float indexFloat, float thumbFloat)
    {
        switch(selectedExercise)
        {
            // Finger Tap
            case 1:
                // If the user has completed the exercise, return to the selection screen
                if(count == REQUIRED_REPS)
                {
                    // Stop graphing the glove's values
//                    processData.setHandler(null);

                    // Save the entry lists so that they can be converted into CSV files
//                    GatherCSVData.writeFingerTap(indexEntries, thumbEntries, xAxis);


                    // Return to the exercise selection screen
                    Intent intent = new Intent(this, ExerciseSelection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                // User is at the halfway point of the repetition
                else if((halfway == false) && (indexFloat > thumbFloat) && (indexFloat > 25000))
                {
                    halfway = true;
                }
                // User has completed a repetition
                else if ((halfway == true) && (thumbFloat < 20000) && (indexFloat < thumbFloat))
                {
                    count++;
                    halfway = false;
                }
                break;
            // Closed Grip
            case 2:
                if(count == REQUIRED_REPS)
                {
                    try{Thread.sleep(250);}
                    catch(Exception e){Log.d(TAG, "Could not sleep due to exception " + e);}
                    Intent intent = new Intent(this, ExerciseSelection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case 3:
                break;
            default:
                Log.w(TAG, "The selected exercise was not set.");
        }
    }

    private void addEntry(int thumb, int index)
    {
        Log.e("MainActivity", "Thumb: " + thumb + " Index: " + index);
        series1.appendData(new DataPoint(count, thumb), true, 1000);
        series2.appendData(new DataPoint(count, index), true, 1000);
        count++;
    }

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received BLE Update");
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);

            if(action.equals(BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY)) {
                UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                    Log.d(TAG, "Data Received");
                    byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                    // First Data Set
                    int thumb = (((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF)));
                    int index = (((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF)));
                    addEntry(thumb, index);

                    // Second Data Set
                    thumb = (((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                    index = (((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));
                    addEntry(thumb, index);


                    // Third Data Set
                    thumb = (((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));
                    index = (((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF)));
                    addEntry(thumb, index);
                }
            }
        }
    };

    private TexTronicsUpdateReceiver mTexTronicsUpdateReceiver = new TexTronicsUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !intent.hasExtra(UPDATE_DEVICE) || !intent.hasExtra(UPDATE_TYPE)) {
                Log.w(TAG, "Invalid Update Received");
                return;
            }

            String deviceAddress = intent.getStringExtra(UPDATE_DEVICE);    // NULL if MQTT Update
            TexTronicsUpdate updateType = (TexTronicsUpdate) intent.getSerializableExtra(UPDATE_TYPE);

            if (updateType == null) {
                Log.w(TAG, "NULL Update Received");
                return;
            }

            switch (updateType) {
                case started:
                    for(int i = 0; i < deviceAddresses.length; i++) {
                        // Connect to Each Device
                        Log.d(TAG, "Connecting to " + deviceAddresses[i]);
                        TexTronicsManagerService.connect(mContext, deviceAddresses[i], ExerciseMode.getExercise(exerciseModes[i]), DeviceType.getDevicetype(deviceTypes[i]));
                    }

                    break;
                case ble_connecting:
                    // Connecting to Device <deviceAddress>
                    Log.d(TAG, "Connecting to " + deviceAddress);
                    break;
                case ble_connected:
                    // Device <deviceAddress> Has Been Connected
                    Log.d(TAG, "Connected to " + deviceAddress);
                    break;
                case ble_disconnecting:
                    // Disconnecting from Device <deviceAddress>
                    Log.d(TAG, "Disconnecting from " + deviceAddress);
                    break;
                case ble_disconnected:
                    // Device <deviceAddress> Has Been Disconnected
                    Log.d(TAG, "Disconnected from " + deviceAddress);
                    break;
                case mqtt_connected:
                    // Connected to MQTT Server
                    Log.d(TAG, "Connected to MQTT Server");
                    break;
                case mqtt_disconnected:
                    // Disconnected from MQTT Server
                    Log.d(TAG, "Disconnected from MQTT Server");
                    break;
                default:
                    Log.w(TAG, "Unknown Update Received");
                    break;
            }
        }
    };

    @Override
    public void onBackPressed()
    {
        List<String> addressList = Arrays.asList(deviceAddresses);
        List<String> devicesList = Arrays.asList(deviceTypes);
//        processData.setHandler(null);
        ExerciseSelection.start(mContext, addressList, devicesList);
        super.onBackPressed();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        registerReceiver(mTexTronicsUpdateReceiver, TexTronicsUpdateReceiver.INTENT_FILTER);
    }

    @Override
    protected void onStop()
    {
//        processData.setHandler(null);
        unregisterReceiver(mTexTronicsUpdateReceiver);
        unregisterReceiver(mBLEUpdateReceiver);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        TexTronicsManagerService.stop(mContext);
        Log.i(TAG, "Dead as shit");
        super.onDestroy();
    }
}