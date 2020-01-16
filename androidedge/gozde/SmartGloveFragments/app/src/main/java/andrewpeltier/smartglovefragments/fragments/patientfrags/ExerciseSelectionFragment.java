package andrewpeltier.smartglovefragments.fragments.patientfrags;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.visualize.Exercise;
import andrewpeltier.smartglovefragments.visualize.ExerciseList;
import andrewpeltier.smartglovefragments.visualize.ExerciseView;

/** ======================================
 *
 *     ExerciseSelectionFragment Class
 *
 *  ======================================
 *
 *  The Exercise Selection Fragment is, as of version 1.0, the first fragment loaded after the
 *  patient mode is activated from the home screen. From this fragment, the user is allowed to
 *  create a "playlist" of exercises, or a routine, that they will then complete.
 *
 *  The fragment is split into two primary pieces, the first of which being a carousel-styled exercise
 *  selection screen. This screen will allow the user to choose what exercises they would like to be in
 *  their routine. The other piece would be the list of exercises currently in the routine. The user
 *  can review this list, and even press an exercise name for a long period of time to remove it.
 *
 *  This fragment can be used by a patient to create and start an exercise playlist, or by a doctor to
 *  create a routine for another patient.
 *
 *  The class implements the DiscreteScrollView which allows us to implement the carousel functionality
 *  on the left side of the screen. In this fragment, our implemented methods allow us to check to see if the
 *  user is interacting with the carousel.
 *
 *
 *
 *  @author Andrew Peltier
 *  @version 1.0
 */
