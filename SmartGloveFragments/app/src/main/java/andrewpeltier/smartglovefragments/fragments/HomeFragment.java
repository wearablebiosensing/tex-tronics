package andrewpeltier.smartglovefragments.fragments;

import android.content.pm.ActivityInfo;
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
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.R;

public class HomeFragment extends Fragment
{
    private static final String TAG = "HomeFragment";
    private Button startButton;
    private ImageView logo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startButton = view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setIsDoctor(false);
                ((MainActivity)getActivity()).addFragment(new ExerciseSelectionFragment(), "ExerciseSelectionFragment");
            }
        });
        startButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)getActivity()).setIsDoctor(true);
                ((MainActivity)getActivity()).addFragment(new PatientFeedFragment(), "PatientFeedFragment");
                return false;
            }
        });

        logo = view.findViewById(R.id.logo);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.logofadein);
        logo.startAnimation(fadeInAnimation);

        Log.d(TAG, "onCreateView: Started.");
        return view;
    }
}
