package andrewpeltier.smartglovefragments.tex_tronics.devices;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import andrewpeltier.smartglovefragments.ble.GattDevices;
import andrewpeltier.smartglovefragments.io.DataLogService;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexOnlyData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.ImuOnlyData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexImuData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.TexTronicsData;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;
import andrewpeltier.smartglovefragments.visualize.Choice;
import andrewpeltier.smartglovefragments.visualize.Exercise;


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
    private TexTronicsData mData;           // CSV Formatted exercise data.

    String header;



    String header_flexOnly = "Device Address,Exercise,Timestamp,Thumb,Index,Ring,Pinky";

    String header_imuonly = "Device Address,Exercise,Timestamp,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z)";


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
    public SmartGlove(int id, String exerciseName,int flag,String deviceAddress, String exerciseMode, Choice choice, String exerciseID, String routineID) {
        /*
         * Calls super method, which sets all shared parent variables given the
         * input parameters
         */
        super(deviceAddress, exerciseMode, choice, exerciseID, routineID);
        mDeviceAddress = deviceAddress;

        // Set CSV Header and Data Model depending on exercise mode
        switch (exerciseMode) {
            case "Flex + IMU":
                mData = new FlexImuData();
                mHeader = "Device Address,Exercise,Timestamp,Thumb,Ring,Middle,Index,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z)\n";
                break;
            case "Flex Only":
                mData = new FlexOnlyData();
                mHeader = "Device Address,Exercise,Thumb,Index,Middle,Ring,Pinky";
                break;
            case "Imu Only":
                mData = new ImuOnlyData();
                mHeader = "Device Address,Exercise,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z)\n ";
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
        create_files( id,  exerciseName, flag);



    }
    public void create_files(int id, String exerciseName,int flag){


        Date date = Calendar.getInstance().getTime();

        // Set Default Output File
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);

        /* Create 9 different files for 9 different exercises.
         *  How to get the exercise name.?
         * */

        // Get the device type for each exercise.
        String exerciseDeviceType= Exercise.getDeviceName(exerciseName); //glove or shoe
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();



        //Log.e("Data log DEVICE TYPE CONNECTION=", gattDevices);
        String existingDevice = null;
        for(int i = 0; i <deviceAddressList.length; i++ ) {

            existingDevice = deviceAddressList[i];

            if (existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR)) {
                exerciseDeviceType = "LEFT_GLOVE_";
            } else if (existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR)) {
                exerciseDeviceType = "RIGHT_GLOVE_";
            } else if (existingDevice.equals(GattDevices.LEFT_SHOE_ADDR)) {
                exerciseDeviceType = "LEFT_SHOE_";
            } else if (existingDevice.equals(GattDevices.RIGHT_SHOE_ADDR)) {
                exerciseDeviceType = "RIGHT_SHOE_";
            }
        }



        /* Debug statements... */
        Log.e("Data log FLAG!!=", String.valueOf(flag));
        Log.e("Data log DEVICE TYPE=", exerciseDeviceType);
        Log.e("Data log EXERCISE NAME=", exerciseName.toString());
        Log.e("Data log ID NUMBER=", String.valueOf(id));



//        String folder_main = "/storage/emulated/0/Documents";
//        //Creates new folder.
//        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//
//        if (!f.exists()) {
//            f.mkdirs();
//        }

        //  for(int i = 0 ; i <exerciseName.length();i++){
        String fileName = dateString + "/" + String.valueOf(id) + "/" + timeString + "_" + exerciseDeviceType + exerciseName + ".csv";

        File parentFile = new File( "/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);
        setCsvFile(file);


        /*  Set the header according to the exercises. */
//        if(flag == 1 || flag ==0 ){
//            header = header_imuonly;
//        }
//        else if(flag == 2){
//            header = header_imuonly;
//        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(mHeader);
           // outputStreamWriter.write(mData.toString());

            outputStreamWriter.close();
            Log.d("","Creating new CSV file");

        }catch (IOException e){

            Log.d( "", "NOT Creating new CSV");
            e.printStackTrace();
        }

        //}

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
        String logString = mDeviceAddress + "," + MainActivity.exercise_mode + "," + data;
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
    public void setAccX(short accX) {
        try {
            mData.setAccX(accX);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setAccY(short accY) {
        try {
            mData.setAccY(accY);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setAccZ(short accZ) {
        try {
            mData.setAccZ(accZ);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrX(short gyrX) {
        try {
            mData.setGyrX(gyrX);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrY(short gyrY) {
        try {
            mData.setGyrY(gyrY);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

    @Override
    public void setGyrZ(short gyrZ) {
        try {
            mData.setGyrZ(gyrZ);
        } catch (IllegalDeviceType e) {
            Log.e("SmartGlove", e.toString());
        }
    }

//    @Override
//    public void setMagX(int magX) {
//        try {
//            mData.setMagX(magX);
//        } catch (IllegalDeviceType e) {
//            Log.e("SmartGlove", e.toString());
//        }
//    }
//
//    @Override
//    public void setMagY(int magY) {
//        try {
//            mData.setMagY(magY);
//        } catch (IllegalDeviceType e) {
//            Log.e("SmartGlove", e.toString());
//        }
//    }
//
//    @Override
//    public void setMagZ(int magZ) {
//        try {
//            mData.setMagZ(magZ);
//        } catch (IllegalDeviceType e) {
//            Log.e("SmartGlove", e.toString());
//        }
//    }
}