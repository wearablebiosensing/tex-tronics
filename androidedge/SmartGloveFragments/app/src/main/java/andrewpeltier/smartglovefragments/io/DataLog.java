package andrewpeltier.smartglovefragments.io;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.FileOutputStream;
import java.util.Locale;

import andrewpeltier.smartglovefragments.ble.GattDevices;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.visualize.Exercise;


/**
 *
 *  ======================================
 *
 *              DataLog Class
 *
 *  ======================================
 *
 *  @author Shehjar Sadhu.
 *  @version 1.0
 *  @Date Created on June 12th 2019.
 *
 */
public class DataLog {
    String header = "DataLog, Device Address, Exercise, TimeStamp, Thumb, Index, Middle, Ring, Pinky";


    //Get the current date.
    Date date = Calendar.getInstance().getTime();

    // Set Default Output File
    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
    String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);


    /* Default Constructor. Does nothing */
    public void DataLog(){
    }

    /*
    * File Constructor.
    *  @Params - 1. Takes in the patient id
    *            2. Takes in the exerciseName as input.
    *            3. Device Exercises type- for seperates which exerciese is for which device. i.e. glove/shoe.
    *  Creates new csv files for each of the exercises.
    * */

    public void DataLog(int id, String exerciseName,String info){

        // Get the device type for each exercise.
        String exerciseDeviceType = Exercise.getDeviceName(exerciseName);
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();



       //Log.e("Data log DEVICE TYPE CONNECTION=", gattDevices);
        String existingDevice = deviceAddressList[0];

        if(existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR))
        {
            exerciseDeviceType = "LEFT_GLOVE_";
        }
        else if(existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR)){
            exerciseDeviceType = "RIGHT_GLOVE_";
        }
        else if(existingDevice.equals(GattDevices.LEFT_SHOE_ADDR)){
            exerciseDeviceType = "LEFT_SHOE_";
        }
        else if (existingDevice.equals(GattDevices.RIGHT_SHOE_ADDR)){
            exerciseDeviceType = "RIGHT_SHOE_";
        }
        /* Debug statements... */
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

        String fileName = dateString + "/" + String.valueOf(id) + "/" + timeString + "_" + exerciseDeviceType + exerciseName + ".csv";

        File parentFile = new File( "/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);
        String insert_string = timeString + "," + info;

        try {
            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(header);
            bufferedWriter.newLine();

            bufferedWriter.write(insert_string);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();


            Log.d("","Creating new CSV file");

        }catch (IOException e){

            Log.d( "", "NOT Creating new CSV");
            e.printStackTrace();
        }


    }
}
