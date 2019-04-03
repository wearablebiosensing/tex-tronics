package andrewpeltier.smartglovefragments.fragments.patientfrags;

import android.app.Fragment;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.database.User;
import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.io.ExportCVS_Study;
import andrewpeltier.smartglovefragments.io.StudyLog;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;


public class StudyFinishFragment extends android.support.v4.app.Fragment {

    final String TAG = "Finish_study";
    //List<User> mUser = Collections.emptyList();
    List<User> mUsers;
    Button returnButton;
    ExportCVS_Study mExportCSV_Study;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_study_finish, container, false);

        mUsers = new ArrayList<>();
        try {
            mUsers = UserRepository.getInstance(getActivity().getApplicationContext()).getAllUsersFromStudy();
        } catch (Exception e) {
            Log.d(TAG, "onClick: Error with identities");
        }

        Log.d(TAG, "onCreateView: User length " + Integer.toString(mUsers.size()));


        mExportCSV_Study = new ExportCVS_Study();
        mExportCSV_Study.ExportCSV_Study(mUsers);
        //myStudyLog.StudyLog(jsonPaitent);


        returnButton = view.findViewById(R.id.restart_button);
        // Sets the return button to restart the application and return to the home screen on click
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });

        return view;

    }


    private void restart()
    {
        try
        {
            // Clears the list of exercises from our exercise selection fragment
            ExerciseSelectionFragment.adapter.clear();
            ExerciseSelectionFragment.listItems.clear();
            // Disconnects from all devices
            ((MainActivity)getActivity()).disconnect();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error: " + e);
        }
        // Disconnects from MQTT server
        TexTronicsManagerService.stop(getActivity());
        // Restart the main activity to clear fragment manager and launch it again
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

}

