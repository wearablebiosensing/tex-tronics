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

    //Get the current date.
    Date date = Calendar.getInstance().getTime();

    // Set Default Output File
    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
    String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);
    String header;
    String header_flexOnly = "Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky";

    String header_imuonly = "Device Address,Exercise,Timestamp,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z)";

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

    public void DataLog(int id, String exerciseName,int flag) {

        // Get the device type for each exercise.
        String exerciseDeviceType = Exercise.getDeviceName(exerciseName);
        String[] deviceAddressList = MainActivity.getmDeviceAddressList();

        for(int i = 0; i<deviceAddressList.length ; i++){
        //Log.e("Data log DEVICE TYPE CONNECTION=", gattDevices);
        String existingDevice = deviceAddressList[i];

        if (existingDevice.equals(GattDevices.LEFT_GLOVE_ADDR)) {
            exerciseDeviceType = "LEFT_GLOVE_";
        } else if (existingDevice.equals(GattDevices.RIGHT_GLOVE_ADDR)) {
            exerciseDeviceType = "RIGHT_GLOVE_";
        } else if (existingDevice.equals(GattDevices.LEFT_SHOE_ADDR)) {
            exerciseDeviceType = "LEFT_SHOE_";
        } else if (existingDevice.equals(GattDevices.RIGHT_SHOE_ADDR)) {
            exerciseDeviceType = "RIGHT_SHOE_";
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

        String fileName = dateString + "/" + String.valueOf(id) + "/" + timeString + "_" + exerciseDeviceType + exerciseName + ".csv";

        File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);

        /*  Set the header according to the exercises. */
        if (flag == 1 || flag == 0) {
            header = header_flexOnly;
        } else if (flag == 2) {
            header = header_imuonly;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(header);
            outputStreamWriter.close();
            // BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            //bufferedWriter.write("Hello world!");
            //bufferedWriter.newLine();
            Log.d("", "Creating new CSV file");

        } catch (IOException e) {

            Log.d("", "NOT Creating new CSV");
            e.printStackTrace();
        }

    }
    }
}
