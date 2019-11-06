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

import andrewpeltier.smartglovefragments.io.DataLogService;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexImuData;
import andrewpeltier.smartglovefragments.tex_tronics.data_types.FlexOnlyData;
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
    public SmartGlove(String deviceAddress, ExerciseMode exerciseMode, Choice choice, String exerciseID, String routineID)
    {
        /*
         * Calls super method, which sets all shared parent variables given the
         * input parameters
         */
        super(deviceAddress, exerciseMode, choice, exerciseID, routineID);
        mDeviceAddress = deviceAddress;

        // Set CSV Header and Data Model depending on exercise mode
        switch (EXERCISE_MODE)
        {
            case FLEX_IMU:
                mData = new FlexImuData();
                mHeader = "Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z),Mag(x),Mag(y),Mag(z)";
                break;
            case FLEX_ONLY:
                mData = new FlexOnlyData();
                mHeader = "Device Address,Exercise,Timestamp,Thumb,Index,Middle,Ring,Pinky";
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