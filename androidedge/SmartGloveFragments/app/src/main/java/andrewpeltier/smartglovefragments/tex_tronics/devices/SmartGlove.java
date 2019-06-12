package andrewpeltier.smartglovefragments.tex_tronics.devices;

import android.content.Context;
import android.util.Log;

import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.io.DataLogService;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexImuData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexOnlyData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.ImuOnlyData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.TexTronicsData;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;
import andrewpeltier.smartglovefragments.visualize.Choice;


/** ======================================
 *
 *            SmartGlove Class
 *
 *  ======================================
 *
 *  Child of the TexTronicsDevice class
 *
 *  Stores all of the data that pertains to a single device, including its name, the name and mode of
 *  the exercise that it collected data from, the routine ID, etc. This information is used to publish
 *  data each exercise to the MQTT server.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class SmartGlove extends TexTronicsDevice
{
  //  private String exerciseName;                            // Name of the exercise currently in session

    //private String exerciseName;// Name of the exercise currently in session
    //String fileName_ex;
    private TexTronicsData mData;           // CSV Formatted exercise data

    /** SmartGlove Constructor
     *
     * Takes the following parameters as inputs and uses their data to create a CSV file / JSON formatted
     * MQTT server data entry.
     *
     * @param deviceAddress             -MAC address of the Smart Glove device
     * @param exerciseMode              -Mode of the exercise, being FlexIMU or FlexOnly
     * @param choice                    -Choice of one of the eight exercises
     * @param exerciseID                -ID of the chosen exercise
     * @param routineID                 -ID of the routine
     */
    public SmartGlove(String deviceAddress, ExerciseMode exerciseMode, Choice choice, String exerciseID, String routineID) {
        /*
         * Calls super method, which sets all shared parent variables given the
         * input parameters
         */
        super(deviceAddress, exerciseMode, choice, exerciseID, routineID);
        mDeviceAddress = deviceAddress;

        // Set CSV Header and Data Model depending on exercise mode
        switch (EXERCISE_MODE) {
            case FLEX_IMU:
                mData = new FlexImuData();
                mHeader = "Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z),Mag(x),Mag(y),Mag(z)";
                break;
            case FLEX_ONLY:
                mData = new FlexOnlyData();
                mHeader = "Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky";
                break;

            case IMU_ONLY:
                mData = new ImuOnlyData();
                mHeader = "Device Address,Exercise,Timestamp,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z)";
                break;
        }

        Date date = Calendar.getInstance().getTime();

        // Set Default Output File
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);

        String fileName = dateString + "/" + timeString + "_glove.csv";
        File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);
        setCsvFile(file);

        /* Create 6 different files for 6 different exercises.
         *  How to get the exercise name.?
         * */

