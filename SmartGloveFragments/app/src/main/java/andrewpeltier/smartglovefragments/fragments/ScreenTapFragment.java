package andrewpeltier.smartglovefragments.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;


public class ScreenTapFragment extends Fragment
{
    private static final String TAG = "ScreenTapActivity";
    private static final int    REQUIRED_TAPS = 10;
    private TextView screenTapCount;
    private int         count = 0;
    private long startTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_screen_tap, container, false);
        View rootView = getActivity().findViewById(R.id.container);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenTapped();
            }
        });

        startTime = System.currentTimeMillis();
        screenTapCount = view.findViewById(R.id.screen_tap_count);

        Log.d(TAG, "onCreateView: Started.");
        return view;
    }

    public void screenTapped()
    {
        count++;
        if(count < REQUIRED_TAPS)
        {
            screenTapCount.setText("" + count);
        }
        else
        {
            ((MainActivity)getActivity()).startExercise();
        }
        Log.d(TAG, "screenTapped: Tapped screen. Count: " + count);
    }
}
