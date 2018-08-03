package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.activities.ExerciseInstructions;
import edu.uri.wbl.tex_tronics.smartglove.activities.ExerciseSelection;
import edu.uri.wbl.tex_tronics.smartglove.activities.FinishActivity;
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
    private static ArrayDeque<String> mNames;
    private static Intent testIntent;

    public static void setManager(String[] deviceAddressList, String[] deviceTypeList,
                                  String[] exerciseChoices, String[] exerciseModes)
    {
        Log.v(TAG, "Setting T.T Exercise Manager");
        mDeviceAddressList = deviceAddressList;
        mDeviceTypeList = deviceTypeList;
        mExerciseChoices = exerciseChoices;
        mExerciseModes = exerciseModes;
        mNames = new ArrayDeque<>(Arrays.asList(exerciseChoices));
    }

    public static void clearManager()
    {
        Log.v(TAG, "Clearing T.T Exercise Manager");
        mDeviceAddressList = null;
        mDeviceTypeList = null;
        mExerciseChoices = null;
        mExerciseModes = null;
    }

    public static void startExercise(Context mContext)
    {
        Log.v(TAG, "Starting T.T Exercise...");
        Log.e(TAG, "Address Size: " + mDeviceAddressList.length);
        Log.e(TAG, "Types Size: " + mDeviceTypeList.length);
        Log.e(TAG, "Choices Size: " + mExerciseChoices.length);
        Log.e(TAG, "Modes Size: " + mExerciseModes.length);
        if(mNames.size() > 0)
        {
            String nextExercise = mNames.pop();

            Intent intent = new Intent(mContext, ExerciseInstructions.class);
            intent.putExtra(EXERCISE_NAME, nextExercise);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();

//            if(nextExercise.equals("Screen Tap"))
//            {
//                intent = new Intent(mContext, ScreenTapActivity.class);
//                mContext.startActivity(intent);
//                ((Activity)mContext).finish();
//            }
//            else
//            {
//                intent.putExtra(EXERCISE_NAME, nextExercise);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                mContext.startActivity(intent);
//                ((Activity)mContext).finish();
//            }
        }
        else
        {
            Intent intent = new Intent(mContext, FinishActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        }
    }

    public static int getExerciseCount()
    {
        if(mExerciseChoices != null)
            return mExerciseChoices.length;
        else
            return 0;
    }

    public static String[] getmDeviceAddressList() {
        return mDeviceAddressList;
    }

    public static String[] getmDeviceTypeList() {
        return mDeviceTypeList;
    }

    public static String[] getmExerciseModes() {
        return mExerciseModes;
    }

    public static String[] getmExerciseChoices()
    {
        return mExerciseChoices;
    }
}
