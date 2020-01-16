package andrewpeltier.smartglovefragments.fragments.patientfrags;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import andrewpeltier.smartglovefragments.R;
import andrewpeltier.smartglovefragments.ble.GattDevices;
import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService;
import andrewpeltier.smartglovefragments.tex_tronics.devices.SmartGlove;
import andrewpeltier.smartglovefragments.tex_tronics.enums.DeviceType;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.visualize.Choice;
import andrewpeltier.smartglovefragments.visualize.Exercise;
import pl.droidsonroids.gif.GifImageView;

import static andrewpeltier.smartglovefragments.tex_tronics.TexTronicsManagerService.*;


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
    private TextView patient_id;



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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_instruction, container, false);
        startExerciseButton = view.findViewById(R.id.start_exercise_button);
        instrImage = view.findViewById(R.id.stexercise_side_image);
        instrText = view.findViewById(R.id.instructions_text);
        patient_id = view.findViewById(R.id.patientid);

        // Sets up the image according to the exercise name
        exerciseName = MainActivity.exercise_name;
        if (exerciseName != null) {
            setSideViews(exerciseName);
        }

        Log.d(TAG, "Main activity COUNTER EIF== " +  MainActivity.counter);
        Log.d(TAG, "MainActivity.exercise_mode  ===== " + MainActivity.exercise_mode);



        // Connect to the appropriate device(s)
        checkConnection(MainActivity.exercise_mode);
        List<Integer> ident = new ArrayList<>();
        try {
            ident  = UserRepository.getInstance(getActivity().getApplicationContext()).getAllIdentities();
        }
        catch(Exception e){
            Log.d(TAG, "onClick: Error with identities");
        }
        patient_id.setText(Integer.toString(ident.size()));

        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the exercise name to determine what exercise fragment to move to
                if (exerciseName != null) {
                    Log.d(TAG, "onCreateView: Removing in start exercise button");
                    /*
                     * Screen Tap exercise requires a different fragment than the other exercises.
                     */
                    if (exerciseName.equals("Screen Tap")) {
                        ((MainActivity) getActivity()).addFragment(new ScreenTapFragment(), "ScreenTapFragment");
                    } else {
                        ((MainActivity) getActivity()).addFragment(new DeviceExerciseFragment(), "DeviceExerciseFragment");
                    }
                }
            }
        });

//        if (MainActivity.DeviceConection != 1) {
//            startExerciseButton.setEnabled(false);
//
//        }
//        else if(MainActivity.DeviceConection == 1){
//            startExerciseButton.setEnabled(true);
//
//        }

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
        if (name.equals("Finger_Tap"))
        {
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrText.setText(SmartGloveInterface.StudyInstructionsText.FINGER_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.FINGER_TAP_GIF);
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Closed_Grip"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.CLOSED_GRIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.CLOSED_GRIP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Hand_Flip"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HAND_FLIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HAND_FLIP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Finger_to_Nose"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.FINGER_TONOSE_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.SCREEN_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Hold_Hands_Out"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HOLD_HANDS_OUT_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HANDS_HOLD_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Resting_Hands_on_Thighs"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.RESTING_HANDS_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.RESTING_HANDS_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Heel_Stomp"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.HEEL_STOMP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.HEEL_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Toe_Tap"))
        {
            instrText.setText(SmartGloveInterface.StudyInstructionsText.TOE_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.StudyInstructionsImage.TOE_TAP_GIF);
            instrImage.getLayoutParams().width = 660;
            instrImage.getLayoutParams().height = 371;
            instrImage.setVisibility(View.VISIBLE);
        }
        else if (name.equals("Walk_Steps"))
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

    public static int flag=3;

    private void checkConnection(String exercise_modes)
    {
        Log.d(TAG, "checkConnection: Checking connection");
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();
        if(deviceAddressList != null && deviceAddressList.length > 0 && MainActivity.CONNECTED)
        {
            for(int devindex = 0; devindex<deviceAddressList.length; devindex++) {
                // Checks to see the types of devices you are already connected to
                Log.d(TAG, "checkConnection: device list exists");
                String existingDevice = deviceAddressList[devindex];
//
//                //------------------------------------------------------------
//                if (/*exercise_modes.equals("Imu Only")*/exerciseName.equals("Hold_Hands_Out") || exerciseName.equals("Resting_Hands_on_Thighs")
//                        || exerciseName.equals("Hand_Flip")
//                ) {
//                    flag = 3;
//                    Log.e(TAG, "flag=" + flag);
//                } else if (/*exercise_modes.equals("Flex Only")*/exerciseName.equals("Finger_to_Nose") || exerciseName.equals("Heel_Stomp")
//                        || exerciseName.equals("Finger_Tap") || exerciseName.equals("Toe_Tap") || exerciseName.equals("Closed_Grip") || exerciseName.equals("Walk_Steps")) {
//                    flag = 3;
//                    Log.e(TAG, "flag=" + flag);
//                }
//                //------------------------------------------------------------
//
                if (existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR) || existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR) || existingDevice.equals(GattDevices.LEFT_SHOE_ADDR) || existingDevice.equals(GattDevices.RIGHT_SHOE_ADDR)) {
                    existingDevice = "Glove";
                }
                //else if (existingDevice.equals(GattDevices.LEFT_SHOE_ADDR) || existingDevice.equals(GattDevices.RIGHT_SHOE_ADDR) || existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR) || existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR))
                //    existingDevice = "Shoe";
                // If your device is not required for the following exercise, it changes the device list
                // and reconnects to the appropriate devices
                String exerciseDeviceType = Exercise.getDeviceName(exerciseName);
                if (!existingDevice.equals(exerciseDeviceType)) {
                    ((MainActivity) getActivity()).disconnect();
                    changeConnection(exerciseDeviceType);
                } else
                    Log.d(TAG, "checkConnection: already connected to appropriate device");
            }
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
            String[] deviceAddressList = new String[] {GattDevices.LEFT_GLOVE_ADDR,GattDevices.RIGHT_GLOVE_ADDR,GattDevices.LEFT_SHOE_ADDR, GattDevices.RIGHT_SHOE_ADDR};
            String[] deviceTypeList = new String[] {DeviceType.SMART_GLOVE.toString(),DeviceType.SMART_GLOVE.toString(),DeviceType.SMART_GLOVE.toString(), DeviceType.SMART_GLOVE.toString()};
            ((MainActivity)getActivity()).setDeviceLists(deviceAddressList, deviceTypeList);
            ((MainActivity)getActivity()).connect();
        }
        else
        {
            Log.d(TAG, "checkConnection: not connecting to shoes");
//            String[] deviceAddressList = new String[] {GattDevices.LEFT_SHOE_ADDR, GattDevices.RIGHT_SHOE_ADDR,GattDevices.LEFT_GLOVE_ADDR,GattDevices.RIGHT_GLOVE_ADDR};
//            String[] deviceTypeList = new String[] {DeviceType.SMART_SOCK.toString(), DeviceType.SMART_SOCK.toString(),DeviceType.SMART_GLOVE.toString(),DeviceType.SMART_GLOVE.toString()};
//            ((MainActivity)getActivity()).setDeviceLists(deviceAddressList, deviceTypeList);
//            ((MainActivity)getActivity()).connect();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying fragment...");
        super.onDestroy();
    }
}

