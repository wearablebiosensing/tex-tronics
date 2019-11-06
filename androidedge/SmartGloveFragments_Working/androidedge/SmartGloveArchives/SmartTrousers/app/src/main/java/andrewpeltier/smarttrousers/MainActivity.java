package andrewpeltier.smarttrousers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

import andrewpeltier.smarttrousers.ble.GattDevices;
import andrewpeltier.smarttrousers.fragments.DataStreamFrag;
import andrewpeltier.smarttrousers.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smarttrousers.tex_tronics.TexTronicsUpdate;
import andrewpeltier.smarttrousers.tex_tronics.TexTronicsUpdateReceiver;
import andrewpeltier.smarttrousers.tex_tronics.enums.DeviceType;
import andrewpeltier.smarttrousers.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smarttrousers.visualize.Choice;

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
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private UUID mRoutineID;
    public static String exercise_name;
    public static boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        // Initially load the fragment container with the home fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new DataStreamFrag(), "DataStreamFrag");
        fragmentTransaction.commit();
    }

    /**
     *  ========= Tex Tronics Manager Service Methods =========
     */


    public void connect()
    {
        UUID exerciseID = UUID.randomUUID();
        mRoutineID = UUID.randomUUID();
        TexTronicsManagerService.connect(this,
                GattDevices.ST_ADDR,
                Choice.getChoice(),
                ExerciseMode.getExercise(ExerciseMode.FLEX_ONLY.toString()),
                DeviceType.getDevicetype(DeviceType.SMART_GLOVE.toString()), exerciseID, mRoutineID);
        connected = true;
    }

    public void disconnect()
    {
        TexTronicsManagerService.disconnect(this, GattDevices.ST_ADDR);
        connected = false;
    }

    public void publish()
    {
        TexTronicsManagerService.publish(this, GattDevices.ST_ADDR);
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
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO: Handle event Permissions denied
    }
}
