package andrewpeltier.smartglovefragments.fragments.doctorfrags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseSelectionFragment;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;

public class RoutineCreateFragment extends Fragment
{
    private Button contButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routine_creation, container, false);
        setHasOptionsMenu(true);

        contButton = view.findViewById(R.id.rou_cont_btn);
        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).addFragment(new ExerciseSelectionFragment(), "ExerciseSelectionFragme");
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.patient_feed:
                ((MainActivity)getActivity()).addFragment(new PatientFeedFragment(), "PatientFeedFragment");
                return true;

            case R.id.create_pat_pro:
                ((MainActivity)getActivity()).addFragment(new CreateProfileFragment(), "CreateProfileFragment");
                return true;

            case R.id.create_routine:
                // Already here, do nothing
                return true;

            case R.id.patient_mode:
                //TODO: Do something with this one
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
