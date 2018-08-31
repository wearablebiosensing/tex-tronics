package andrewpeltier.smartglovefragments.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.ColorTemplate;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smartglovefragments.visualize.GenerateGraph;

public class FinishFragment extends Fragment
{
    private static final String TAG = "FinishFragment";
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                restart();
            }
        });

        // Find charts
        chart = view.findViewById(R.id.chart);
        chart2 = view.findViewById(R.id.chart2);
        chart3 = view.findViewById(R.id.chart3);

        // Set up charts with original data
        chart = setUpChart(chart, ColorTemplate.getHoloBlue(), 50, 100, "Graph 1");
        chart2 = setUpChart(chart2, ColorTemplate.JOYFUL_COLORS[0], 250, 1000, "Graph 2");
        chart3 = setUpChart(chart3, ColorTemplate.JOYFUL_COLORS[1], 1320, 2000, "Graph 3");

        // Start chart layout animation process
        chartLayout = view.findViewById(R.id.chartLayout);
        startTimer = new CountDownTimer(startFinish, startTick)
        {
            @Override
            public void onTick(long l) {
                if(firstTick)
                    firstTick = false;
                else
                    animateView(chartLayout);
            }

            @Override
            public void onFinish()
            {
                restart();
            }
        }.start();

        // Clears all pre-existing exercises from the exercise manager
        Log.d(TAG, "onCreateView: Started.");
        return view;
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
            chart = new PieChart(getActivity());

        chart = GenerateGraph.createPieChart(chart, value, max, graphName);
        GenerateGraph.addData(chart, color, value, max);
        return chart;
    }

    private void restart()
    {
        ExerciseSelectionFragment.adapter.clear();
        ExerciseSelectionFragment.listItems.clear();
        ((MainActivity)getActivity()).disconnect();
        TexTronicsManagerService.stop(getActivity());
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }
}
