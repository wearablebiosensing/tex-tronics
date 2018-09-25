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

public class ExerciseInstructionFragment extends Fragment
{
    private static final String TAG = "ExerciseInstruction";
    private String exerciseName;
    private Button startExerciseButton;
    private TextView instrText;
    private GifImageView instrImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_instruction, container, false);
        startExerciseButton = view.findViewById(R.id.start_exercise_button);
        instrImage = view.findViewById(R.id.stexercise_side_image);
        instrText = view.findViewById(R.id.instructions_text);

        // Sets up the image and
        exerciseName = MainActivity.exercise_name;
        if(exerciseName != null)
        {
            setSideViews(exerciseName);
        }

        checkConnection();

        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(exerciseName != null)
                {
                    Log.d(TAG, "onCreateView: Removing in start exercise button");
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

    private void setSideViews(String name)
    {
        if (name.equals("Finger Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.FINGER_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.FINGER_TAP_GIF);
        }
        else if (name.equals("Closed Grip"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.CLOSED_GRIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.CLOSED_GRIP_GIF);
        }
        else if (name.equals("Hand Flip"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.HAND_FLIP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.HAND_FLIP_GIF);
        }
        else if (name.equals("Screen Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.SCREEN_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.SCREEN_TAP_GIF);
            instrImage.getLayoutParams().width = 428;
            instrImage.getLayoutParams().height = 371;
        }
        else if (name.equals("Heel Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.HEEL_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.HEEL_TAP_GIF);
        }
        else if (name.equals("Toe Tap"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.TOE_TAP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.TOE_TAP_GIF);
        }
        else if (name.equals("Foot Stomp"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.FOOT_STOMP_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.FOOT_STOMP_GIF);
        }
        else if (name.equals("Walk Steps"))
        {
            instrText.setText(SmartGloveInterface.InstructionsText.WALK_STEPS_TEXT);
            instrImage.setBackgroundResource(SmartGloveInterface.InstructionsImage.WALK_STEPS_GIF);
        }
    }

    private void checkConnection()
    {
        Log.d(TAG, "checkConnection: Checking connection");
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();
        if(deviceAddressList != null && deviceAddressList.length > 0 && MainActivity.connected)
        {
            // Checks to see the types of devices you are already connected to
            Log.d(TAG, "checkConnection: device list exists");
            String existingDevice = deviceAddressList[0];
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

    private void changeConnection(String exerciseDeviceType)
    {
        if(exerciseDeviceType.equals("Glove"))
        {
            Log.d(TAG, "checkConnection: connecting to gloves");
            String[] deviceAddressList = new String[] {GattDevices.LEFT_GLOVE_ADDR};
            String[] deviceTypeList = new String[] {DeviceType.SMART_GLOVE.toString()};
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

