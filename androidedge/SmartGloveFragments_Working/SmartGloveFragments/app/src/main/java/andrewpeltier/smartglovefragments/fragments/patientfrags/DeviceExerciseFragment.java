package andrewpeltier.smartglovefragments.fragments.patientfrags;

import java.lang.String;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;   //API for media recorder.
import android.media.MediaRecorder; //API for media recorder.
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;  //for logging print statements on the console.
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; //for getting local date and time from the system.
import java.util.List;
import java.util.UUID;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.ble.BluetoothLeConnectionService;
import andrewpeltier.smartglovefragments.ble.GattCharacteristics;
import andrewpeltier.smartglovefragments.database.StudyInformation;
import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.io.DataLog;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.io.StudyLog;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdate;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdateReceiver;
import andrewpeltier.smartglovefragments.visualize.Exercise;
import andrewpeltier.smartglovefragments.visualize.GenerateGraph;
import pl.droidsonroids.gif.GifImageView;

import java.io.File;  //to create folders on device file management.


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
public class DeviceExerciseFragment extends Fragment implements SmartGloveInterface {

    private static final String TAG = "DeviceExerciseFragment";
    /**
     * Determines when the data from the exercise should start logging, namely when
     * the required devices are connected and the user is ready.
     */
    public static boolean START_LOG = false;
    private String exerciseName;                            // Name of the exercise currently in session
    private  String exerciseMode;
    private Button disconnectBtn, nextButton;               // View buttons
    private GifImageView sideImage;                         // Animated GIF specific to exercise
    private TextView loadingText;
    private EditText score_text;
    private String score_str;

    private List<Integer> ids;
    private int current_id;
    private float score;

    // Creates the media recorder object.
    private MediaRecorder myAudioRecorder ;
    // For the output files.
    private String outFile;

    public DeviceExerciseFragment(){

    }
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
        score_text = view.findViewById(R.id.score_value);

        //Create a DataLog object.
        DataLog dataLog;


        ids = new ArrayList<>();

        try{
            ids = UserRepository.getInstance(getActivity().getApplicationContext()).getAllIdentities();
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: ", e);
        }

        current_id = ids.size();

        //Log.d(TAG, "onCreateView: current ID Device exer gra " + Integer.toString(current_id));

        List<Integer> ident = new ArrayList<>();
        try {
            ident  = UserRepository.getInstance(getActivity().getApplicationContext()).getAllIdentities();
        }
        catch(Exception e){
            Log.d(TAG, "onClick: Error with identities");
        }


        // TODO: Countdown timer:--
        // Starts logging if the devices are connected
        if(MainActivity.CONNECTED){
            //startTimer();
            //startTimerMedia();
        }

