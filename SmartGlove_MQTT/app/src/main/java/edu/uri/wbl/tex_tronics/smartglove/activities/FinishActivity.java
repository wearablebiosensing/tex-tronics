package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    private PieChart chart, chart2, chart3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "Creating Finish Activity...");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_finish);

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

        chart = findViewById(R.id.chart);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);

        chart = setUpChart(chart, ColorTemplate.getHoloBlue(), 50, 100, "Graph 1");
        chart2 = setUpChart(chart2, ColorTemplate.JOYFUL_COLORS[0], 250, 1000, "Graph 2");
        chart3 = setUpChart(chart3, ColorTemplate.JOYFUL_COLORS[1], 1320, 2000, "Graph 3");

        TexTronicsExerciseManager.clearManager();
        ExerciseSelection.listItems.clear();
        ExerciseSelection.adapter.clear();
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
