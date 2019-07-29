package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsExerciseManager;

public class ScreenTapActivity extends AppCompatActivity
{
    private static final String TAG = "ScreenTapActivity";
    private static final int    REQUIRED_TAPS = 10;
    private Context context;
    private TextView screenTapCount;
    private ConstraintLayout constraintLayout;
    private int         count = 0;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceBundle)
    {
        Log.e(TAG, "Creating ScreenTap Activity...");
        super.onCreate(savedInstanceBundle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_screen_tap);
        setTitle("Screen Tap");

        context = this;
        startTime = System.currentTimeMillis();
        screenTapCount = (TextView) findViewById(R.id.screen_tap_count);
        constraintLayout = findViewById(R.id.screenTapLayout);
    }

    public void screenTapped(View view)
    {
        count++;
        if(count < REQUIRED_TAPS)
        {
            screenTapCount.setText("" + count);
        }
        else
        {
//                    GatherCSVData.writeScreenTap((System.currentTimeMillis() - startTime) / 1000);
            // ExerciseSelection.exerciseComplete(4);
//            Intent intent = new Intent(this, GloveExerciseActivity.class);
//            TexTronicsExerciseManager.startExercise(intent, context);
            TexTronicsExerciseManager.startExercise(context);
        }
    }


    @Override
    public void onBackPressed()
    {
        Log.e(TAG, "Back pressed. Navigating to " + getParentActivityIntent());
        List<String> addressList = Arrays.asList(TexTronicsExerciseManager.getmDeviceAddressList());
        List<String> devicesList = Arrays.asList(TexTronicsExerciseManager.getmDeviceTypeList());

        ExerciseSelection.start(context, addressList, devicesList);
        super.onBackPressed();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

}
