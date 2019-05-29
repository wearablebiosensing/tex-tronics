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
import android.widget.EditText;

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
    EditText com_text;
    //List<User> mUser = Collections.emptyList();
    List<User> mUsers;
    List<Integer> ids;
    Button returnButton;
    ExportCVS_Study mExportCSV_Study;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_study_finish, container, false);

        com_text = view.findViewById(R.id.final_comments);
        mUsers = new ArrayList<>();
        ids = new ArrayList<>();
        try {
            mUsers = UserRepository.getInstance(getActivity().getApplicationContext()).getAllUsersFromStudy();
            ids = UserRepository.getInstance(getActivity().getApplicationContext()).getAllIdentities();
        } catch (Exception e) {
            Log.d(TAG, "onClick: Error with identities");
        }



        Log.d(TAG, "onCreateView: User length " + Integer.toString(mUsers.size()));


        // TODO make an insert of the data into the database for final comments then log them into a file


        returnButton = view.findViewById(R.id.restart_button);
        // Sets the return button to restart the application and return to the home screen on click
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: in restart button");
                String coms = com_text.getText().toString();
                Log.d(TAG, "onClick: this is coms" + coms + "\n");
                UserRepository.getInstance(((MainActivity)getActivity()).getApplicationContext()).updateData_final_comments(coms,ids.size());
                mExportCSV_Study = new ExportCVS_Study();
                mExportCSV_Study.ExportCSV_Study(mUsers.get(ids.size()-1), ids.size());
                ((MainActivity)getActivity()).StartSucess();

            }
        });

        return view;

    }


}

