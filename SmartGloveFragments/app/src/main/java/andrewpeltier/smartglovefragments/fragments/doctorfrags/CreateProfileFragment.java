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

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;

public class CreateProfileFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_creation, container, false);
        setHasOptionsMenu(true);

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
                // Already on patient feed, do nothing
                return true;

            case R.id.create_routine:
                ((MainActivity)getActivity()).addFragment(new RoutineCreateFragment(), "RoutineCreateFragment");
                return true;

            case R.id.patient_mode:
                //TODO: Do something with this one
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
