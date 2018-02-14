package edu.uri.wbl.tex_tronics.smartglove;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.uri.wbl.tex_tronics.smartglove.smart_glove.SmartGloveManagerService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 034);
        }

        Button connectBtn = findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartGloveManagerService.connect(mContext, GattDevices.SMART_GLOVE_DEVICE);
            }
        });

        Button disconnectBtn = findViewById(R.id.disconnect_btn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartGloveManagerService.disconnect(mContext, GattDevices.SMART_GLOVE_DEVICE);
            }
        });

        Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Started scan from Main");
                SmartGloveManagerService.scan(mContext);
            }
        });
    }
}
