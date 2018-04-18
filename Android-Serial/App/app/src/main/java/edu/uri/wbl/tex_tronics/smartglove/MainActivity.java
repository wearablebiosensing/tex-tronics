package edu.uri.wbl.tex_tronics.smartglove;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsManagerService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdate;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsUpdateReceiver;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;

/**
 * A simple Activity demonstrating how to use the features provided by TexTronics Manager Service.
 */
public class MainActivity extends AppCompatActivity {
    private final static String TAG = "Demo";

    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,                  // Required for BLE Operations
            Manifest.permission.WRITE_EXTERNAL_STORAGE,     // Required for Local I/O Operations
            Manifest.permission.WAKE_LOCK,                  // Required for MQTT Paho Library
            Manifest.permission.ACCESS_NETWORK_STATE,       // Required for MQTT Paho Library
            Manifest.permission.INTERNET                    // Required for MQTT Paho Library
    };
    private static final int PERMISSION_CODE = 111;

    private Context mContext;
    private CoordinatorLayout mMessageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Data Members
        mContext = this;            // Used by any method calls requiring a Context argument
        mMessageContainer = findViewById(R.id.message_container);

        Button connectLeftShoeBtn = findViewById(R.id.connect_btn1);
        connectLeftShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TexTronicsManagerService.connect(mContext, DeviceList.LEFT_SHOE, ExerciseMode.FLEX_ONLY, DeviceType.SMART_SOCK);
            }
        });

        Button connectRightShoeBtn = findViewById(R.id.connect_btn2);
        connectRightShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TexTronicsManagerService.connect(mContext, DeviceList.RIGHT_SHOE, ExerciseMode.FLEX_ONLY, DeviceType.SMART_SOCK);
            }
        });

        Button disconnectLeftShoeBtn = findViewById(R.id.disconnect_btn1);
        disconnectLeftShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TexTronicsManagerService.disconnect(mContext, DeviceList.LEFT_SHOE);
            }
        });

        Button disconnectRightShoeBtn = findViewById(R.id.disconnect_btn2);
        disconnectRightShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TexTronicsManagerService.disconnect(mContext, DeviceList.RIGHT_SHOE);
            }
        });
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

    private void showMessage(String message) {
        Snackbar.make(mMessageContainer, message, Snackbar.LENGTH_LONG).show();
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
                    showMessage("Connecting to " + deviceAddress);
                    break;
                case ble_connected:
                    // Device <deviceAddress> Has Been Connected
                    Log.d(TAG,"Connected to " + deviceAddress);
                    showMessage("Connected to " + deviceAddress);
                    break;
                case ble_disconnecting:
                    // Disconnecting from Device <deviceAddress>
                    Log.d(TAG,"Disconnecting from " + deviceAddress);
                    showMessage("Disconnecting from " + deviceAddress);
                    break;
                case ble_disconnected:
                    // Device <deviceAddress> Has Been Disconnected
                    Log.d(TAG,"Disconnected from " + deviceAddress);
                    showMessage("Disconnected from " + deviceAddress);
                    break;
                case mqtt_connected:
                    // Connected to MQTT Server
                    Log.d(TAG,"Connected to MQTT Server");
                    showMessage("Connected to MQTT Server");
                    break;
                case mqtt_published:
                    // Successfully Published Data
                    Log.d(TAG,"Published Data");
                    showMessage("Published Data");
                case mqtt_disconnected:
                    // Disconnected from MQTT Server
                    Log.d(TAG,"Disconnected from MQTT Server");
                    showMessage("Disconnected from MQTT Server");
                    break;
                default:
                    Log.w(TAG, "Unknown Update Received");
                    break;
            }
        }
    };
}
