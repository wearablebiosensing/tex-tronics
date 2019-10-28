package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.ColorTemplate;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsExerciseManager;
import edu.uri.wbl.tex_tronics.smartglove.visualize.GenerateGraph;

public class FinishActivity extends AppCompatActivity
{
    private static final String TAG = "FinishActivity";
    private ExerciseSelection exerciseSelection;
    private Button returnButton;
    private ConstraintLayout chartLayout;
    private PieChart chart, chart2, chart3;
    private CountDownTimer transitionTimer, startTimer;
    private long tranTick = 1000;
    private long tranFinish = 1000;
    private long startTick = 6000;
    private long startFinish = 300000;
    private boolean demo = false;
    private boolean firstTick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "Creating Finish Activity...");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_finish);

        // Sets up the return button
        exerciseSelection = new ExerciseSelection();
        returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        // Find charts
        chart = findViewById(R.id.chart);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);

        // Set up charts with original data
        chart = setUpChart(chart, ColorTemplate.getHoloBlue(), 50, 100, "Graph 1");
        chart2 = setUpChart(chart2, ColorTemplate.JOYFUL_COLORS[0], 250, 1000, "Graph 2");
        chart3 = setUpChart(chart3, ColorTemplate.JOYFUL_COLORS[1], 1320, 2000, "Graph 3");

        // Start chart layout animation process
        chartLayout = findViewById(R.id.chartLayout);
        startTimer = new CountDownTimer(startFinish, startTick) {
            @Override
            public void onTick(long l) {
                if(firstTick)
                    firstTick = false;
                else
                    animateView(chartLayout);
            }

            @Override
            public void onFinish() {
                onBackPressed();
            }
        }.start();

        // Clears all pre-existing exercises from the exercise manager
        TexTronicsExerciseManager.clearManager();
        ExerciseSelection.listItems.clear();
        ExerciseSelection.adapter.clear();
    }

    private void animateView(View view)
    {
        // Sends the view off of the page
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "x", 0, 3000);
        anim.setDuration(1000);
        anim.start();

        // Starts transition timer
        transitionTimer = new CountDownTimer(tranFinish, tranTick)
        {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish()
            {
                // After waiting, send the alternate graphs back on to the screen
                demo = !demo;
                changeChart();
                ObjectAnimator anim = ObjectAnimator.ofFloat(chartLayout, "x", -3000, 0);
                anim.setDuration(1000);
                anim.start();
            }
        }.start();
    }

    private void changeChart()
    {
        // Alternates between graph sets
        if(!demo)
        {
            chart = setUpChart(chart, ColorTemplate.getHoloBlue(), 50, 100, "Graph 1");
            chart2 = setUpChart(chart2, ColorTemplate.JOYFUL_COLORS[0], 250, 1000, "Graph 2");
            chart3 = setUpChart(chart3, ColorTemplate.JOYFUL_COLORS[1], 1320, 2000, "Graph 3");
        }
        else
        {
            chart = setUpChart(chart, Color.MAGENTA, 25, 100, "Graph 4");
            chart2 = setUpChart(chart2, Color.CYAN, 750, 1000, "Graph 5");
            chart3 = setUpChart(chart3, ColorTemplate.VORDIPLOM_COLORS[1], 1900, 2000, "Graph 6");
        }
    }

    private PieChart setUpChart(PieChart chart, int color, int value, int max, String graphName)
    {
        if(chart == null)
            chart = new PieChart(this);

        chart = GenerateGraph.createPieChart(chart, value, max, graphName);
        GenerateGraph.addData(chart, color, value, max);
        return chart;
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
