package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsManagerService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;


public class ConnectionSelection extends AppCompatActivity
{
    private Context mContext;
    private Button  leftGloveBtn, rightGloveBtn, leftShoeBtn, rightShoeBtn, continueBtn;

    private List<String> deviceAddressList;
    private List<String> deviceTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_picker);
        mContext = this;

        deviceAddressList = new ArrayList<>(4);
        deviceTypeList = new ArrayList<>(4);

        leftGloveBtn = findViewById(R.id.left_glove);
        rightGloveBtn = findViewById(R.id.right_glove);
        leftShoeBtn = findViewById(R.id.left_shoe);
        rightShoeBtn = findViewById(R.id.right_shoe);
        continueBtn = findViewById(R.id.continueBtn);

        leftGloveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DEMO", "Clicked on Left Glove Btn");
                deviceAddressList.add(GattDevices.LEFT_GLOVE_ADDR);
                deviceTypeList.add(DeviceType.SMART_GLOVE.toString());
            }
        });

        rightGloveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DEMO", "Clicked on Right Glove Btn");
//                deviceAddressList.add(RIGHT_GLOVE_ADDR);
//                deviceTypeList.add(DeviceType.SMART_GLOVE);
            }
        });

        leftShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DEMO", "Clicked on Left Shoe Btn");
                deviceAddressList.add(GattDevices.LEFT_SHOE_ADDR);
                deviceTypeList.add(DeviceType.SMART_SOCK.toString());
            }
        });

        rightShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DEMO", "Clicked on Right Shoe Btn");
                deviceAddressList.add(GattDevices.RIGHT_SHOE_ADDR);
                deviceTypeList.add(DeviceType.SMART_SOCK.toString());
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseSelection.start(mContext, deviceAddressList, deviceTypeList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
