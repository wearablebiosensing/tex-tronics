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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;

public class CreateProfileFragment extends Fragment
{
    private TextView instrText, nameText, ageText, heightText, weightText, diagText;
    private Button nextAttrBtn;
    private EditText editName;

    private int state = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_creation, container, false);
        setHasOptionsMenu(true);

        instrText = view.findViewById(R.id.instrText);
        nameText = view.findViewById(R.id.nameText);
        ageText = view.findViewById(R.id.ageText);
        heightText = view.findViewById(R.id.heightText);
        weightText = view.findViewById(R.id.weightText);
        diagText = view.findViewById(R.id.diagText);
        nextAttrBtn = view.findViewById(R.id.nextAttrBtn);
        editName = view.findViewById(R.id.editName);

        nextAttrBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Check to see what state
                switch(state)
                {
                    case 0:
                        if(!editName.getText().toString().equals("Name") && !editName.getText().toString().equals(""))
                        {
                            nameText.setText(editName.getText().toString());
                            // Move view
                            state++;
                        }
                        else
                            Toast.makeText(getActivity(),"Please enter a valid name", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
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