public class ExerciseSelectionFragment extends Fragment implements
        DiscreteScrollView.ScrollStateChangeListener<ExerciseAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ExerciseAdapter.ViewHolder>,
        View.OnClickListener
{
    private static final String TAG = "ExerciseSelectionFragme";
    private List<String> exerciseModes;                                 // List of modes to send to Main Activity
    private List<Exercise> possibleExercises;                           // List of all exercises inside of carousel
    private Exercise current;                                           // Exercise currently looking at in carousel
    private ExerciseView exerciseView;                                  // View for left piece, the exercise selection part
    private DiscreteScrollView exercisePicker;                          // Carousel view
    private static ListView listView;                                   // List view, right piece of the fragment
    public static ArrayList<String> listItems=new ArrayList<String>();  // List of exercises the user has selected for their playlist
    public static ArrayAdapter<String> adapter;                         // Adapter that adds strings to our list
    private Button contButton;                                          // Starts the playlist
    boolean doctorMode;                                                 // Identifies if a user is using this fragment

    /** onCreateView()
     *
     * Called when the view is first created. We use the fragment_exercise_selection XML file to load the view and its
     * properties into the fragment, which is then given to the Main Activity. For the exercise selection fragment, we
     * need to set up the carousel, the playlist, and the next button.
     *
     * @param inflater                      -Used to "inflate" or load the layout inside the main activity
     * @param container                     -Object containing the fragment layout (from MainActivity XML)
     * @param savedInstanceState            -State of the application
     * @return                          The intractable exercise selection fragment view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Set up layout
        View view = inflater.inflate(R.layout.fragment_exercise_selection, container, false);
        exerciseView = view.findViewById(R.id.exercise_view);
        listView = view.findViewById(R.id.listView);
        exerciseModes = new ArrayList<>();

        // Set up carousel selection
        possibleExercises = ExerciseList.get().getExercises();   // ExerciseList elements correspond with carousel item elements
        exercisePicker = view.findViewById(R.id.exercise_picker);
        exercisePicker.setBackgroundColor(Color.TRANSPARENT);
        exercisePicker.setSlideOnFling(true);
        // Use the exercise list to create the adapter for the carousel
        exercisePicker.setAdapter(new ExerciseAdapter(possibleExercises, getActivity()));
        exercisePicker.addOnItemChangedListener(this);
        exercisePicker.addScrollStateChangeListener(this);
        exercisePicker.scrollToPosition(1);
        exercisePicker.setItemTransitionTimeMillis(100);
        exercisePicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        // Set the initial exercise in the carousel
        exerciseView.setChoice(possibleExercises.get(0));

        // Set up list view for the exercise playlist
        listView = view.findViewById(R.id.listView);
        if(MainActivity.getExerciseCount() >= 1)
        {
            // Clear the list once the page is first visited
            String[] choices = MainActivity.getmExerciseChoices();
            listItems.clear();
            for(String choice : choices)
                listItems.add(choice);
        }

        // Create adapter for the elements in our playlist
        adapter=new ArrayAdapter<>(getActivity(),
                R.layout.list_white_text, R.id.list_content,
                listItems);
        listView.setAdapter(adapter);

        /* When an element in the list is clicked for a long time, we remove that
         *  element of the list.
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.i(TAG, "Exercise removed from list");
                listItems.remove(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        /** Sets up the continue button
         *
         * If the doctor is using this fragment, the playlist is used to save a doctor recommended
         * routine to their patient's profile. If a patient is using it, we just use the playlist
         * to start a new routine.
         *
         */
        doctorMode = ((MainActivity)getActivity()).getIsDoctor();
        contButton = view.findViewById(R.id.rou_cont_btn);
        if(doctorMode)
            contButton.setText(R.string.button_set_routine);
        else
            contButton.setText(R.string.button_continue);
        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getCount() == 0)
                    Toast.makeText(getActivity(), "Please select at least one exercise", Toast.LENGTH_SHORT).show();
                else
                {
                    if(doctorMode)
                    {
                        //TODO: Save routine to patient profile
                    }
                    else
                    {
                        // Takes exercises from list and sends them to Main Activity
                        String[] chosenExercises = new String[adapter.getCount()];

                        for(int i = 0; i < adapter.getCount(); i++)
                        {
                            chosenExercises[i] = adapter.getItem(i);
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
                        String[] studyExercises = {"Resting_Hands_on_Thighs","Hold_Hands_Out","Finger_to_Nose","Finger_Tap" ,"Closed_Grip","Hand_Flip"}; /*,"Toe_Tap","Heel_Stomp","Walk_Steps"*/



                        String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);
                        ((MainActivity)getActivity()).setExercises(chosenExercises, exerciseModeArray);
                        ((MainActivity)getActivity()).setModes(studyExercises, exerciseModeArray);
                        ((MainActivity)getActivity()).startExercise();
                    }
                }
            }
        });

        Log.d(TAG, "onCreateView: started");
        return view;
    }

    /** addItems()
     *
     * Called by the ExerciseAdapter. In other words, it is called when a user clicks an exercise
     * icon in the carousel. We take the name of the exercise and add it to our playlist on the right side
     * of the fragment.
     *
     *
     * @param v                 -Not used, our adapter and list are global
     * @param selection         -Add string (exercise name) to the list
     */
    public static void addItems(View v, String selection)
    {
        listItems.add(selection);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() -1);
        Log.i(TAG, selection + " added to the list");
    }

    // Moves the exercise carousel to the next index
    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ExerciseAdapter.ViewHolder currentHolder,
            @Nullable ExerciseAdapter.ViewHolder newHolder)
    {
        current = possibleExercises.get(currentIndex);
        if (newIndex >= 0 && newIndex < exercisePicker.getAdapter().getItemCount())
        {
            Exercise next = possibleExercises.get(newIndex);
            exerciseView.onScroll(1f - Math.abs(position), current, next);
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable ExerciseAdapter.ViewHolder viewHolder, int adapterPosition)
    {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (viewHolder != null)
        {
            exerciseView.setChoice(possibleExercises.get(adapterPosition));
            viewHolder.showText();
        }
    }

    @Override
    public void onScrollStart(@NonNull ExerciseAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        currentItemHolder.hideText();
    }

    /**
     *  Overriden methods that we don't add any additional code to
     */
    @Override
    public void onScrollEnd(@NonNull ExerciseAdapter.ViewHolder currentItemHolder, int adapterPosition) { }
    @Override
    public void onClick(View view) { }
}
