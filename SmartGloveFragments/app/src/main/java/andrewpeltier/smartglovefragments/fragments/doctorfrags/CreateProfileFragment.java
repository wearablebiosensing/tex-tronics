package andrewpeltier.smartglovefragments.fragments.doctorfrags;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.fragments.HomeFragment;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;

public class CreateProfileFragment extends Fragment
{
    private static final String TAG = "CreateProfileFragment";
    private TextView instrText, nameText, ageText, heightText, weightText, diagText;
    private Button nextAttrBtn, backAttrBtn, dateBtn;
    private EditText editName;
    private Spinner spinner;
    private Animation fadeOutAnim, fadeInAnim;

    private int state = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile_creation, container, false);
        setHasOptionsMenu(true);

        // Set up views
        instrText = view.findViewById(R.id.instrText);
        nameText = view.findViewById(R.id.nameText);
        ageText = view.findViewById(R.id.ageText);
        heightText = view.findViewById(R.id.heightText);
        weightText = view.findViewById(R.id.weightText);
        diagText = view.findViewById(R.id.diagText);
        nextAttrBtn = view.findViewById(R.id.nextAttrBtn);
        backAttrBtn = view.findViewById(R.id.backBtn);
        editName = view.findViewById(R.id.editName);
        dateBtn = view.findViewById(R.id.dateBtn);
        spinner = view.findViewById(R.id.spinner);
        fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        fadeInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        // Sets navigation button clicks
        nextAttrBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick: Next pressed. Checking instruction field...");
                changeInstruction();
            }
        });
        backAttrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finds the correct view, then launches method
                Log.d(TAG, "onClick: Back pressed. Moving to previous instruction...");
                if(state == 1)
                {
                    state--;
                    nextInstruction(dateBtn);
                }
                if(state > 1)
                {
                    state--;
                    nextInstruction(spinner);
                }
                else
                    Log.d(TAG, "onClick: Back pressed, but cannot go back further");
            }
        });

        Log.d(TAG, "onCreateView: view created");
        return view;
    }

    /** changeInstruction()
     *
     * Checks to see if the current instructions have been filled
     * out correctly (i.e. the field has been filled out). If so, the
     * user can move on to the next instruction. In short, this method solely
     * checks the field before it proceeds, and if not filled out, prints out
     * an error to the user.
     *
     */
    private void changeInstruction()
    {
        switch(state)
        {
            // State 0: Name
            case 0:
                if(!editName.getText().toString().equals("Name") && !editName.getText().toString().equals(""))
                {
                    Log.d(TAG, "changeInstruction: Success! Changing instruction...");
                    nameText.setText(editName.getText().toString());
                    state++;
                    nextInstruction(editName);
                }
                else
                    Toast.makeText(getActivity(),"Please enter a valid name", Toast.LENGTH_SHORT).show();
                break;
            // State 1: Date of Birth
            case 1:
                if(!ageText.getText().toString().equals(""))
                {
                    Log.d(TAG, "changeInstruction: Success! Changing instruction...");
                    state++;
                    nextInstruction(dateBtn);
                }
                else
                    Toast.makeText(getActivity(),"Please select a date", Toast.LENGTH_SHORT).show();
                break;
            // State 2: Height
            case 2:
                if(!heightText.getText().toString().equals(""))
                {
                    Log.d(TAG, "changeInstruction: Success! Changing instruction...");
                    state++;
                    nextInstruction(spinner);
                }
                else
                    Toast.makeText(getActivity(),"Please select your height", Toast.LENGTH_SHORT).show();
                break;
            // State 3: Weight
            case 3:
                if(!weightText.getText().toString().equals(""))
                {
                    Log.d(TAG, "changeInstruction: Success! Changing instruction...");
                    state++;
                    nextInstruction(spinner);
                }
                else
                    Toast.makeText(getActivity(),"Please select your weight", Toast.LENGTH_SHORT).show();
                break;
            // State 4: Diagnosis
            case 4:
                if(!diagText.getText().toString().equals(""))
                {
                    Log.d(TAG, "changeInstruction: Success! Changing instruction...");
                    state++;
                    nextInstruction(spinner);
                }
                else
                    Toast.makeText(getActivity(),"Please select your diagnosis", Toast.LENGTH_SHORT).show();
                break;
            // State 5: Confirmation
            case 5:
                // If user is satisfied, go back to patient feed as of right now
                Toast.makeText(getActivity(),"Profile Created", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).addFragment(new PatientFeedFragment(), "PatientFeedFragment");
                break;
            default:
                Log.d(TAG, "changeInstruction: Error. State not found.");
        }
    }

    /** nextInstruction
     *
     * Fades both the instruction text and the current field view. After the
     * views have been faded out, set up the new views.
     *
     *
     * @param view  -the view that corresponds with the field that
     *               the user has to fill out in the given state
     */
    public void nextInstruction(final View view)
    {
        fadeOutAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                Log.d(TAG, "onAnimationEnd: Animation ended. Setting up instruction...");
                view.setVisibility(View.INVISIBLE);
                if(state <= 5)
                    setupInstruction();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        view.startAnimation(fadeOutAnim);
        instrText.startAnimation(fadeOutAnim);
    }

    /** setupInstruction()
     *
     * This method simply sets up the new views based on what state is
     * now being accessed after either the back button or next button
     * has been pressed.
     *
     */
    public void setupInstruction()
    {
        switch(state)
        {
            case 0:
                Log.d(TAG, "setupInstruction: Loading name...");
                loadName();
                break;
            case 1:
                Log.d(TAG, "setupInstruction: Loading date of birth...");
                loadBirthday();
                break;
            case 2:
                Log.d(TAG, "setupInstruction: Loading height...");
                loadHeight();
                break;
            case 3:
                Log.d(TAG, "setupInstruction: Loading weight...");
                loadWeight();
                break;
            case 4:
                Log.d(TAG, "setupInstruction: Loading diagnosis...");
                loadDiagnosis();
                break;
            case 5: // Confirmation
                Log.d(TAG, "setupInstruction: Loading conformation...");
                instrText.setText("Does this look correct?");
                instrText.startAnimation(fadeInAnim);
                break;
            default:
                Log.d(TAG, "setupInstruction: Error. State not found");
                break;
        }
    }

    /**===============================================
     *
     *      Field Instantiation Methods
     *
     =================================================*/
    private void loadName()
    {
        instrText.setText("Enter Your Name");
        instrText.startAnimation(fadeInAnim);
        editName.setVisibility(View.VISIBLE);
        editName.setText("Name");
        editName.setAnimation(fadeInAnim);
        Log.d(TAG, "loadName: Instruction loaded");
    }

    private void loadBirthday()
    {
        instrText.setText("Select Your Age");
        instrText.startAnimation(fadeInAnim);
        dateBtn.setVisibility(View.VISIBLE);
        /**
         *  Sets up the calendar dialog which allows the user
         *  to pick their date of birth.
         *
         *  Their date of birth, in the form of a text field,
         *  will be set to the date TextView upon selection
         */
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Log.d(TAG, "onDateSet: Date has been set");
                // Updates the text view with the date
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                ageText.setText(sdf.format(myCalendar.getTime()));
            }

        };
        dateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Shows the date dialog picker
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dateBtn.startAnimation(fadeInAnim);
        Log.d(TAG, "loadBirthday: Instruction loaded");
    }

    private void loadHeight()
    {
        instrText.setText("Select Your Height");
        instrText.startAnimation(fadeInAnim);
        spinner.setVisibility(View.VISIBLE);

        // Sets up the adapter with an array from the strings xml
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.height_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

        // When selected, the text from the string array will be set to the
        // height text field
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.d(TAG, "onItemSelected: Height has been set");
                heightText.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinner.setAnimation(fadeInAnim);
        Log.d(TAG, "loadHeight: Instruction loaded");
    }

    private void loadWeight()
    {
        instrText.setText("Select Your Weight");
        instrText.startAnimation(fadeInAnim);
        spinner.setVisibility(View.VISIBLE);

        // Creates a string array with weights from 90 - 589lbs
        String[] weights = new String[501];
        weights[0] = "";
        for(int i = 1; i <= 500; i ++)
        {
            weights[i] = i + 89 + " lbs";
        }

        // Sets the string array to the adapter
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                getActivity(),R.layout.spinner_item, weights);
        adapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter2);

        // Sets the weight field with the selected weight string
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.d(TAG, "onItemSelected: Weight has been set");
                weightText.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinner.setAnimation(fadeInAnim);
        Log.d(TAG, "loadWeight: Instruction loaded");
    }

    private void loadDiagnosis()
    {
        instrText.setText("Whats Your Diagnosis?");
        instrText.startAnimation(fadeInAnim);
        spinner.setVisibility(View.VISIBLE);

        // Sets up the adapter with an array from the strings xml
        final ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                getActivity(), R.array.disorders_array, R.layout.spinner_item);
        adapter3.setDropDownViewResource(R.layout.spinner_item);

        // When selected, the text from the string array will be set to the
        // diagnosis text field
        spinner.setAdapter(adapter3);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.d(TAG, "onItemSelected: Diagnosis has been set");
                diagText.setText(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinner.setAnimation(fadeInAnim);
        Log.d(TAG, "loadDiagnosis: Instruction loaded");
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
