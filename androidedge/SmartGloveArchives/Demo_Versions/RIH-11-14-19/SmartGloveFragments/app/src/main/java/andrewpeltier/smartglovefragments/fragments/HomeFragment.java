package andrewpeltier.smartglovefragments.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import andrewpeltier.smartglovefragments.fragments.doctorfrags.PatientFeedFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseSelectionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.SurveyFragment;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.R;

/** ======================================
 *
 *          HomeFragment Class
 *
 *  ======================================
 *
 *      The Home fragment is the first fragment loaded into the main activity. This animates the logo, as well as
 *  gets starts the application in either doctor or user mode.
 *
 *  @author Andrew Peltier
 *  @version 1.0
 *
 */
public class HomeFragment extends Fragment
{
    private static final String TAG = "HomeFragment";

    private Button beginButton;                 // Button that changes fragment
    private ImageView logo;                     // Center animated Smart Glove logo

    /** onCreateView()
     *
     * Called when the view is first created. We use the fragment_home XML file to load the view and its
     * properties into the fragment, which is then given to the Main Activity. For the home screen, we only
     * need to set up the start button and the logo animation.
     *
     * @param inflater                      -Used to "inflate" or load the layout inside the main activity
     * @param container                     -Object containing the fragment layout (from MainActivity XML)
     * @param savedInstanceState            -State of the application
     * @return                          The intractable home fragment view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        beginButton = view.findViewById(R.id.start_button);

        Log.d(TAG, "onCreateView: Home Fragment is launched");

        // For version 1.0, a short click will activate the user side of the application...
        beginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((MainActivity)getActivity()).setIsDoctor(false);
                // change to do the survey fragment, from the exercise selection fragment
                ((MainActivity)getActivity()).addFragment(new SurveyFragment(), "SurveyFragment");
            }
        });
        // while a long click will activate the doctor side
//        startButton.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View view)
//            {
//                ((MainActivity)getActivity()).setIsDoctor(true);
//                ((MainActivity)getActivity()).addFragment(new PatientFeedFragment(), "PatientFeedFragment");
//                return false;
//            }
//        });

        // Animates the Smart Glove logo
        logo = view.findViewById(R.id.logo);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.logo_fade_in);
        logo.startAnimation(fadeInAnimation);

        Log.d(TAG, "onCreateView: Started.");
        return view;
    }
}
