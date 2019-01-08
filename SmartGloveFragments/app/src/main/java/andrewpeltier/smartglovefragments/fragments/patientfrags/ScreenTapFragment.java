package andrewpeltier.smartglovefragments.fragments.patientfrags;

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

/** ======================================
 *
 *        ScreenTapFragment Class
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
public class ScreenTapFragment extends Fragment
{
    private static final String TAG = "ScreenTapActivity";
    /**
     * Taps required to finish the exercise
     */
    private static final int    REQUIRED_TAPS = 10;
    private TextView screenTapCount;                // Text that shows how many taps have been made
    private int      count = 0;                     // Counts how many taps have been made
    private long startTime;                         // Time since the exercise have started (to be used later)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_screen_tap, container, false);
        View rootView = getActivity().findViewById(R.id.container);

        // Sets the root container to register a screen tap
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenTapped();
            }
        });

        // Sets up our other views
        startTime = System.currentTimeMillis();
        screenTapCount = view.findViewById(R.id.screen_tap_count);

        Log.d(TAG, "onCreateView: Started.");
        return view;
    }

    /** screenTapped()
     *
     * Previously set up to be called whenever the root view, being the container in the main activity that
     * holds the screen tap fragment view, is tapped by the user. We increment the count of taps that have
     * occurred, and check if the number of required taps have been met. If so, then the exercise is complete
     * and we move to the next one.
     *
     */
    public void screenTapped()
    {
        // Increment count, then check how many taps we've made
        count++;
        if(count < REQUIRED_TAPS)
        {
            // Let the user know how many taps they currently have made
            screenTapCount.setText("" + count);
        }
        else
        {
            // Move to the next exercise
            ((MainActivity)getActivity()).startExercise();
        }
        Log.d(TAG, "screenTapped: Tapped screen. Count: " + count);
    }
}
