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

public class ExerciseSelectionFragment extends Fragment implements
        DiscreteScrollView.ScrollStateChangeListener<ExerciseAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ExerciseAdapter.ViewHolder>,
        View.OnClickListener
{
    private static final String TAG = "ExerciseSelectionFragme";
    private String[] deviceAddressList;
    private String[] deviceTypeList;
    private List<String> exerciseModes;
    private static int exerciseSelection;
    private List<Exercise> possibleExercises;
    private Exercise current;
    private ExerciseView exerciseView;
    private DiscreteScrollView exercisePicker;
    private static ListView listView;
    private static int currentIndex = 1;
    public static ArrayList<String> listItems=new ArrayList<String>();
    public static ArrayAdapter<String> adapter;
    private Button contButton;
    boolean doctorMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_selection, container, false);

        exerciseModes = new ArrayList<>();

        // Set up layout
        exerciseView = view.findViewById(R.id.exercise_view);
        listView = view.findViewById(R.id.listView);

        // Set up carousel selection
        possibleExercises = ExerciseList.get().getExercises();
        exercisePicker = view.findViewById(R.id.exercise_picker);
        exercisePicker.setBackgroundColor(Color.TRANSPARENT);
        exercisePicker.setSlideOnFling(true);
        exercisePicker.setAdapter(new ExerciseAdapter(possibleExercises, getActivity()));
        exercisePicker.addOnItemChangedListener(this);
        exercisePicker.addScrollStateChangeListener(this);
        exercisePicker.scrollToPosition(1);
        exercisePicker.setItemTransitionTimeMillis(100);
        exercisePicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        exerciseView.setChoice(possibleExercises.get(0));

        // Set up list view
        listView = view.findViewById(R.id.listView);
        if(MainActivity.getExerciseCount() >= 1)
        {
            String[] choices = MainActivity.getmExerciseChoices();
            listItems.clear();
            for(String choice : choices)
                listItems.add(choice);
        }
        adapter=new ArrayAdapter<>(getActivity(),
                R.layout.list_white_text, R.id.list_content,
                listItems);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "Exercise removed from list");
                listItems.remove(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        // Set up continue button
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

                    }
                    else
                    {
                        // Takes exercises from list and sends them to Main Activity
                        String[] chosenExercises = new String[adapter.getCount()];
                        for(int i = 0; i < adapter.getCount(); i++)
                        {
                            chosenExercises[i] = adapter.getItem(i);
                            exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                        }
                        String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);
                        ((MainActivity)getActivity()).setExercises(chosenExercises, exerciseModeArray);
                        ((MainActivity)getActivity()).startExercise();
                    }
                }
            }
        });

        Log.d(TAG, "onCreateView: started");
        return view;
    }

    public static void addItems(View v, String selection)
    {
        listItems.add(selection);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() -1);
        Log.i(TAG, selection + " added to the list");
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable ExerciseAdapter.ViewHolder viewHolder, int adapterPosition) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (viewHolder != null) {
            exerciseView.setChoice(possibleExercises.get(adapterPosition));
            viewHolder.showText();
        }
    }

    @Override
    public void onScrollStart(@NonNull ExerciseAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        currentItemHolder.hideText();
    }

    @Override
    public void onScrollEnd(@NonNull ExerciseAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ExerciseAdapter.ViewHolder currentHolder,
            @Nullable ExerciseAdapter.ViewHolder newHolder) {
        current = possibleExercises.get(currentIndex);
        this.currentIndex = currentIndex;
        if (newIndex >= 0 && newIndex < exercisePicker.getAdapter().getItemCount()) {
            Exercise next = possibleExercises.get(newIndex);
            exerciseView.onScroll(1f - Math.abs(position), current, next);
        }
    }
}
