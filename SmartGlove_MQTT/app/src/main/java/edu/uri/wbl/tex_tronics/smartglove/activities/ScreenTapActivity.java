package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.uri.wbl.tex_tronics.smartglove.R;

public class ScreenTapActivity extends AppCompatActivity
{
    private static final String TAG = "ScreenTapActivity";
    private static final int    REQUIRED_TAPS = 10;
    private static Context context;
    private ImageButton screenTapButton;
    private TextView screenTapCount;
    private int         count = 0;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceBundle)
    {
        Log.e(TAG, "Creating ScreenTap Activity...");
        super.onCreate(savedInstanceBundle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.screen_tap_layout);
        context = this;
        startTime = System.currentTimeMillis();

        screenTapButton = (ImageButton) findViewById(R.id.screen_tap_button);
        screenTapCount = (TextView) findViewById(R.id.screen_tap_count);

        screenTapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                count++;
                if(count < REQUIRED_TAPS)
                {
                    screenTapCount.setText("" + count);
                }
                else
                {
//                    GatherCSVData.writeScreenTap((System.currentTimeMillis() - startTime) / 1000);
                    ExerciseSelection.exerciseComplete(4);
                    Intent intent = new Intent(context, ExerciseSelection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
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
