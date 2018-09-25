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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.UUID;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseInstructionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseSelectionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.FinishFragment;
import andrewpeltier.smartglovefragments.fragments.HomeFragment;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdate;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsUpdateReceiver;
import andrewpeltier.smartglovefragments.tex_tronics.enums.DeviceType;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.visualize.Choice;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,                  // Required for BLE Operations
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // Required for Local I/O Operations
            Manifest.permission.WAKE_LOCK,                  // Required for MQTT Paho Library
            Manifest.permission.ACCESS_NETWORK_STATE,       // Required for MQTT Paho Library
            Manifest.permission.INTERNET                    // Required for MQTT Paho Library
    };
    private static final int PERMISSION_CODE = 111;

    private boolean isDoctor = false;
    private static String[] deviceAddressList;
    private String[] deviceTypeList;
    private static String[] exerciseChoices;
    private String[] exerciseModes;
    private ArrayDeque<String> mNames;
    private UUID mRoutineID;
    public static String exercise_name;
    public static boolean connected;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String mFragmentTag;

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


    public void addFragment(Fragment fragment, String tag)
    {
        // Replaces the current fragment with the fragment parameter
        Log.d(TAG, "addFragment: Adding fragment " + tag);
        mFragmentTag = tag;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();
    }

    public void startExercise()
    {
        // Stores the name of the next exercise and starts the instruction fragment
        if(mNames.size() > 0)
        {
            exercise_name = mNames.pop();
            if(exercise_name != null)
                addFragment(new ExerciseInstructionFragment(), "ExerciseInstructionFragment");
        }
        // No exercises left
        else {
            addFragment(new FinishFragment(), "FinishFragment");
        }
    }

    public void setIsDoctor(boolean doctor)
    {
        Log.v(TAG, "setIsDoctor: Doctor is set to " + doctor);
        isDoctor = doctor;
    }
    public boolean getIsDoctor()
    {
        return isDoctor;
    }

    /**
     *  ========= Tex Tronics Data Members =========
     */


    public void setDeviceLists(String[] deviceAddresses, String[] deviceTypes)
    {
        deviceAddressList = deviceAddresses;
        deviceTypeList = deviceTypes;
        Log.d(TAG, "setDeviceLists: Devices set");
    }

    public void setExercises(String[] chosenExercises, String[] exerciseModeArray)
    {
        exerciseChoices = chosenExercises;
        exerciseModes = exerciseModeArray;
        mNames = new ArrayDeque<>(Arrays.asList(exerciseChoices));
        mRoutineID = UUID.randomUUID();
        Log.d(TAG, "setExercises: Exercises set");
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


    public void connect()
    {
        UUID exerciseID = UUID.randomUUID();
        for(int i = 0; i < deviceAddressList.length; i++) {
            TexTronicsManagerService.connect(this,
                    deviceAddressList[i],
                    Choice.getChoice(exercise_name),
                    ExerciseMode.getExercise(exerciseModes[0]),
                    DeviceType.getDevicetype(deviceTypeList[i]), exerciseID, mRoutineID);
        }
        connected = true;
    }

    public void disconnect()
    {
        for(String address : deviceAddressList)
        {
            TexTronicsManagerService.disconnect(this, address);
        }
        connected = false;
    }

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

    private TexTronicsUpdateReceiver mTexTronicsUpdateReceiver = new TexTronicsUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null || !intent.hasExtra(UPDATE_DEVICE) || !intent.hasExtra(UPDATE_TYPE)) {
                Log.w(TAG,"Invalid Update Received");
                return;
            }

            String deviceAddress = intent.getStringExtra(UPDATE_DEVICE);    // NULL if MQTT Update
            TexTronicsUpdate updateType = (TexTronicsUpdate) intent.getSerializableExtra(UPDATE_TYPE);

            if(updateType == null) {
                Log.w(TAG,"NULL Update Received");
                return;
            }

            switch (updateType) {
                case ble_connecting:
                    // Connecting to Device <deviceAddress>
                    Log.d(TAG,"Connecting to " + deviceAddress);
                    break;
                case ble_connected:
                    // Device <deviceAddress> Has Been Connected
                    Log.d(TAG,"Connected to " + deviceAddress);
                    break;
                case ble_disconnecting:
                    // Disconnecting from Device <deviceAddress>
                    Log.d(TAG,"Disconnecting from " + deviceAddress);
                    break;
                case ble_disconnected:
                    // Device <deviceAddress> Has Been Disconnected
                    Log.d(TAG,"Disconnected from " + deviceAddress);
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
        }
    };

    /**
     *  ========= Override Methods =========
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

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister the TexTronics Update Receiver
        unregisterReceiver(mTexTronicsUpdateReceiver);

        // Stop the TexTronics Manager Service
        TexTronicsManagerService.stop(this);
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed: back pressed");
        if(mFragmentTag.equals("ExerciseSelectionFragment"))
            addFragment(new HomeFragment(), "HomeFragment");
        else if(mFragmentTag.equals("HomeFragment"))
            super.onBackPressed();
        else if(mFragmentTag.equals("PatientFeedFragment")||
                mFragmentTag.equals("RoutineCreateFragment")||
                mFragmentTag.equals("CreateProfileFragment"))
            addFragment(new HomeFragment(), "HomeFragment");
        else
        {
            Log.d(TAG, "onBackPressed: navigating back to exercise selection...");
            disconnect();
            addFragment(new ExerciseSelectionFragment(), "ExerciseSelectionFragment");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO: Handle event Permissions denied
    }
}
