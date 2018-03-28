package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.uri.wbl.tex_tronics.smartglove.R;

public class FinishActivity extends AppCompatActivity
{
    private static final String TAG = "FinishActivity";
    private ExerciseSelection exerciseSelection;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "Creating Finish Activity...");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.finish_layout);

//        GatherCSVData gatherCSVData = new GatherCSVData();
//        gatherCSVData.logFingerTap(this);
//        gatherCSVData.logScreenTap(this);

        exerciseSelection = new ExerciseSelection();
        returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exerciseSelection.resetCompletion();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Log.e(TAG, "All activities completed. Navigating to main...");
        Intent intent = this.getParentActivityIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}
