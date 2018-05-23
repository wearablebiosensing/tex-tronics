package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import edu.uri.wbl.tex_tronics.smartglove.R;

public class ExerciseInstructions extends AppCompatActivity
{
    private static final String TAG = "ExerciseInstructions";
    private int selectedExercise;
    private Context context;
    private Button startExerciseButton;
    private VideoView instructionsVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.exercise_instructions_layout);
        Log.e(TAG, "Creating ExerciseInstructions Activity...");

        context = this;

        startExerciseButton = (Button) findViewById(R.id.start_exercise_button);
        instructionsVideo = (VideoView) findViewById(R.id.instruction_view);

        selectedExercise = ExerciseSelection.getExerciseSelection();
        setUpVideo();

        if(selectedExercise == 1|| selectedExercise == 2|| selectedExercise == 3)
        {
            startExerciseButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.v(TAG, "Launching Finger Tap Exercise...");
                    GloveExerciseActivity.setExerciseSelection(selectedExercise);
                    Intent intent = new Intent(context, GloveExerciseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else if(selectedExercise == 4)
        {
            startExerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Launching Screen Tap Exercise...");
                    Intent intent = new Intent(context, ScreenTapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void setUpVideo()
    {
        switch (selectedExercise)
        {
            case 1:
                instructionsVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.finger_tap_instructions);
                break;
            case 2:
                instructionsVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.closed_grip_instructions);
                break;
            case 3:
                instructionsVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.hand_flip_instructions);
                break;
            case 4:
                instructionsVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.screen_tap_instructions);
                break;
            default:
                Log.d(TAG, "Error. Video not found / Exercise selection error.");
        }
        instructionsVideo.setMediaController(new MediaController(context));
        instructionsVideo.requestFocus();
        instructionsVideo.start();
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
