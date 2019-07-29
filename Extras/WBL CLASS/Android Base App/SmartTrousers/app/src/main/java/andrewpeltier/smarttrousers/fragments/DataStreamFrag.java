package andrewpeltier.smarttrousers.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.UUID;

import andrewpeltier.smarttrousers.MainActivity;
import andrewpeltier.smarttrousers.R;
import andrewpeltier.smarttrousers.ble.BluetoothLeConnectionService;
import andrewpeltier.smarttrousers.ble.GattCharacteristics;
import andrewpeltier.smarttrousers.io.SmartGloveInterface;
import andrewpeltier.smarttrousers.visualize.GenerateGraph;
import pl.droidsonroids.gif.GifImageView;

public class DataStreamFrag extends Fragment implements SmartGloveInterface
{
    private static final String TAG = "DeviceExerciseFragment";

    private GraphView graph;
    private LineGraphSeries<DataPoint> series1, series2;
    private int count;
    private String exerciseName;
    private Button disconnectBtn, connectButton, startButton;
    private TextView loadingText;
    public static boolean START_LOG;
    private boolean started = false;
    private GifImageView sideImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_data_stream, container, false);

        if(MainActivity.exercise_name != null)
            exerciseName = MainActivity.exercise_name;

        sideImage = view.findViewById(R.id.stexercise_side_image);
        if(MainActivity.exercise_name != null)
            setSideViews(exerciseName);

        connectButton = view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).connect();
            }
        });

        disconnectBtn = view.findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).disconnect();
            }
        });

        loadingText = view.findViewById(R.id.loadingText);

        startButton = view.findViewById(R.id.start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!started)
                {
                    Log.d(TAG, "Starting data logging.");
                    final CountDownTimer startTimer = new CountDownTimer(3000, 980) {
                        int countdown = 3;

                        @Override
                        public void onTick(long l)
                        {
                            Log.v(TAG, "Tick: " + countdown);
                            loadingText.setText("" + countdown);
                            countdown--;
                        }

                        @Override
                        public void onFinish()
                        {
                            START_LOG = true;
                            loadingText.setVisibility(View.INVISIBLE);
                        }
                    };
                    startTimer.start();
                    startButton.setText("Stop");
                    started = true;
                }
                else
                {
                    startButton.setText("Stop");
                    START_LOG = false;
                    loadingText.setText("Waiting...");
                    loadingText.setVisibility(View.VISIBLE);
                    ((MainActivity)getActivity()).publish();
                }
            }
        });

        // Sets up the repetition counter on the GUI
        count = 0;
        graph = view.findViewById(R.id.graph);
        graph = GenerateGraph.makeRTGraph(graph);
        series1 = new LineGraphSeries<>();
        // Thumb
        series1.setColor(Color.RED);
        graph.addSeries(series1);
        series2 = new LineGraphSeries<>();
        // Index
        series2.setColor(Color.GREEN);
        graph.addSeries(series2);

        // Gets the current time for graph timer
        Log.d(TAG, "onCreateView: Started.");
        return view;
    }

    private void setSideViews(String name)
    {

    }

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received BLE Update: " + exerciseName);
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            Log.v(TAG, "DeviceAddress: " + deviceAddress);
            Log.v(TAG, "BLE Action: " + action);

            if(action.equals(BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY)) {
                UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                    Log.d(TAG, "Data Received");
                    byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                    // First Data Set
                    int thumb = (((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF)));
                    int index = (((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF)));
                    if(START_LOG)
                        addEntry(thumb, index);

                    // Second Data Set
                    thumb = (((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                    index = (((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));
                    if(START_LOG)
                        addEntry(thumb, index);


                    // Third Data Set
                    thumb = (((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));
                    index = (((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF)));
                    if(START_LOG)
                        addEntry(thumb, index);

                    Log.d(TAG, "onReceive: Start Log = " + START_LOG);
                }
            }
        }
    };

    private void addEntry(int thumb, int index)
    {
        Log.e("MainActivity", "Thumb: " + thumb + " Index: " + index);
        series1.appendData(new DataPoint(count, thumb), true, 1000);
        series2.appendData(new DataPoint(count, index), true, 1000);
        count++;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: started " + exerciseName);

        getActivity().registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: Paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: stopped");

        getActivity().unregisterReceiver(mBLEUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroying exercise fragment...");
        super.onDestroy();
    }
}
