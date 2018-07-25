package edu.uri.wbl.tex_tronics.smartglove.tex_tronics.devices;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.data_types.TexTronicsData;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.ExerciseMode;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by mcons on 2/28/2018.
 */

public abstract class TexTronicsDevice {
    protected final String DEVICE_ADDRESS;
    protected final ExerciseMode EXERCISE_MODE;

    protected String mHeader;
    protected File mCsvFile;
    protected String mDate;

    protected String mDeviceAddress;

    public TexTronicsDevice(@NonNull String deviceAddress, @NonNull ExerciseMode exerciseMode) throws IllegalArgumentException {
        // Validate Bluetooth Device Address Provided
        if(!BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
            throw new IllegalArgumentException("Invalid Device Address");
        }

        // Initialize Constant Data Members
        DEVICE_ADDRESS = deviceAddress;
        EXERCISE_MODE = exerciseMode;

        // Initialize CSV File to NULL
        mCsvFile = null;

        Date date = Calendar.getInstance().getTime();
        mDate = new SimpleDateFormat("MM-dd-yyyy kk:mm:ss:SSS", Locale.US).format(date);
    }

    public void setCsvFile(File file) {
        mCsvFile = file;
    }

    public File getCsvFile() {
        return mCsvFile;
    }

    public String getDeviceAddress(){
        return DEVICE_ADDRESS;
    }

    public String getDate() {
        return mDate;
    }

    public ExerciseMode getExerciseMode() {
        return EXERCISE_MODE;
    }

    public void logData(Context context) throws IOException {
        if(mCsvFile == null) {
            throw new IOException("CSV File Must be Specified");
        }
    }

    public abstract void clear();

    public long getTimestamp() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setTimestamp(long timestamp) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getThumbFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setThumbFlex(int thumbFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }


    public int getIndexFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setIndexFlex(int indexFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getMiddleFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMiddleFlex(int middleFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getRingFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setRingFlex(int ringFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getPinkyFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setPinkyFlex(int pinkyFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getAccX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccX(int accX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getAccy() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccY(int accY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getAccZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccZ(int accZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getGyrX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrX(int gyrX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getGyrY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrY(int gyrY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getGyrZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrZ(int gyrZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getMagX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagX(int magX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getMagY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagY(int magY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public int getMagZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagZ(int magZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }
}
