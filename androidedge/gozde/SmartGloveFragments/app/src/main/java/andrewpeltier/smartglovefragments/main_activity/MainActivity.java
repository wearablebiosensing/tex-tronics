package andrewpeltier.smartglovefragments.main_activity;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.UUID;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseInstructionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseSelectionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.FinishFragment;
import andrewpeltier.smartglovefragments.fragments.HomeFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.StudyFinishFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.StudySuccessFragment;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdate;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdateReceiver;
import andrewpeltier.smartglovefragments.tex_tronics.enums.DeviceType;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.visualize.Choice;
import andrewpeltier.smartglovefragments.visualize.StudyChoice;

import static andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService.deviceAddress;


/** ======================================
 *
 *          MainActivity Class
 *
 *  ======================================
 *
 *      The main activity which is called when the app is started and holds the fragments that
 *  handle all user interactions. This communicates to each fragment it holds, so it
 *  also handles the communication from the TexTronics Update receiver, which handles the
 *  database and ble connections, to an appropriate fragment.
 *
 *
 *
 * @author Andrew Peltier
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    // Holds each of the Android permissions that are required to use the application
    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,                  // Required for BLE Operations
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // Required for Local I/O Operations
            Manifest.permission.WAKE_LOCK,                  // Required for MQTT Paho Library
            Manifest.permission.ACCESS_NETWORK_STATE,       // Required for MQTT Paho Library
            Manifest.permission.INTERNET                    // Required for MQTT Paho Library
    };
    private static final int PERMISSION_CODE = 111;
    private boolean isDoctor = false;                   // Confirms patient or doctor user
    private static String[] deviceAddressList;          // List of devices held by the application
    private String[] deviceTypeList;                    // List of the types of devices held
    private static String[] exerciseChoices;            // List of exercise choices once picked
    private String[] exerciseModes;                     // List of exercise modes
    private ArrayDeque<String> mNames;                  // String name of each exercise in list
    private ArrayDeque<String> mModes;
    private UUID mRoutineID;                            // ID of the routine, set randomly
    public static String exercise_name;                 // Name of the current exercise
    public static String exercise_mode;
    public static boolean CONNECTED;                    // Checks for BLE connection
    private FragmentManager fragmentManager;            // Manages the fragments held by the Main Activity
    private FragmentTransaction fragmentTransaction;    // Changes the fragments
    private String mFragmentTag;                        // Name of the fragment currently in use
    String deviceAddress;
    public static int counter =0;
    public static int DeviceConection = 0;


    /** onCreate()
     *
     * Called when the main activity is created upon the initialization of
     * the application. For our purposes, we just have it load our fragment
     * manager with the home screen fragment.
     *
     * @param savedInstanceState        -Data from the current fragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        // Initially load the fragment container with the home fragment
        fragmentManager = getSupportFragmentManager();
        mFragmentTag = "HomeFragment";
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new HomeFragment(), "HomeFragment");
        fragmentTransaction.commit();


    }

    /**
     *  ========= Fragment Management =========
     */

    /** addFragment()
     *
     * Replaces the current fragment with the input fragment parameter. This
     * is called whenever the user switches to a new page of the application.
     *
     * @param fragment      -The fragment that will replace the current one
     * @param tag           -The name of the new fragment
     */
    public void addFragment(Fragment fragment, String tag)
    {
        Log.d(TAG, "addFragment: Adding fragment " + tag);
        mFragmentTag = tag;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();
    }

    /** startExercise()
     *
     * Looks at the list of exercises and starts the next exercise in the list, starting
     * with the instructions page. If there are no more exercises in the list, then the
     * user has completed all of their exercises, and the finish screen is launched instead.
     *
     */
    public void startExercise()
    {
        // If there is another exercise, launch its instructions
        if(mNames.size() > 0)
        {
            // The name is stored so that we can load the proper exercise and instructions
            exercise_name = mNames.pop();
            exercise_mode = mModes.pop();

            if(exercise_name != null)
                addFragment(new ExerciseInstructionFragment(), "ExerciseInstructionFragment");

        }
        // No exercises left, go to the finish screen
        else {
            addFragment(new StudyFinishFragment(), "StudyFinishFragment");
//            addFragment(new StudySuccessFragment(), "StudySuccessFragment");
        }
    }

    public void StartSucess(){
        addFragment(new StudySuccessFragment(), "StudySuccessFragment");
    }

    public void RedoInst(){
        addFragment(new ExerciseInstructionFragment(), "ExerciseInstructionFragment");
    }

    /** setIsDoctor()
     *
     * Changes the isDoctor boolean to reflect whether or not the doctor interface
     * of the application should currently be used. If a doctor's credentials are
     * recognized when the user logs in to the application, then this is set to true,
     * and the doctor interface will be accessed. This will be changed to false when the
     * doctor logs off.
     *
     * @param doctor
     */
    public void setIsDoctor(boolean doctor)
    {
        Log.v(TAG, "setIsDoctor: Doctor is set to " + doctor);
        isDoctor = doctor;
    }

    // Always returns false, want to disable doctor mode for the clinical trial
    public boolean getIsDoctor()
    {
        //return isDoctor;
        return false;
    }

    /**
     *  ========= Tex Tronics Data Members =========
     */


    /** setDeviceLists()
     *
     * Changes the devices inside each of our device lists. This is called in the
     * Exercise Instructions Fragment, which sends as parameters the devices and the
     * device types required to complete the current exercise.
     *
     * @param deviceAddresses       -MAC addresses of the required devices
     * @param deviceTypes           -Types of the required devices
     */
    public void setDeviceLists(String[] deviceAddresses, String[] deviceTypes)
    {
        deviceAddressList = deviceAddresses;
        deviceTypeList = deviceTypes;
        Log.d(TAG, "setDeviceLists: Devices set");
    }

    /** setExercises()
     *
     * Called by the ExerciseSelectionFragment. The list of chosen exercises and their
     * corresponding modes are used to set our lists in the MainActivity.
     *
     * @param chosenExercises           -Each exercise the user has in their playlist upon
     *                                  completing the ExerciseSelectionFragment
     * @param exerciseModeArray         -The mode of each exercise in that list
     */
    public void setExercises(String[] chosenExercises, String[] exerciseModeArray)
    {
        exerciseChoices = chosenExercises;
        exerciseModes = exerciseModeArray;
        //mModes =  new ArrayDeque<>(Arrays.asList(exerciseModes));
        mNames = new ArrayDeque<>(Arrays.asList(exerciseChoices));
        // A new routine has just been created, so we create a random ID for
        // this new routine
        mRoutineID = UUID.randomUUID();
        Log.d(TAG, "setExercises: Exercises set");
    }
    public void setModes(String[] chosenExercises, String[] exerciseModeArray){

        //exerciseChoices = chosenExercises;
        exerciseModes = exerciseModeArray;
        mModes =  new ArrayDeque<>(Arrays.asList(exerciseModes));
       // mNames = new ArrayDeque<>(Arrays.asList(exerciseChoices));
        // A new routine has just been created, so we create a random ID for
        // this new routine
        mRoutineID = UUID.randomUUID();
        Log.d(TAG, "setModes: Exercises MODE set");

    }


    public static int getExerciseCount()
    {
        if(exerciseChoices != null)
            return exerciseChoices.length;
        else
            return 0;
    }

    public static String[] getmDeviceAddressList() {
        return deviceAddressList;
    }

    public static String[] getmExerciseChoices()
    {
        return exerciseChoices;
    }


    /**
     *  ========= Tex Tronics Manager Service Methods =========
     */


    /** connect()
     *
     * Provides the TexTronics Manager Service the necessary information to connect to
     * each each device in our list of devices. Our list of devices will change depending on
     * the exercise that the user is currently on, either to two gloves or two shoes. This
     * is called primarily in the ExerciseInstructionFragment.
     *
     */
    public void connect()
    {
        UUID exerciseID = UUID.randomUUID();
        for(int i = 0; i < deviceAddressList.length; i++) {
            /*Call creating file tings here......*/

            TexTronicsManagerService.connect_devices(this,
                    deviceAddressList[i],
                    Choice.getChoice(exercise_name),
                    //StudyChoice.getChoice(exercise_name),
                    ExerciseMode.getExercise(exerciseModes[i]),//exerciseModes[i]
                    DeviceType.getDevicetype(deviceTypeList[i]), exerciseID, mRoutineID);
        }
    }


    /** disconnect()
     *
     * Disconnects from each connected device in our device list.
     *
     */
    public void disconnect()
    {
        for(String address : deviceAddressList)
        {
            TexTronicsManagerService.disconnect(this, address);
        }
        CONNECTED = false;
    }

    /** publish()
     *
     * Provides the TexTronics Manager Service with the device addresses of each
     * connected device so that its exercise information can have its exercise information
     * published to the MQTT server / CSV file.
     *
     */
    public void publish()
    {
        if(deviceAddressList != null)
        {
            for(String address : deviceAddressList)
                TexTronicsManagerService.publish(this, address);
        }
    }

    /**
     *  ========= TexTronics Update Receiver =========
     */

    private TexTronicsUpdateReceiver mTexTronicsUpdateReceiver = new TexTronicsUpdateReceiver()
    {

        /** onReceive()
         *
         * Called automatically once there is a change of state with either the connection to a
         * BLE device or the MQTT receiver. Here, we only log the update to the console.
         *
         * @param context           -Current state of the application
         * @param intent            -Operation to be performed. Here, it contains the type of update
         */
        @Override

        public void onReceive(Context context, Intent intent) {

            if(intent == null || !intent.hasExtra(UPDATE_DEVICE) || !intent.hasExtra(UPDATE_TYPE)) {
                Log.w(TAG,"Invalid Update Received");
                return;
            }

            deviceAddress = intent.getStringExtra(UPDATE_DEVICE);    // NULL if MQTT Update
            TexTronicsUpdate updateType = (TexTronicsUpdate) intent.getSerializableExtra(UPDATE_TYPE);

            if(updateType == null) {
                Log.w(TAG,"NULL Update Received");
                return;
            }

            // Print update to console
            switch (updateType)
            {
                case ble_connecting:
                    // Connecting to Device <deviceAddress>
                    Log.d(TAG,"Connecting to " + deviceAddress);
                    break;
                case ble_connected:
                    // Device <deviceAddress> Has Been Connected
                    Log.d(TAG,"Connected to " + deviceAddress);
                    counter++;
                    break;
                case ble_disconnecting:
                    // Disconnecting from Device <deviceAddress>
                    Log.d(TAG,"Disconnecting from " + deviceAddress);


                    break;
                case ble_disconnected:
                    // Device <deviceAddress> Has Been Disconnected
                    Log.d(TAG,"Disconnected from " + deviceAddress);
                    counter--;
                    break;
                case mqtt_connected:
                    // Connected to MQTT Server
                    Log.d(TAG,"Connected to MQTT Server");
                    break;
                case mqtt_disconnected:
                    // Disconnected from MQTT Server
                    Log.d(TAG,"Disconnected from MQTT Server");
                    break;
                default:
                    Log.w(TAG, "Unknown Update Received");
                    break;
            }

            if(counter == 4){
                TextView connectionText  = findViewById(R.id.connectionText);

                connectionText.setText("CONNECTED TO ALL DEVICES.");
                Toast.makeText(context,"CONNECTED TO ALL DEVICES!",Toast.LENGTH_LONG).show();

            }

        }
    };

    /**
     *  ========= Override Methods =========
     */

    /** onStart()
     *
     * Called when the application first starts. This requests permission to use
     * your phone's protected features, like Bluetooth, wifi, and data storage.
     * Additionally, we register the TexTronics receiver to the MainActivity, and
     * start connecting to the MQTT server with the manager service.
     *
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Check Permissions at Runtime (Android M+), and Request if Necessary
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // The Results from this Request are handled in a Callback Below.
            requestPermissions(PERMISSIONS, PERMISSION_CODE);
        }

        // Register TexTronics Update Receiver - This is how this Activity will Receive Data from the TexTronics Manager Service
        registerReceiver(mTexTronicsUpdateReceiver, TexTronicsUpdateReceiver.INTENT_FILTER);

        // Start the TexTronics Manager Service
        TexTronicsManagerService.start(this);
    }

    /** onStop()
     *
     * Called when the application stops (inactive for a long period of time or
     * gets killed). We essentially disconnect from everything, being the BLE devices
     * and the MQTT server.
     *
     */
    @Override
    protected void onStop() {
        super.onStop();

        // Unregister the TexTronics Update Receiver
        unregisterReceiver(mTexTronicsUpdateReceiver);

        // Stop the TexTronics Manager Service
        TexTronicsManagerService.stop(this);
    }

    /** onBackPressed()
     *
     * Called when the smartphone or tablet's back button is pressed. This will
     * load the previously visited fragment to the main activity, replacing the
     * current one.
     *
     * @since 1.0
     */
    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed: back pressed");

        // Exercise Selection's previous page is the Home Page
        if(mFragmentTag.equals("ExerciseSelectionFragment"))
            addFragment(new HomeFragment(), "HomeFragment");

        // Back button on HomeFragment just exits app
        else if(mFragmentTag.equals("HomeFragment"))
            super.onBackPressed();

        // For every doctor page, the back button goes back home
        else if(mFragmentTag.equals("PatientFeedFragment")||
                mFragmentTag.equals("RoutineCreateFragment")||
                mFragmentTag.equals("CreateProfileFragment"))
            addFragment(new HomeFragment(), "HomeFragment");

        // For instructions or exercises, the back button sends the user back to selection fragment
        else
        {
            Log.d(TAG, "onBackPressed: navigating back to exercise selection...");
            disconnect();
            addFragment(new HomeFragment(), "HomeFragment");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO: Handle event Permissions denied
    }
}
