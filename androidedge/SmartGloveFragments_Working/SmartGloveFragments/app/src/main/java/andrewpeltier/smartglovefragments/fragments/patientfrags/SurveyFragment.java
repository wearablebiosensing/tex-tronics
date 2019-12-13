package andrewpeltier.smartglovefragments.fragments.patientfrags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.database.User;
import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.io.StudyLog;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;

public class SurveyFragment extends Fragment {

    public String TAG = "SURVEY_FRAG";

    EditText age_text;
    EditText year_text;
    EditText mnth_text;
    EditText time_text;
    EditText amount_text;
    EditText comments_text;
    RadioGroup radioGender, radioHand, radioOn_Off;
    Button nextButton;
    StudyLog myStudyLog;

    int _age;
    int _year;
    int _mon;
    int m_f;
    int r_l;
    int o_f;
    float _time;
    float _amount;
    String _comments, age_str, year_str, mon_str, time_str, amount_str;

    private List<String> exerciseModes;                                 // List of modes to send to Main Activity


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        Log.d(TAG, "onCreateView: survey fragment created successfully");

        // 0 is false, 1 is true
        m_f = 0;
        r_l = 0;
        o_f = 0;

        age_text = view.findViewById(R.id.age_text);
        year_text = view.findViewById(R.id.years_text);
        mnth_text = view.findViewById(R.id.months_text);
        //time_text = view.findViewById(R.id.time_text);
        amount_text = view.findViewById(R.id.amount_text);
        comments_text = view.findViewById(R.id.comments_text);

        nextButton = view.findViewById(R.id.next_button);
        exerciseModes = new ArrayList<>();

        radioGender = view.findViewById(R.id.gen_group);
        radioHand = view.findViewById(R.id.handed_group);
        radioOn_Off = view.findViewById(R.id.feeling_group);

        radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()  {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: Made it ");
                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.male_radio:
                        // male is 0
                        m_f = 0;
                        break;
                    case R.id.female_radio:
                            // female is 1
                        m_f = 1;
                        break;
                }

            }

        });

        radioHand.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()  {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: Made it ");
                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.male_radio:
                        // left is 0
                        r_l = 0;
                        break;
                    case R.id.female_radio:
                        // right is 1
                        r_l = 1;
                        break;
                }
            }
        });


        radioOn_Off.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()  {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: Made it ");
                // Check which radio button was clicked
                switch(checkedId) {
                    case R.id.on_radio:
                        // on is 0
                        o_f = 0;
                        break;
                    case R.id.off_radio:
                        // off is 1
                        o_f = 1;
                        break;
                    case R.id.unsure_radio:
                        // off is 1
                        o_f = 2;
                        break;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean empty = true;
                //TODO need to add in protection for a forgotten field
                // TODO need to create a string for csv files of patient data
                age_str = age_text.getText().toString();
                year_str = year_text.getText().toString();
                mon_str = mnth_text.getText().toString();
                //time_str = time_text.getText().toString();
                //amount_str = amount_text.getText().toString();
                _comments = comments_text.getText().toString();

                try {
                    _age = Integer.parseInt(age_str);
                    _year = Integer.parseInt(year_str);
                    _mon = Integer.parseInt(mon_str);
                    _time = Float.parseFloat(time_str);
                    _amount = Float.parseFloat(amount_str);
                }
                catch (Exception e){
                    // add exeption
                    //UnCOMMENT THIS!!!!!!

                  //  empty = true;
                }


               /* if(empty){
                    Toast.makeText(getActivity(),"Go back and enter the values!",Toast.LENGTH_SHORT).show();
                }
                else {*/
                    int dur_len = (_year *12) +_mon;

                    Log.d(TAG, "onClick: age: " + age_str + "\n"+ "year : " + year_str + "\n" + "month: " + mon_str + "\n" + "time: " + time_str + "\n" + "amount: " + amount_str + "\n" + _comments
                            + "\n" + "male or female " + Integer.toString(m_f) + "\n" + "right or left: " + Integer.toString(r_l) + "\n" + "on or off " + Integer.toString(o_f)+"\n");

                    User user = new User(_age,m_f,r_l,dur_len,_time,o_f, _amount, _comments);
                    UserRepository.getInstance(getActivity().getApplicationContext()).insertUsr(_age,m_f,r_l,dur_len,_time, _amount, o_f,_comments);

                    List<Integer> ident = new ArrayList<>();
                    try {
                        ident  = UserRepository.getInstance(getActivity().getApplicationContext()).getAllIdentities();
                    }
                    catch(Exception e){
                        Log.d(TAG, "onClick: Error with identities");
                    }

                    Log.d(TAG, "onClick: IDENTITY" + Integer.toString(ident.size()));


                    String jsonPaitent = "Age: " + age_str + "," + " Gender: " + Integer.toString(m_f) + ","+ " Duration: " + Integer.toString(dur_len) + "," + " Handedness: "+
                            Integer.toString(r_l) + "," + " Dose: " + time_str + "," + " Amount: " + amount_str + "," + " Feel: " + Integer.toString(o_f) + "," + " Comments: " + _comments + "\n";


                    myStudyLog = new StudyLog();
                    myStudyLog.StudyLog(ident.size(), jsonPaitent);

                    String[] studyExercises = {"Resting_Hands_on_Thighs","Hold_Hands_Out","Finger_to_Nose","Finger_Tap" ,"Closed_Grip","Hand_Flip"};



                    // Takes exercises from list and sends them to Main Activity
//                     String[] chosenExercises = new String[adapter.getCount()];
                    for(int i = 0; i < studyExercises.length; i++)
                    {
                        //chosenExercises[i] = adapter.getItem(i);
                        exerciseModes.add(ExerciseMode.FLEX_IMU.toString());
                    }
                    /*exerciseModes.add(ExerciseMode.IMU_ONLY.toString());
                    exerciseModes.add(ExerciseMode.IMU_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    exerciseModes.add(ExerciseMode.IMU_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());*/

                    String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);

                    ((MainActivity)getActivity()).setExercises(studyExercises, exerciseModeArray);
                    ((MainActivity)getActivity()).setModes(studyExercises, exerciseModeArray);
                    ((MainActivity)getActivity()).startExercise();
                //}
            }



        });

        return view;
    }



}
