package andrewpeltier.smarttrousers.visualize;

import android.graphics.Color;

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
}
