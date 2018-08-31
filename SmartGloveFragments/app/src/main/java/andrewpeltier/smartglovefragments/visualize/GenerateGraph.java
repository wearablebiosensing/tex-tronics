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

public class GenerateGraph
{
    public static GraphView makeRTGraph(GraphView graph)
    {
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(400);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(250);
        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        return graph;
    }

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
