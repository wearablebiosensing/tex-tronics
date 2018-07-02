package edu.uri.wbl.tex_tronics.smartglove.visualize;

import com.jjoe64.graphview.GraphView;

public class GenerateGraph
{
    public static GraphView makeGraph(GraphView graph)
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
