package edu.uri.wbl.tex_tronics.smartglove.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.R;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsExerciseManager;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.TexTronicsManagerService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;
import edu.uri.wbl.tex_tronics.smartglove.visualize.Exercise;
import edu.uri.wbl.tex_tronics.smartglove.visualize.ExerciseList;
import edu.uri.wbl.tex_tronics.smartglove.visualize.ExerciseView;

public class ExerciseSelection extends AppCompatActivity implements
        DiscreteScrollView.ScrollStateChangeListener<ExerciseAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ExerciseAdapter.ViewHolder>,
        View.OnClickListener
{
    private static final String TAG = "ExerciseSelection";
    private static final String EXTRA_DEVICE_ADDRS = "uri.wbl.tex_tronics.devices";
    private static final String EXTRA_DEVICE_TYPES = "uri.wbl.tex_tronics.device_types";
    private static final String EXTRA_EXERCISE_MODES = "uri.wbl.tex_tronics.exercise_modes";

    private String[] deviceAddressList;
    private String[] deviceTypeList;
    private List<String> exerciseModes;
    private static int exerciseSelection;
    private Context context;
    private List<Exercise> possibleExercises;
    private Exercise current;
    private ExerciseView exerciseView;
    private DiscreteScrollView exercisePicker;
    private static ListView listView;
    private static int currentIndex = 1;
    public static ArrayList<String> listItems=new ArrayList<String>();
    public static ArrayAdapter<String> adapter;
    private Button contButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Sets up activity
        super.onCreate(savedInstanceState);
        setTitle(R.string.ab_exercise_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        context = this;
        Log.e(TAG, "Creating ExerciseSelection Activity...");

        // Gets connectivity information
        deviceAddressList = getIntent().getStringArrayExtra(EXTRA_DEVICE_ADDRS);
        for(String addr : deviceAddressList) {
            Log.d(TAG, "1 BT ADDRESS: " + addr);
        }
        deviceTypeList = getIntent().getStringArrayExtra(EXTRA_DEVICE_TYPES);
        for(String type : deviceTypeList) {
            Log.d(TAG, "1 DEVICE TYPE: " + type);
        }
        exerciseModes = new ArrayList<>();

        // Set up layout
        setContentView(R.layout.activity_exercise_sel);
        exerciseView = findViewById(R.id.exercise_view);
        listView = findViewById(R.id.listView);

        possibleExercises = ExerciseList.get().getExercises();
        exercisePicker = findViewById(R.id.exercise_picker);
        exercisePicker.setBackgroundColor(Color.TRANSPARENT);
        exercisePicker.setSlideOnFling(true);
        exercisePicker.setAdapter(new ExerciseAdapter(possibleExercises, this));
        exercisePicker.addOnItemChangedListener(this);
        exercisePicker.addScrollStateChangeListener(this);
        exercisePicker.scrollToPosition(1);
        exercisePicker.setItemTransitionTimeMillis(100);
        exercisePicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        exerciseView.setChoice(possibleExercises.get(0));
        listView = findViewById(R.id.listView);
        //TODO: Needs fixing. Does not work
        if(TexTronicsExerciseManager.getExerciseCount() >= 1)
        {
            String[] choices = TexTronicsExerciseManager.getmExerciseChoices();
            listItems.clear();
            for(String choice : choices)
                listItems.add(choice);
        }

        adapter=new ArrayAdapter<>(this,
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

        contButton = findViewById(R.id.continue_button);
        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getCount() == 0)
                    Toast.makeText(context, "Please select at least one exercise", Toast.LENGTH_SHORT).show();
                else
                {
                    String[] chosenExercises = new String[adapter.getCount()];
                    for(int i = 0; i < adapter.getCount(); i++)
                    {
                        chosenExercises[i] = adapter.getItem(i);
                        exerciseModes.add(ExerciseMode.FLEX_ONLY.toString());
                    }
                    String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);
                    Intent intent = new Intent(context, GloveExerciseActivity.class);
                    TexTronicsExerciseManager.setManager(deviceAddressList, deviceTypeList, chosenExercises, exerciseModeArray);
                    TexTronicsExerciseManager.startExercise(intent, context);
                    finish();
                }
            }
        });
    }

    public static void start(Context context, @NonNull List<String> deviceAddressList, @NonNull List<String> deviceTypeList) {
        Intent intent = new Intent(context, ExerciseSelection.class);
        String[] deviceAddresses = deviceAddressList.toArray(new String[deviceAddressList.size()]);
        String[] deviceTypes = deviceTypeList.toArray(new String[deviceTypeList.size()]);
        intent.putExtra(EXTRA_DEVICE_ADDRS, deviceAddresses);
        intent.putExtra(EXTRA_DEVICE_TYPES, deviceTypes);
        for(String addr : deviceAddressList) {
            Log.d(TAG, "BT ADDRESS: " + addr);
        }
        context.startActivity(intent);
    }

    public static int getExerciseSelection(){return exerciseSelection;}

    @Override
    public void onBackPressed()
    {
        Log.e(TAG, "Back pressed. Navigating to " + getParentActivityIntent());
        Intent intent = this.getParentActivityIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    public static void addItems(View v, String selection)
    {
        listItems.add(selection);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() -1);
        Log.i(TAG, selection + " added to the list");
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

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onScrollEnd(@NonNull ExerciseAdapter.ViewHolder currentItemHolder, int adapterPosition) { }

    @Override
    public void onClick(View view) { }
}
