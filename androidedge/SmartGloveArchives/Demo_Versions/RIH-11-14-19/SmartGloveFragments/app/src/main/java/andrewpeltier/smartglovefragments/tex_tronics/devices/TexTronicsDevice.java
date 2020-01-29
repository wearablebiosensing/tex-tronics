package andrewpeltier.smartglovefragments.tex_tronics.devices;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;
import andrewpeltier.smartglovefragments.visualize.Choice;

/** ======================================
 *
 *     TexTronicsDevice Abstract Class
 *
 *  ======================================
 *
 *  Parent class responsible for formatting device data so that it can be published to
 *  the MQTT server.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public abstract class TexTronicsDevice
{
    /**
     * Constant for the MAC address of the TexTronics Device
     */
    protected final String DEVICE_ADDRESS;
    /**
     * Constant exercise mode, being either FlexOnly or FlexIMU
     */
    protected final String EXERCISE_MODE;
    /**
     * One of the eight exercises that the device is currently collecting data from
     */
    protected final Choice CHOICE;
    /**
     * ID of the currently collected exercise
     */
    protected final String EXERCISE_ID;
    /**
     * ID of the routine currently in session
     */
    protected final String ROUTINE_ID;

    protected String mHeader;               // Header of the CSV file, dependent on device type
    protected File mCsvFile;                // CSV file to be written
    protected String mDate;                 // Date that the CSV file has been generated, including time
    protected String mDeviceAddress;        // MAC address of TexTronics Device

    public TexTronicsDevice(@NonNull String deviceAddress, @NonNull String exerciseMode, @NonNull Choice choice, String exerciseID, String routineID) throws IllegalArgumentException {

        // Validate Bluetooth Device Address Provided
        if(!BluetoothAdapter.checkBluetoothAddress(deviceAddress))
        {
            throw new IllegalArgumentException("Invalid Device Address");
        }

        // Initialize Constant Data Members
        DEVICE_ADDRESS = deviceAddress;
        EXERCISE_MODE = exerciseMode;
        CHOICE = choice;
        EXERCISE_ID = exerciseID;
        ROUTINE_ID = routineID;

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

    public String getExerciseMode() {
        return EXERCISE_MODE;
    }

    public Choice getChoice() {
        return CHOICE;
    }

    public String getExerciseID() {return EXERCISE_ID;}

    public String getRoutineID() {return ROUTINE_ID;}

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

    public short getAccX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccX(short accX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getAccy() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccY(short accY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getAccZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setAccZ(short accZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getGyrX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrX(short gyrX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getGyrY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrY(short gyrY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getGyrZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setGyrZ(short gyrZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getMagX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagX(short magX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getMagY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagY(short magY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public short getMagZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    public void setMagZ(short magZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }
}