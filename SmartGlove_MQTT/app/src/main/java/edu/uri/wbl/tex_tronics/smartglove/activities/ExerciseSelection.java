package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.DeviceType;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;

public class ExerciseSelection extends AppCompatActivity
{
    private static final String TAG = "ExerciseSelection";
    private static final String EXTRA_DEVICE_ADDRS = "uri.wbl.tex_tronics.devices";
    private static final String EXTRA_DEVICE_TYPES = "uri.wbl.tex_tronics.device_types";
    private static final String EXTRA_EXERCISE_MODES = "uri.wbl.tex_tronics.exercise_modes";
    private static boolean  fingerTapComplete = false,
                            closedGripComplete = false,
                            handFlipComplete = false,
                            screenTapComplete = false;

    private ImageButton fingerTapButton,
                        closedGripButton,
                        handFlipButton,
                        screenTapButton;

    private String[] deviceAddressList;
    private String[] deviceTypeList;
    private List<String> exerciseModes;
    private static int exerciseSelection;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Sets up activity
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.exercise_selection_layout);
        context = this;
        Log.e(TAG, "Creating ExerciseSelection Activity...");

        deviceAddressList = getIntent().getStringArrayExtra(EXTRA_DEVICE_ADDRS);
        for(String addr : deviceAddressList) {
            Log.d("DEMO", "1 BT ADDRESS: " + addr);
        }
        deviceTypeList = getIntent().getStringArrayExtra(EXTRA_DEVICE_TYPES);
        for(String type : deviceTypeList) {
            Log.d("DEMO", "1 DEVICE TYPE: " + type);
        }
        exerciseModes = new ArrayList<>();

        // Checks to see which exercises are complete
        checkCompletion();

        // Sets up image buttons
        fingerTapButton = (ImageButton) findViewById(R.id.fingertap_button);
        closedGripButton = (ImageButton) findViewById(R.id.closedgrip_button);
        handFlipButton = (ImageButton) findViewById(R.id.handflip_button);
        screenTapButton = (ImageButton) findViewById(R.id.screentap_button);

        // Greys out image buttons if corresponding exercise is complete
        if(fingerTapComplete)
            fingerTapButton.setBackgroundColor(Color.GRAY);
        if(closedGripComplete)
            closedGripButton.setBackgroundColor(Color.GRAY);
        if(handFlipComplete)
            handFlipButton.setBackgroundColor(Color.GRAY);
        if(screenTapComplete)
            screenTapButton.setBackgroundColor(Color.GRAY);

        // Sets click listeners
        fingerTapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!fingerTapComplete)
                {
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    launchInstructionActivity(1);
                }
            }
        });

        closedGripButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!closedGripComplete)
                {
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    launchInstructionActivity(2);
                }
            }
        });

        handFlipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!handFlipComplete)
                {
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    launchInstructionActivity(3);
                }
            }
        });

        screenTapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!screenTapComplete)
                {
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    launchInstructionActivity(4);
                }
            }
        });
    }

    public static void start(Context context, @NonNull List<String> deviceAddressList, @NonNull List<String> deviceTypeList) {
        Intent intent = new Intent(context, ExerciseSelection.class);
        String[] deviceAddresses = deviceAddressList.toArray(new String[deviceAddressList.size()]);
        String[] deviceTypes = deviceTypeList.toArray(new String[deviceTypeList.size()]);
        intent.putExtra(EXTRA_DEVICE_ADDRS, deviceAddresses);
        intent.putExtra(EXTRA_DEVICE_TYPES, deviceTypes);
        for(String addr : deviceAddressList) {
            Log.d("DEMO", "BT ADDRESS: " + addr);
        }
        context.startActivity(intent);
    }

    private void launchInstructionActivity(int selection)
    {
        Log.v(TAG, "Launching GloveExercise Activity...");
        exerciseSelection = selection;
        Intent intent = new Intent(context, GloveExerciseActivity.class);
        String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);
        intent.putExtra(EXTRA_DEVICE_ADDRS, deviceAddressList);
        intent.putExtra(EXTRA_DEVICE_TYPES, deviceTypeList);
        intent.putExtra(EXTRA_EXERCISE_MODES, exerciseModeArray);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void checkCompletion()
    {
        Log.e(TAG, "Checking Completion...");
//        if(fingerTapComplete && closedGripComplete && handFlipComplete && screenTapComplete)
//        {
//            // All activities are completed
//        Log.v(TAG, "Launching Finish Activity...");
//        Intent intent = new Intent(context, FinishActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        }
        if(screenTapComplete && fingerTapComplete)
        {
            Log.v(TAG, "Launching Finish Activity...");
            Intent intent = new Intent(context, FinishActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        Log.e(TAG, "Not all activities are completed.");
    }

    public void resetCompletion()
    {
        fingerTapComplete = false;
        closedGripComplete = false;
        handFlipComplete= false;
        screenTapComplete = false;
    }

    public static int getExerciseSelection(){return exerciseSelection;}

    public static void exerciseComplete(int exercise)
    {
        switch(exercise)
        {
            case 1:
                fingerTapComplete = true;
                break;
            case 2:
                closedGripComplete = true;
                break;
            case 3:
                handFlipComplete = true;
                break;
            case 4:
                screenTapComplete = true;
                break;
        }
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
