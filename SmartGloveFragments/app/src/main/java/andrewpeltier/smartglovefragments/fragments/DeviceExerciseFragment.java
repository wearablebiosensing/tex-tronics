package andrewpeltier.smartglovefragments.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.UUID;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.ble.BluetoothLeConnectionService;
import andrewpeltier.smartglovefragments.ble.GattCharacteristics;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smartglovefragments.visualize.GenerateGraph;
import pl.droidsonroids.gif.GifImageView;

public class DeviceExerciseFragment extends Fragment implements SmartGloveInterface
{
    private static final String TAG = "DeviceExerciseFragment";

    private GraphView graph;
    private LineGraphSeries<DataPoint> series1, series2;
    private int count;
    private String exerciseName;
    private Button disconnectBtn, nextButton;
    private GifImageView sideImage;
    public static boolean startLog = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_device_exercise, container, false);

        if(MainActivity.connected)
            startLog = true;

        if(MainActivity.exercise_name != null)
            exerciseName = MainActivity.exercise_name;

        sideImage = view.findViewById(R.id.stexercise_side_image);
        if(MainActivity.exercise_name != null)
            setSideViews(exerciseName);

        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startLog = false;
                ((MainActivity)getActivity()).publish();
                ((MainActivity)getActivity()).startExercise();
            }
        });

        disconnectBtn = view.findViewById(R.id.disconnectBtn);
        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).disconnect();
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
        if (name.equals("Finger Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed Grip"))
        {
            sideImage.setBackgroundResource(InstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand Flip"))
        {
            sideImage.setBackgroundResource(InstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Heel Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Toe Tap"))
        {
            sideImage.setBackgroundResource(InstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Foot Stomp"))
        {
            sideImage.setBackgroundResource(InstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Walk Steps"))
        {
            sideImage.setBackgroundResource(InstructionsImage.WALK_STEPS_GIF);
        }
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
                    if(startLog)
                        addEntry(thumb, index);

                    // Second Data Set
                    thumb = (((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                    index = (((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));
                    if(startLog)
                        addEntry(thumb, index);


                    // Third Data Set
                    thumb = (((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));
                    index = (((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF)));
                    if(startLog)
                        addEntry(thumb, index);

                    Log.d(TAG, "onReceive: Start Log = " + startLog);
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
