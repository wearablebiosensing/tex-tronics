package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.activities.ExerciseInstructions;
import edu.uri.wbl.tex_tronics.smartglove.activities.ExerciseSelection;
import edu.uri.wbl.tex_tronics.smartglove.activities.GloveExerciseActivity;
import edu.uri.wbl.tex_tronics.smartglove.activities.MainActivity;
import edu.uri.wbl.tex_tronics.smartglove.activities.ScreenTapActivity;

public class TexTronicsExerciseManager
{
    private static final String TAG = "TTExerciseManager";
    private static final String EXERCISE_NAME = "uri.wbl.tex_tronics.name";

    private static String[] mDeviceAddressList;
    private static String[] mDeviceTypeList;
    private static String[] mExerciseChoices;
    private static String[] mExerciseModes;

    public static void setManager(String[] deviceAddressList, String[] deviceTypeList,
                                  String[] exerciseChoices, String[] exerciseModes)
    {
        mDeviceAddressList = deviceAddressList;
        mDeviceTypeList = deviceTypeList;
        mExerciseChoices = exerciseChoices;
        mExerciseModes = exerciseModes;
    }

    public static void startExercise(Intent intent, Context mContext)
    {
        if(mExerciseChoices.length > 0)
        {
            String nextExercise = mExerciseChoices[0];
            mExerciseChoices = Arrays.copyOfRange(mExerciseChoices, 1, mExerciseChoices.length);

            if(nextExercise.equals("Screen Tap"))
            {
                Log.v(TAG, "Launching ExerciseSelection Activity...");
                intent = new Intent(mContext, ScreenTapActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
//                ((Activity)mContext).finish();
            }
            else
            {
                Log.v(TAG, "Launching GloveExercise Activity...");
//                Intent intent = new Intent(mContext, GloveExerciseActivity.class);
                intent.putExtra(EXERCISE_NAME, nextExercise);
//                intent.putExtra(EXTRA_DEVICE_ADDRS, mDeviceAddressList);
//                intent.putExtra(EXTRA_DEVICE_TYPES, mDeviceTypeList);
//                intent.putExtra(EXTRA_EXERCISE_MODES, mExerciseModes);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
//                ((Activity)mContext).finish();
                Log.i(TAG, "Finished!!");
            }
        }
        else
        {
            intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        }
    }

//    private void launchInstructionActivity(int selection)
//    {
//        if(selection <= 3)
//        {
//            Log.v(TAG, "Launching GloveExercise Activity...");
//            exerciseSelection = selection;
//            Intent intent = new Intent(context, GloveExerciseActivity.class);
//            String[] exerciseModeArray = exerciseModes.toArray(new String[exerciseModes.size()]);
//            intent.putExtra(EXTRA_DEVICE_ADDRS, deviceAddressList);
//            intent.putExtra(EXTRA_DEVICE_TYPES, deviceTypeList);
//            intent.putExtra(EXTRA_EXERCISE_MODES, exerciseModeArray);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
//        else
//        {
//            exerciseSelection = selection;
//            Log.v(TAG, "Launching ExerciseSelection Activity...");
//            Intent intent = new Intent(context, ExerciseInstructions.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
//    }


    public static String[] getmDeviceAddressList() {
        return mDeviceAddressList;
    }

    public static String[] getmDeviceTypeList() {
        return mDeviceTypeList;
    }

    public static String[] getmExerciseModes() {
        return mExerciseModes;
    }

    public static int getExerciseCount()
    {
        if(mExerciseChoices != null)
            return mExerciseChoices.length;
        else
            return 0;
    }
    public static String[] getmExerciseChoices()
    {
        return mExerciseChoices;
    }

}
