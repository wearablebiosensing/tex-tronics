package edu.uri.wbl.tex_tronics.smartglove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import edu.uri.wbl.tex_tronics.smartglove.graph.GenerateGraph;
import edu.uri.wbl.tex_tronics.smartglove.graph.HandleData;
import edu.uri.wbl.tex_tronics.smartglove.smart_glove.SmartGloveManagerService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Handler handler;
    private HandleData handleData;
    private GraphView graph;
    private int counter = 0;
    LineGraphSeries<DataPoint> series1, series2;
    DataPoint[] dataPoints1;
    DataPoint[] dataPoints2;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 034);
        }

        Button connectBtn = findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartGloveManagerService.connect(mContext, GattDevices.SMART_GLOVE_DEVICE);
            }
        });

        Button disconnectBtn = findViewById(R.id.disconnect_btn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartGloveManagerService.disconnect(mContext, GattDevices.SMART_GLOVE_DEVICE);
            }
        });

        Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Started scan from Main");
                SmartGloveManagerService.scan(mContext);
            }
        });

        graph = (GraphView) findViewById(R.id.graph);
        graph = GenerateGraph.makeGraph(graph);
        series1 = new LineGraphSeries<>();
        // Thumb
        series1.setColor(Color.RED);
        graph.addSeries(series1);
        series2 = new LineGraphSeries<>();
        // Index
        series2.setColor(Color.GREEN);
        graph.addSeries(series2);


        handler = new Handler()
        {
            public void handleMessage(Message message)
            {
                switch (message.what)
                {
                    case 1:
                        // Receives values from the ProcessData class
                        final int thumbVal = HandleData.getThumbFlex();
                        final int indexVal = HandleData.getIndexFlex();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addEntry(thumbVal, indexVal);
                            }
                        });

                        // Updates the graph with those values
                        break;
                    default:
                        Log.v("MainActivity", "Message handle error");
                }
            }
        };

        HandleData.setHandler(handler);

    }

    private void addEntry(int thumb, int index)
    {
        Log.e("MainActivity", "Thumb: " + thumb + " Index: " + index);
        series1.appendData(new DataPoint(counter, thumb), true, 1000);
        series2.appendData(new DataPoint(counter, index), true, 1000);
        counter++;
    }
}
