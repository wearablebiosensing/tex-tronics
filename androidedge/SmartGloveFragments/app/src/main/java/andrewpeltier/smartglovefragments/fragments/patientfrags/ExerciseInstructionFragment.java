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
import android.widget.TextView;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.ble.GattDevices;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.enums.DeviceType;
import andrewpeltier.smartglovefragments.visualize.Exercise;
import pl.droidsonroids.gif.GifImageView;



/** ======================================
 *
 *    ExerciseInstructionFragment Class
 *
 *  ======================================
 *
 *      This fragment loads into the main activity holder before the exercise starts
 *  and after the exercises have been selected. Along with visually providing the user
 *  instructions on how to complete the exercise, this fragment is responsible for changing
 *  the connection between devices depending on the current exercise.
 *
 *  @author Andrew Peltier
 *  @version 1.0
 */
public class ExerciseInstructionFragment extends Fragment
{
    private static final String TAG = "ExerciseInstruction";
    private String exerciseName;                // Name of the current exercise
    private Button startExerciseButton;         // Button that starts the exercise when the user is ready
    private TextView instrText;                 // Instructions to the user on how to complete the exercise
    private GifImageView instrImage;            // Gif image that corresponds to the exercise


    /** onCreateView()
     *
     * Called when the view is first created. We use the fragment_exercise_instruction XML file to load the view and its
     * properties into the fragment, which is then given to the Main Activity. For the instruction fragment, we set up the
     * button, image, and text views, as well as compare the currently connected devices to the current exercise.
     *
     * @param inflater                      -Used to "inflate" or load the layout inside the main activity
     * @param container                     -Object containing the fragment layout (from MainActivity XML)
     * @param savedInstanceState            -State of the application
     * @return                          The intractable instruction fragment view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_instruction, container, false);
        startExerciseButton = view.findViewById(R.id.start_exercise_button);
        instrImage = view.findViewById(R.id.stexercise_side_image);
        instrText = view.findViewById(R.id.instructions_text);

        // Sets up the image according to the exercise name
        exerciseName = MainActivity.exercise_name;
        if(exerciseName != null)
        {
            setSideViews(exerciseName);
        }

        // Connect to the appropriate device(s)
        checkConnection();

        // Set up the exercise button click method
        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // Use the exercise name to determine what exercise fragment to move to
                if(exerciseName != null)
                {
                    Log.d(TAG, "onCreateView: Removing in start exercise button");
                    /*
                     * Screen Tap exercise requires a different fragment than the other exercises.
                     */
                    if(exerciseName.equals("Screen Tap"))
                    {
                        ((MainActivity)getActivity()).addFragment(new ScreenTapFragment(), "ScreenTapFragment");
                    }
                    else
                    {
                        ((MainActivity)getActivity()).addFragment(new DeviceExerciseFragment(), "DeviceExerciseFragment");
                    }
                }
            }
        });

        Log.d(TAG, "onCreateView: " + exerciseName + "  Instructions Started.");
        return view;
    }

    /** setSideViews
     *
     * Just like in the DeviceExerciseFragment, this method uses the name of the exercise to load our
     * gif image with its corresponding image
     *
     * @param name
     */
    private void setSideViews(String name)
    {
        if (name.equals("Finger Tap"))
        {
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrText.setText(SmartGloveInterface.StudyInstructionsText.FINGER_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.FINGER_TAP_GIF);
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Closed Grip"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.CLOSED_GRIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.CLOSED_GRIP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Hand Flip"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HAND_FLIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HAND_FLIP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Finger to Nose"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.FINGER_TONOSE_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.SCREEN_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Hold Hands Out"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HOLD_HANDS_OUT_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HANDS_HOLD_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Resting Hands on Thighs"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.RESTING_HANDS_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.WALK_STEPS_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Heel Stomp"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HEEL_STOMP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HEEL_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Toe Tap"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.TOE_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.TOE_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Walk Steps"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.GAIT_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.WALK_STEPS_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
    }

    /** checkConnection()
     *
     * Checks to see if we are connected to any BLE device, and if so, whether the
     * connected device should be used for this particular exercise.
     *
     */

    public static int flag=0;

    private void checkConnection()
    {
        Log.d(TAG, "checkConnection: Checking connection");
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();
        if(deviceAddressList != null && deviceAddressList.length > 0 && MainActivity.CONNECTED)
        {
            // Checks to see the types of devices you are already connected to
            Log.d(TAG, "checkConnection: device list exists");
            String existingDevice = deviceAddressList[0];
            //------------------------------------------------------------
            if (exerciseName.equals("Finger to Nose")|| exerciseName.equals("Hand Flip") ||
                    exerciseName.equals("Closed Grip") || exerciseName.equals("Finger Tap")){
                flag=1;
                Log.d(TAG, "flag=" + flag);}
            else if(exerciseName.equals("Resting Hands on Thighs") || exerciseName.equals("Heel Stomp") ||
                    exerciseName.equals("Toe Tap")|| exerciseName.equals("Hold Hands Out")){
                flag=2;
                Log.d(TAG, "flag=" + flag);
            }
            //------------------------------------------------------------
            if(existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR) || existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR))
                existingDevice = "Glove";

            else
                existingDevice = "Shoe";
            // If your device is not required for the following exercise, it changes the device list
            // and reconnects to the appropriate devices
            String exerciseDeviceType = Exercise.getDeviceName(exerciseName);
            if(!existingDevice.equals(exerciseDeviceType))
            {
                ((MainActivity)getActivity()).disconnect();
                changeConnection(exerciseDeviceType);
            }
            else
                Log.d(TAG, "checkConnection: already connected to appropriate device");
        }
        else
        {
            // If you are currently not connected to devices, it will connect
            // you to the devices required for this exercise
            Log.d(TAG, "checkConnection: device list does not exist");
            String exerciseDeviceType = Exercise.getDeviceName(exerciseName);
            changeConnection(exerciseDeviceType);
        }
    }

    /** changeConnection()
     *
     * Called from checkConnection(). We've found that we were not connected to any devices
     * or the wrong devices for this current exercise. We simply create a list of devices
     * that we need to connect to, then send that list to the main activity.
     *
     * @param exerciseDeviceType            -The type of device(s) that we need to connect to,
     *                                      either being Glove or Shoe
     */
    private void changeConnection(String exerciseDeviceType)
    {
        if(exerciseDeviceType.equals("Glove"))
        {
            Log.d(TAG, "checkConnection: connecting to gloves");
            String[] deviceAddressList = new String[] {GattDevices.LEFT_GLOVE_ADDR,GattDevices.RIGHT_GLOVE_ADDR};
            String[] deviceTypeList = new String[] {DeviceType.SMART_GLOVE.toString(),DeviceType.SMART_GLOVE.toString()};
            ((MainActivity)getActivity()).setDeviceLists(deviceAddressList, deviceTypeList);
            ((MainActivity)getActivity()).connect();
        }
        else
        {
            Log.d(TAG, "checkConnection: connecting to shoes");
            String[] deviceAddressList = new String[] {GattDevices.LEFT_SHOE_ADDR, GattDevices.RIGHT_SHOE_ADDR};
            String[] deviceTypeList = new String[] {DeviceType.SMART_SOCK.toString(), DeviceType.SMART_SOCK.toString()};
            ((MainActivity)getActivity()).setDeviceLists(deviceAddressList, deviceTypeList);
            ((MainActivity)getActivity()).connect();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying fragment...");
        super.onDestroy();
    }
}

