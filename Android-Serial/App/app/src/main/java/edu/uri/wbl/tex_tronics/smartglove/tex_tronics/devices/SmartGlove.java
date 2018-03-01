package edu.uri.wbl.tex_tronics.smartglove.tex_tronics.devices;

import android.content.Context;
import android.util.Log;

import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;
import edu.uri.wbl.tex_tronics.smartglove.mqtt.MqttConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.data_types.FlexImuData;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.data_types.FlexOnlyData;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.data_types.TexTronicsData;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by mcons on 2/28/2018.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */

public class SmartGlove extends TexTronicsDevice {
    private TexTronicsData mData;

    public SmartGlove(String deviceAddress, ExerciseMode exerciseMode) {
        super(deviceAddress, exerciseMode);

        mDeviceAddress = deviceAddress;

        // Set CSV Header and Data Model
        switch (EXERCISE_MODE) {
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

    @Override
    public void logData(Context context) throws IOException {
        super.logData(context); // Validates CSV File

        // Store in CSV File
        String data = mData.toString();
        String logString = mDeviceAddress + "," + EXERCISE_MODE.toString() + "," + data;
        DataLogService.log(context, mCsvFile, logString, mHeader);
    }

    @Override
    public void clear() {
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