        // Gets the exercise name from the Main Activity
        if(MainActivity.exercise_name != null) {
            exerciseName = MainActivity.exercise_name;

            if(exerciseName.equals("Resting_Hands_on_Thighs")){

                /*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
                /*    Variables are for the media recording feature.   */

                //Gets the current date from the Calendar API.
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

                //Gets the current tie from the Calender API built in Java.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                String time = simpleDateFormat.format(calendar.getTime());

                //Folder to hold the recordings.
                String folder_main = "SmartGloveRecordings";

                //Creates the folder SmartGloveRecordings in the android device if the folder with the same name is not already created.
                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (!f.exists()) {
                    f.mkdirs();
                }


                //Sets the output path on the android device to store the media recordings.
                outFile = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/" + folder_main + "/smart_speechSG" + currentDate + time + ".3gp";

                //Create new media recorder instance.
                myAudioRecorder = new MediaRecorder();

                // This is for setting the audio resource i.e. microphone.
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                //Define the output format.
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                //This is magic line don't know what's gong on.
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                //Set the output file paths. Little bit magic here.
                myAudioRecorder.setOutputFile(outFile);
                /*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

            }

            // Initialize the data log obj bu passing in the exercise name.
            //dataLog = new DataLog();

            // Call to the constructor to create files.
          //  dataLog.DataLog(ident.size(),exerciseName, ExerciseInstructionFragment.flag);

        }

        // Call timer for the exercise
        exeTimer(exerciseName);

        Log.d(TAG, "onCreateView: THIS IS THE EXERCISE NAME " + exerciseName);

        // Using the exercise name, set the animated gif to the image corresponding to
        // the current exercise
        sideImage = view.findViewById(R.id.stexercise_side_image);
        if(MainActivity.exercise_name != null)
        {
            setSideViews(exerciseName);
            sideImage.setVisibility(View.INVISIBLE);

            //Init with the constructor.

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
                score_str = score_text.getText().toString();
                //score = Float.parseFloat(score_str);

                boolean empty = false;
                try {
                    score = Float.parseFloat(score_str);
                }
                catch (Exception e){
                    // add exeption

                    empty = true;
                }


                if(empty){
                    Toast.makeText(getActivity(),"Please enter a score!",Toast.LENGTH_SHORT).show();
                }
                else{

                    // TODO make an update call here for the score
                    // TODO make a determination of which exercise this is...
                    if (exerciseName.equals("Finger_Tap"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_fin_tap_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Closed_Grip") )
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_op_cl_score(score,ids.size());
                    }

                    else if (exerciseName.equals("Hand_Flip") )
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_h_flip_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Finger_to_Nose"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_fin_nose_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Hold_Hands_Out"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_handout_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Resting_Hands_on_Thighs"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_h_rest_score(score,ids.size());

                    }
                    else if (exerciseName.equals("Heel_Stomp"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_heel_stmp_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Toe_Tap"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_toe_tap_score(score,ids.size());
                    }
                    else if (exerciseName.equals("Walk_Steps"))
                    {
                        UserRepository.getInstance(getActivity().getApplicationContext()).updateData_gait_score(score,ids.size());
                    }

                    START_LOG = false;

                    ((MainActivity)getActivity()).publish();
                    ((MainActivity)getActivity()).startExercise();
                }
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
                ((MainActivity)getActivity()).RedoInst();

            }
        });

        // Set up loading text

        Log.d(TAG, "onCreateView: Started.");

        return view;
    }

    /*
    *
    *       Media recording feature.
    *      ==========================
    * Limitations:-
    * 1. Limited to SDK version 18.
    * 2. Records data for 10 seconds only no more no less.
    *
    * Author @Shehjar Sadhu completed on Monday June 10th 2019.
    *
    * */

    /*  This function prepares and starts the media recording by enabeling the device microphone. */
    public void start_rec(){

        try {
            //.prepare(), .start() is from the media API of android studio.
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } //Freak out here...
        catch(IllegalStateException ise){
            //No need to print anything.

        } catch (IOException ioe){
            //No need to print anything.


        }
        //Toast a message...
        //Toast.makeText(getContext().getApplicationContext()," Recording started ...... ",Toast.LENGTH_LONG).show();


    }
    /* This function stops the media recording. */
    public void stop_rec(){

        //.prepare(), .start() is from the media API of android studio.
        myAudioRecorder.stop();
        myAudioRecorder.release();
        //sets the Media recorder object to null.
        myAudioRecorder = null;

        //Toast a message...
        //Toast.makeText(getContext().getApplicationContext()," Audio recording ended ...... ",Toast.LENGTH_LONG).show();
    }

    /* Sets the out file dir for the recorded media file.*/
    public void store_rec(){

        // Instansiate  a  MediaPlayer object from Media API.
        MediaPlayer mediaPlayers = new MediaPlayer();

        try {

            //Set the data sources here... with the output file.
            mediaPlayers.setDataSource(outFile); //This freaks out of we don't have a try catch.

            /* This will play the audio and once recording is over. For debugging purposes. */
            //Prepare data.
            //mediaPlayers.prepare();

            //Start to play the audio data.
            //mediaPlayers.start();

        }
        catch(Exception e){
            //No need to print anything.
        }

        //Toast a message...
        //Toast.makeText(getContext().getApplicationContext()," Playing the recording.... ",Toast.LENGTH_LONG).show();

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
        if (name.equals("Finger_Tap"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed_Grip"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand_Flip"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Finger_to_Nose"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Hold_Hands_Out"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Resting_Hands_on_Thighs"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Heel_Stomp"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.WALK_STEPS_GIF);
        }
        else if (name.equals("Toe_Tap"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.WALK_STEPS_GIF);
        }
        else if (name.equals("Walk_Steps"))
        {
            sideImage.setBackgroundResource(StudyInstructionsImage.WALK_STEPS_GIF);
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

    private void  exeTimer(String name){
        if (name.equals("Finger_to_Nose")|| name.equals("Hand_Flip") || name.equals("Closed_Grip") || name.equals("Finger_Tap")
                || name.equals("Hold_Hands_Out")/* ||name.equals("Resting Hands on Thighs")*/ )
        {
            final CountDownTimer exe_Timer = new CountDownTimer(10000, 1000) {
                int countdown = 10;

                @Override
                public void onTick(long l)
                {
                    START_LOG = true;
                    Log.v(TAG, "Tick: " + countdown);

                    // loadingText.setText("" + countdown);
                    loadingText.setText("Collecting data...");

                    countdown--;
                }

                @Override
                public void onFinish()
                {
                   // START_LOG = true;

                    // START_LOG = false;
//                graph.setVisibility(View.VISIBLE);
                    loadingText.setText("Completed");

                    sideImage.setVisibility(View.INVISIBLE);
                }
            };
            exe_Timer.start();
        }
        else if (name.equals("Resting_Hands_on_Thighs")){

          //  Toast.makeText(getContext().getApplicationContext()," Resting Hands on Thighs exer.... ",Toast.LENGTH_LONG).show();

            final CountDownTimer exe_Timer = new CountDownTimer(10000, 1000) {
                int countdown = 10;

                @Override
                public void onTick(long l)
                {
                    START_LOG = true;
                    Log.v(TAG, "Tick: " + countdown);
                    // loadingText.setText("" + countdown);
                    loadingText.setText("Collecting data...");
                    //Starts recording the media once the Resting Hands on Thighs exercise has been started.
                    start_rec();
                    countdown--;
                }

                @Override
                public void onFinish()
                {
                    //START_LOG = true;

                    // START_LOG = false;
//                graph.setVisibility(View.VISIBLE);
                    loadingText.setText("Completed");
                    sideImage.setVisibility(View.INVISIBLE);
                    //Stops recording the media once the Resting Hands on Thighs exercise has been started.
                    stop_rec();
                    //Sets the out file dir recording the media once the Resting Hands on Thighs exercise has been started.
                    store_rec();
                }
            };
            exe_Timer.start();
        }

        else if(name.equals("Heel_Stomp") || name.equals("Toe_Tap")){
            final CountDownTimer exe_Timer1 = new CountDownTimer(8000, 1000) {
                int countdown = 8;

                @Override
                public void onTick(long l)
                {
                    START_LOG = true;

                    Log.v(TAG, "Tick: " + countdown);
                    //loadingText.setText("" + countdown);
                    loadingText.setText("Collecting data...");
                    countdown--;
                }

                @Override
                public void onFinish()
                {

                    //START_LOG = false;
  //                graph.setVisibility(View.VISIBLE);
                    loadingText.setText("Completed");
                    sideImage.setVisibility(View.VISIBLE);
                }
            };
            exe_Timer1.start();
        }
      // TO DO -----  UNLIMITED TIME ADD THIS  ----------------------------
        else if(name.equals("Walk_Steps")){

            final CountDownTimer exe_Timer1 = new CountDownTimer(8000000, 1000) {
                int countdown = 8000;

                @Override
                public void onTick(long l)
                {
                    START_LOG = true;

                    Log.v(TAG, "Tick: " + countdown);
                    //loadingText.setText("" + countdown);
                    loadingText.setText("Collecting data...");
                    countdown--;
                }

                @Override
                public void onFinish()
                {

                    //START_LOG = false;
                    //                graph.setVisibility(View.VISIBLE);
                    loadingText.setText("Completed");
                    sideImage.setVisibility(View.VISIBLE);
                }
            };
            exe_Timer1.start();

        }

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
                if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)||characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC2))
                {
                    /* Check to see which exercise we are on and
                    * then break up the data into different exercises.
                    * Make new csv files for each exercise.
                    * */
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
/*

                    // First Data Set
                    int thumb = (((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF)));
                    int index = (((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF)));


                    // Second Data Set
                    thumb = (((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                    index = (((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));


                    // Third Data Set
                    thumb = (((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));
                    index = (((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF)));
*/


                    Log.d(TAG, "onReceive: Start Log = " + START_LOG);
                }
            }
            else if(action.equals(BluetoothLeConnectionService.GATT_STATE_CONNECTED))
            {
                //startTimer();
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
