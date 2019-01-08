package andrewpeltier.smartglovefragments.visualize;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

/** ======================================
 *
 *           GenerateGraph Class
 *
 *  ======================================
 *
 *  Creates and adds data to the variety of graphs that are used in the application. As
 *  of version 1.0 we use two graphs, a line graph for our data collection and pie graphs
 *  for our finish screen demo.
 *
 *  @author Andrew Peltier
 *  @version 1.0
 *
 */
public class GenerateGraph
{
    /** makeRTGraph()
     *
     * Called by the DeviceExerciseFragment class. This creates a line graph
     * used to visualize incoming data from our devices.
     *
     * @param graph             -Reference to graph object in DeviceExerciseFragment
     * @return              Graph with defined options
     */
    public static GraphView makeRTGraph(GraphView graph)
    {
        // Set the min and max dimensions of the line graph
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(400);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(250);
        // enable scaling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        // Set additional options
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
        return graph;
    }

    /** createPieChart()
     *
     * Called by the FinishFragment. Creates a pie chart used to demo data from the last
     * activity. This method simply formats a chart without adding data to it.
     *
     * @param chart             -Reference to the chart object
     * @param value             -The numerator
     * @param max               -The denominator
     * @param graphName         -Name of the graph
     * @return              Formatted graph object
     */
    public static PieChart createPieChart(PieChart chart, int value, int max, String graphName)
    {
        // Use percentages when graphing
        chart.setUsePercentValues(true);

        // Sets description
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        // Creates the hole in the pie chart
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(80);
        chart.setTransparentCircleRadius(85);

        // Creates the text in the center
        chart.setCenterText(graphName + ":\n\n" + value + "/" + max); // Make this number a dynamic number from sensors
        chart.setCenterTextSize(10);
        chart.setCenterTextColor(ColorTemplate.getHoloBlue());

        chart.setRotationEnabled(false);

        return chart;
    }

    /** addData()
     *
     * Called by the FinishFragment after createPieChart() has been called. This adds data to each
     * pie chart, which in this case means it fills the chart based on a value and the max value.
     *
     * @param mChart            -Reference to the chart
     * @param mColor            -Color of the data in the chart
     * @param value             -Numerator, or the user's data
     * @param max               -Denomenator, or the max value a user can get from a component
     */
    public static void addData(PieChart mChart, int mColor, int value, int max) {
        float[] yData = { value, max - value };
        String[] xData = { "X", "Y"};

        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new PieEntry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "X / Y");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

        colors.add(mColor);
        colors.add(Color.LTGRAY);
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
//        data.setValueTextSize(11f);
//        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();
    }
}
