package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
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
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;


public class ConnectionSelection extends AppCompatActivity
{
    private static final String TAG = "ConnectionSelection";
    private Context mContext;
    private Button  leftGloveBtn, rightGloveBtn, leftShoeBtn, rightShoeBtn, continueBtn;
    private boolean selectedLG, selectedRG, selectedLS, selectedRS;

    private List<String> deviceAddressList;
    private List<String> deviceTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ab_connection_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_connect_select);
        mContext = this;

        deviceAddressList = new ArrayList<>(4);
        deviceTypeList = new ArrayList<>(4);

        leftGloveBtn = findViewById(R.id.left_glove);
        leftGloveBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
        rightGloveBtn = findViewById(R.id.right_glove);
        rightGloveBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
        leftShoeBtn = findViewById(R.id.left_shoe);
        leftShoeBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
        rightShoeBtn = findViewById(R.id.right_shoe);
        rightShoeBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
        continueBtn = findViewById(R.id.continueBtn);

        leftGloveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clicked on Left Glove Btn");
                selectedLG = !selectedLG;
                if(selectedLG)
                    leftGloveBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                else
                    leftGloveBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
            }
        });

        rightGloveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clicked on Right Glove Btn");
                selectedRG = !selectedRG;
                if(selectedRG)
                    rightGloveBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                else
                    rightGloveBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
            }
        });

        leftShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clicked on Left Shoe Btn");
                selectedLS = !selectedLS;
                if(selectedLS)
                    leftShoeBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                else
                    leftShoeBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
            }
        });

        rightShoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Clicked on Right Shoe Btn");
                selectedRS = !selectedRS;
                if(selectedRS)
                    rightShoeBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                else
                    rightShoeBtn.setBackgroundColor(getResources().getColor(R.color.secondaryText));
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(selectedLG)
                {
                    deviceAddressList.add(GattDevices.LEFT_GLOVE_ADDR);
                    deviceTypeList.add(DeviceType.SMART_GLOVE.toString());
                }
                if(selectedRG)
                {
//                deviceAddressList.add(RIGHT_GLOVE_ADDR);
//                deviceTypeList.add(DeviceType.SMART_GLOVE);
                }
                if(selectedLS)
                {
                    deviceAddressList.add(GattDevices.LEFT_SHOE_ADDR);
                    deviceTypeList.add(DeviceType.SMART_SOCK.toString());
                }
                if(selectedRS)
                {
                    deviceAddressList.add(GattDevices.RIGHT_SHOE_ADDR);
                    deviceTypeList.add(DeviceType.SMART_SOCK.toString());
                }
                ExerciseSelection.start(mContext, deviceAddressList, deviceTypeList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        Log.e(TAG, "Back pressed. Navigating to " + getParentActivityIntent());
        Intent intent = this.getParentActivityIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}