//        if(exerciseID != null) {
//          //  exerciseName = MainActivity.exercise_name;
//           // if (exerciseName.equals("Finger Tap"))
//            //{
//                Log.d("", "HEY!!!!! EXERCISE NAME : " + exerciseID);
//
//                String fileName1 = dateString + "/" + timeString + "_glove_"+ "Finger Tap" +".csv";
//                File parentFile1 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file1 = new File(parentFile1, fileName1);
//                setCsvFile(file1);
//
//
//            }
//          //  else if (exerciseName.equals("Closed Grip") )
//           // {
//           //     Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName2 = dateString + "/" + timeString + "_glove_"+ "Closed Grip" +".csv";
//                File parentFile2 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file2 = new File(parentFile2, fileName2);
//                setCsvFile(file2);
//
//          //  }
//
//            //else if (exerciseName.equals("Hand Flip") )
//            //{
//          //      Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName3 = dateString + "/" + timeString + "_glove_"+ "Hand Flip" +".csv";
//                File parentFile3 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file3 = new File(parentFile3, fileName3);
//
//                setCsvFile(file3);
//
//           // }
//           // else if (exerciseName.equals("Finger to Nose"))
//            //{
//              //  Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName4 = dateString + "/" + timeString + "_glove_"+ "Finger to Nose" +".csv";
//                File parentFile4 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file4 = new File(parentFile4, fileName4);
//
//                setCsvFile(file4);
//
//            //}
//            //else if (exerciseName.equals("Hold Hands Out"))
//            //{
//          //      Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName5 = dateString + "/" + timeString + "_glove_"+ "Hold Hands Out" +".csv";
//                File parentFile5 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file5 = new File(parentFile5, fileName5);
//
//                setCsvFile(file5);
//
//            //}
//          //  else if (exerciseName.equals("Resting Hands on Thighs"))
//           // {
//             //   Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName6 = dateString + "/" + timeString + "_glove_"+ "Resting Hands on Thighs" +".csv";
//                File parentFile6 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file6 = new File(parentFile6, fileName6);
//
//                setCsvFile(file6);
//
//
//            //}
//            //else if (exerciseName.equals("Heel Stomp"))
//            //{
//             //   Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName7 = dateString + "/" + timeString + "_glove_"+ "Heel Stomp" +".csv";
//                File parentFile7 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file7 = new File(parentFile7, fileName7);
//
//                setCsvFile(file7);
//            //}
//           // else if (exerciseName.equals("Toe Tap"))
//            //{
//               // Log.d("", "EXERCISE NAME : " + exerciseName);
//
//
//                String fileName8 = dateString + "/" + timeString + "_glove_"+ "Toe Tap" +".csv";
//                File parentFile8 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file8 = new File(parentFile8, fileName8);
//
//                setCsvFile(file8);
//           // }
//            //else if (exerciseName.equals("Walk Steps"))
//            //{
//              //  Log.d("", "EXERCISE NAME : " + exerciseName);
//
//                String fileName9 = dateString + "/" + timeString + "_glove_"+ "Walk Steps" +".csv";
//                File parentFile9 = new File("/storage/emulated/0/Documents");    // FIXME
//                File file9 = new File(parentFile9, fileName9);
//                setCsvFile(file9);
//           // }
        //}


//        String fileName1 = dateString + "/" + timeString + "_glove_"+ exerciseName +".csv";
//        File parentFile1 = new File("/storage/emulated/0/Documents");    // FIXME
//        File file1 = new File(parentFile1, fileName1);
//
//        setCsvFile(file1);


    }

    /** logData()
     *
     * Use the data generated from the constructor to create a CSV file. This will include and mainly
     * consist of the data gathered from a specific exercise.
     *
     * @param context               -State of the application
     * @throws IOException
     */
    @Override
    public void logData(Context context) throws IOException
    {
        super.logData(context); // Validates CSV File

        // Store in CSV File
        String data = mData.toString();
        String logString = mDeviceAddress + "," + EXERCISE_MODE.toString() + "," + data;
        DataLogService.log(context, mCsvFile, logString, mHeader);
    }

    /** clear()
     *
     * Clears the data so that we can reuse this device to store data from a different
     * exercise.
     *
     */
    @Override
    public void clear()
    {
        mData.clear();
    }

    @Override
    public void setTimestamp(long timestamp) {
        try {
            mData.setTimestamp(timestamp);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setThumbFlex(int thumbFlex) {
        try {
            mData.setThumbFlex(thumbFlex);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setIndexFlex(int indexFlex) {
        try {
            mData.setIndexFlex(indexFlex);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setMiddleFlex(int middleFlex) {
        try {
            mData.setMiddleFlex(middleFlex);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setRingFlex(int ringFlex) {
        try {
            mData.setRingFlex(ringFlex);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setPinkyFlex(int pinkyFlex) {
        try {
            mData.setPinkyFlex(pinkyFlex);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setAccX(int accX) {
        try {
            mData.setAccX(accX);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setAccY(int accY) {
        try {
            mData.setAccY(accY);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setAccZ(int accZ) {
        try {
            mData.setAccZ(accZ);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrX(int gyrX) {
        try {
            mData.setGyrX(gyrX);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrY(int gyrY) {
        try {
            mData.setGyrY(gyrY);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrZ(int gyrZ) {
        try {
            mData.setGyrZ(gyrZ);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setMagX(int magX) {
        try {
            mData.setMagX(magX);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setMagY(int magY) {
        try {
            mData.setMagY(magY);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setMagZ(int magZ) {
        try {
            mData.setMagZ(magZ);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }
}