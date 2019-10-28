package andrewpeltier.smartglovefragments.fragments.patientfrags;

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

/** ======================================
 *
 *          FinishFragment Class
 *
 *  ======================================
 *
 *      This fragment needs substantial changes as we are able to gather more information
 *  from our server. At this point, we demo dummy data and restart the application.
 *
 *
 *
 * @author Andrew Peltier
 * @version 1.0
 */
public class FinishFragment extends Fragment
{
    private static final String TAG = "FinishFragment";
    /**
     * The time it takes to transition from one set of graphs to another. At this
     * point, it takes one second to have a set of graphs move off of the screen and
     * one second to move back on
     */
    private static final long TRAN_TICK = 1000;
    private static final long TRAN_FINISH = 1000;
    /**
     * The duration of a set of graphs on the screen. At this point, a set of graphs should
     * remain on the screen for 6 seconds.
     */
    private static final long START_TICK = 6000;
    /**
     * This will probably have to change. The graphs will rotate between each set for 5 minutes,
     * at which point the application will reset. This should be plenty of time to demo
     */
    private static final long START_FINISH = 300000;
    private Button returnButton;                            // Button that returns to home fragment
    private ConstraintLayout chartLayout;                   // Layout that contains our pie graphs
    private PieChart chart, chart2, chart3;                 // Each of the pie graphs that shows dummy data
    private CountDownTimer transitionTimer, startTimer;     // Timer that manages the pie graphs
    private boolean demo = false;                           // Determines what set of graphs we are displaying
    private boolean firstTick = true;                       // Stops the timer from running code twice

    /** onCreateView()
     *
     * Called when the view is first created. We use the fragment_exercise_selection XML file to load the view and its
     * properties into the fragment, which is then given to the Main Activity. For the finish fragment, we just have to
     * set up the charts and start the timers that are used to animate them.
     *
     * @param inflater                      -Used to "inflate" or load the layout inside the main activity
     * @param container                     -Object containing the fragment layout (from MainActivity XML)
     * @param savedInstanceState            -State of the application
     * @return                          The intractable exercise selection fragment view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        returnButton = view.findViewById(R.id.return_button);
        // Sets the return button to restart the application and return to the home screen on click
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

        // Starts the graph rotation timer
        startTimer = new CountDownTimer(START_FINISH, START_TICK)
        {
            /** onTick()
             *
             * Because of how the Android Countdown Timer works, a tick is automatically activated
             * once the clock starts. Since we only want this to activate once, we have a boolean
             * denying the initial tick to start.
             *
             * Other than that, we set up the view animation that rotates between sets of graphs
             *
             * @param l             -The time between ticks
             */
            @Override
            public void onTick(long l)
            {
                if(firstTick)
                    firstTick = false;
                else
                    animateView(chartLayout);
            }

            /** onFinish()
             *
             * After 5 minutes, we restart the application
             */
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

    /** animateView()
     *
     * Called by the start timer every 6 seconds upon tick. This animates the chart layout which contains
     * each graph on and off of the screen
     *
     * @param view              -View to be animated, namely the chart layout
     */
    private void animateView(View view)
    {
        // Sends the view off of the page
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "x", 0, 3000);
        anim.setDuration(1000);
        anim.start();

        // Starts transition timer
        transitionTimer = new CountDownTimer(TRAN_FINISH, TRAN_TICK)
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

    /** changeChart()
     *
     * Called by animateView(). This changes the data of the three pie graphs to the alternate set when
     * they are off the screen. This gives the illusion that there are multiple sets of charts being animated
     *
     */
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

    /** setUpChart()
     *
     * Called by setUpChart(). This takes as parameters all the values that each graph needs and uses the
     * GenerateGraph class to change it
     *
     * @param chart                 -The graph to be changed
     * @param color                 -The color scheme of the graph
     * @param value                 -How filled the graph is
     * @param max                   -Max value that the graph can be filled to
     * @param graphName             -Name of the graph
     * @return                  The fully constructed graph
     */
    private PieChart setUpChart(PieChart chart, int color, int value, int max, String graphName)
    {
        // Create an entirely new chart if there is not one yet
        if(chart == null)
            chart = new PieChart(getActivity());

        // Creates a new chart with our parameters
        chart = GenerateGraph.createPieChart(chart, value, max, graphName);
        // Fills the graph with the data we receive from our parameters
        GenerateGraph.addData(chart, color, value, max);
        return chart;
    }

    /** restart()
     *
     * Clears the data that we've used for our routine. We essentially restart the application from the
     * beginning.
     *
     */
    private void restart()
    {
        try
        {
            // Clears the list of exercises from our exercise selection fragment
            ExerciseSelectionFragment.adapter.clear();
            ExerciseSelectionFragment.listItems.clear();
            // Disconnects from all devices
            ((MainActivity)getActivity()).disconnect();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error: " + e);
        }
        // Disconnects from MQTT server
        TexTronicsManagerService.stop(getActivity());
        // Restart the main activity to clear fragment manager and launch it again
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }
}
