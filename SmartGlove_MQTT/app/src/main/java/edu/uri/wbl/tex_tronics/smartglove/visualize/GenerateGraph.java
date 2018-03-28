package edu.uri.wbl.tex_tronics.smartglove.visualize;

import com.jjoe64.graphview.GraphView;

public class GenerateGraph
{
    public static GraphView makeGraph(GraphView graph)
    {
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(150);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
        return graph;
    }
}
