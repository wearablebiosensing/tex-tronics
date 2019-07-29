package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.io.SmartGloveInterface;
import pl.droidsonroids.gif.GifImageView;

public class ExerciseInstructions extends AppCompatActivity
{
    private static final String TAG = "ExerciseInstructions";
    private static final String EXERCISE_NAME = "uri.wbl.tex_tronics.name";
    private int selectedExercise;
    private String exerciseName;
    private Context context;
    private Button startExerciseButton;
    private VideoView instructionsVideo;
    private TextView instrText;
    private GifImageView instrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_exercise_instr);
        Log.e(TAG, "Creating ExerciseInstructions Activity...");

        context = this;
        startExerciseButton = findViewById(R.id.start_exercise_button);
        instrImage = findViewById(R.id.stexercise_side_image);
        instrText = findViewById(R.id.instructions_text);

        Intent intent = getIntent();
        if(intent != null)
        {
            exerciseName = intent.getStringExtra(EXERCISE_NAME);
            setTitle(exerciseName);
            setSideViews(exerciseName);
        }

//        instructionsVideo = findViewById(R.id.instruction_view);

        startExerciseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(exerciseName != null)
                {
                    if(exerciseName.equals("Screen Tap"))
                    {
                        Intent intent = new Intent(context, ScreenTapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(context, GloveExerciseActivity.class);
                        intent.putExtra(EXERCISE_NAME, exerciseName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                    Toast.makeText(context, "Error, please go back.", Toast.LENGTH_SHORT).show();
            }
        });

//        selectedExercise = ExerciseSelection.getExerciseSelection();
//        setUpVideo();

//        if(selectedExercise == 1|| selectedExercise == 2|| selectedExercise == 3)
//        {
//            startExerciseButton.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    Log.v(TAG, "Launching Finger Tap Exercise...");
//                    GloveExerciseActivity.setExerciseSelection(selectedExercise);
//                    Intent intent = new Intent(context, GloveExerciseActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//        }
//        else if(selectedExercise == 4)
//        {
//            startExerciseButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.v(TAG, "Launching Screen Tap Exercise...");
//                    Intent intent = new Intent(context, ScreenTapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//        }
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.logofadein);
        instrImage.startAnimation(fadeInAnimation);
    }

    private void setSideViews(String name)
    {
        if (name.equals("Finger Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.FINGER_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed Grip"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.CLOSED_GRIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand Flip"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.HAND_FLIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Screen Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.SCREEN_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.SCREEN_TAP_GIF);
            instrImage.getLayoutParams().width = 428;
            instrImage.getLayoutParams().height = 371;
        }
        else if (name.equals("Heel Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.HEEL_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Toe Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.TOE_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Foot Stomp"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.FOOT_STOMP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Walk Steps"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.WALK_STEPS_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.WALK_STEPS_GIF);
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
//        Log.e(TAG, "Back pressed. Navigating to " + getParentActivityIntent());
//        Intent intent = this.getParentActivityIntent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        super.onBackPressed();
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
