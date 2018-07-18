package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.io.SmartGloveInterface;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsManagerService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdate;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdateReceiver;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;

/**
 * A simple Activity demonstrating how to use the features provided by TexTronics Manager Service.
 */
public class MainActivity extends AppCompatActivity implements SmartGloveInterface
{
    private final static String TAG = "MainActivity";

    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,                  // Required for BLE Operations
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // Required for Local I/O Operations
            Manifest.permission.WAKE_LOCK,                  // Required for MQTT Paho Library
            Manifest.permission.ACCESS_NETWORK_STATE,       // Required for MQTT Paho Library
            Manifest.permission.INTERNET                    // Required for MQTT Paho Library
    };
    private static final int PERMISSION_CODE = 111;

    private Context mContext;
    private Button beginButton;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Data Members
        mContext = this;            // Used by any method calls requiring a Context argument

//        ProcessData.setContext(mContext);
//        GatherCSVData.setDeviceContext(mContext);

        beginButton = (Button) findViewById(R.id.start_button);
        beginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Code that checks to see if the glove is working correctly
                //  and if the user has inputted a name for themselves.

                //Gets the user inputted name
                SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
                String name = sharedPreferences.getString(Preferences.NAME, "");

                //If glove is working correctly and if name is selected then launch exercise selection
                if(name != "")
                {
                    Log.v(TAG, "Launching ExerciseSelection Activity...");
                    Intent intent = new Intent(mContext, ConnectionSelection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                // If name is not inputted
                else
                {
                    Toast.makeText(MainActivity.this, "Please set patient name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        logo = findViewById(R.id.logo);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.logofadein);
        logo.startAnimation(fadeInAnimation);

    }

    /** confirmPatient()
     *
     * Sets up a dialog box that saves the user's inputted name to the app cache.
     *
     * If a name is already saved, the dialog box will ask the user if they want to continue
     * as the user that is currently selected.
     *
     * If no name is saved, or if the user wishes to change the name, a dialog box will ask
     * the user to type in a name and save that name to the app cache.
     */
    private void confirmPatient()
    {
        //  Gets the name
        final SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        String name = sharedPreferences.getString(Preferences.NAME, "");

        //  Name is blank
        if(name == "")
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Patient Name");
            builder.setMessage("Please enter your name: ");

            final EditText input = new EditText(mContext);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(input);

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Preferences.NAME, input.getText().toString());
                    editor.commit();
                    Toast.makeText(mContext, "Name Saved.", Toast.LENGTH_SHORT);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        // Name is entered
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Patient Name");
            builder.setMessage("Continue as " + sharedPreferences.getString(Preferences.NAME, "") + "?");

            //User wants to change the name
            builder.setPositiveButton("Change Name", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //Erases name, then recursively calls this method
                    editor.putString(Preferences.NAME, "");
                    editor.commit();
                    confirmPatient();
                }
            });

            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    private void confirmDisconnect()
    {
//        if(!SmartGloveManagerService.READY)
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("Disconnect");
//            builder.setMessage("Smart Glove is not currently connected.");
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//            {
//                @Override
//                public void onClick(DialogInterface dialog, int which)
//                {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
//        }
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("Disconnect");
//            builder.setMessage("Disconnect from Smart Glove?");
//
//            //User wants to change the name
//            builder.setPositiveButton("Disconnect", new DialogInterface.OnClickListener()
//            {
//                @Override
//                public void onClick(DialogInterface dialog, int which)
//                {
//                    SmartGloveManagerService.disconnect(context);
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//            {
//                @Override
//                public void onClick(DialogInterface dialog, int which)
//                {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
//        }
    }

    /** onCreateOptionsMenu
     *
     * Creates a menu at the top right side of the application that allows
     * the user to connect to and disconnect from a Smart Glove device.
     *
     * It also allows the user to keep, change, or set their username.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /** onOptionsItemSelected
     *
     * Called when the user selects an item from the options menu
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Connects to a device
            case R.id.reconnect:
                // Attempts to reconnect to device
                return true;
            // Reveals disconnect options via a dialog box
            case R.id.ble_disconnect:
                confirmDisconnect();
                return true;
            // Reveals username options via a dialog box
            case R.id.change_name:
                confirmPatient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        TexTronicsManagerService.start(mContext);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister the TexTronics Update Receiver
        unregisterReceiver(mTexTronicsUpdateReceiver);

        // Stop the TexTronics Manager Service
        TexTronicsManagerService.stop(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO: Handle event Permissions denied
    }

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
}
