package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;
import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.exception.TransmitModeException;

/**
 * This class contains all information and methods related to TexTronics Devices.
 * This class is responsible for holding and logging data that is received from a TexTronics
 * Device (usually via TexTronicsManagerService). This class also contains enums listing
 * possible device types, transmit modes, and actions.
 *
 * @author Matthew Constant
 * @version 1.0, 02/27/2018
 */
public class TexTronicsData {
    /**
     * Tag used for logging messages to LogCat.
     *
     * @since 1.0
     */
    private static final String TAG = "TexTronicsData";

    /**
     * This provides the possible transmitting modes a TexTronics Device may be using.
     *
     * @since 1.0
     */
    public enum TexTronicsMode {
        FLEX_IMU,       // FlexSensor Data + IMU Data
        FLEX_ONLY       // FlexSensor Data
    }

    /**
     * This provides the different TexTronics Devices Available.
     *
     * @since 1.0
     */
    public enum TexTronicsDevice {
        SMART_GLOVE,
        SMART_SOCKS
    }

    /**
     * This provides the possible actions a User may request.
     *
     * @since 1.0
     */
    public enum TexTronicsAction {
        connect ("uri.wbl.tex_tronics.ble.connect"),
        disconnect ("uri.wbl.tex_tronics.ble.disconnect");

        private final String mAction;

        private TexTronicsAction(String action) {
            mAction = action;
        }

        public static TexTronicsAction getAction(String action) {
            switch (action) {
                case "uri.wbl.tex_tronics.ble.connect":
                    return connect;
                case "uri.wbl.tex_tronics.ble.disconnect":
                    return disconnect;
                default:
                    return null;
            }
        }

        public String toString() {
            return this.mAction;
        }
    }

    /**
     * The device address corresponding to the TexTronics Device. This must be known when
     * instantiating this object and it can not change once the object is instantiated.
     *
     * @since 1.0
     */
    private final String mDeviceAddress;

    /**
     * The TexTronics Device this object represents. This must be known when
     * instantiating this object and it can not change once the object is instantiated.
     *
     * @since 1.0
     */
    private final TexTronicsDevice mTexTronicsDevice;

    /**
     * The header used when logging data locally using CSV files.
     *
     * @since 1.0
     */
    private final String mHeader;

    /**
     * The File location to save CSV files.
     *
     * @since 1.0
     */
    private final File mFile;

    /**
     * The transmitting mode this TexTronics device is operating in. This must be known when
     * instantiating this object and it can not change once the object is instantiated.
     *
     * @since 1.0
     */
    private final TexTronicsMode mTexTronicsMode;

    /**
     * This contains the current data stored by this object and is used to log the data.
     * This data member is only used if the TexTronics Device is transmitting in FLEX_IMU mode.
     *
     * @since 1.0
     */
    private FlexImuData mFlexImuData;

    /**
     * This contains the current data stored by this object and is used to log the data.
     * This data member is only used if the TexTronics Device is transmitting in FLEX_ONLY mode.
     *
     * @since 1.0
     */
    private FlexOnlyData mFlexOnlyData;

    /**
     * This constructor sets the constant data members such as device address and transmit mode.
     * It also sets the header to be used for CSV files and the file location to save the CSV files.
     *
     * @param deviceAddress The Device Address of the TexTronics BLE Device.
     * @param texTronicsDevice The type of TexTronics Device.
     * @param texTronicsMode The transmit mode of the TexTronics Device.
     * @throws IllegalArgumentException Exception thrown when TexTronics Device or Mode is invalid.
     *
     * @since 1.0
     */
    public TexTronicsData(String deviceAddress, TexTronicsDevice texTronicsDevice, TexTronicsMode texTronicsMode) throws IllegalArgumentException {
        mDeviceAddress = deviceAddress;
        mTexTronicsDevice = texTronicsDevice;
        mTexTronicsMode = texTronicsMode;

        // Get Date and Time (used for mFile filepath and name)
        Date date = Calendar.getInstance().getTime();
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);
        String dateTimeString = dateString + "/" + timeString;
        String fileName;

        // Initialize Transmit Mode Specific Data Members
        switch (mTexTronicsMode) {
            case FLEX_IMU:
                mHeader = "Device Address,Timestamp,Thumb,Index,Middle,Ring,Pinky,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z),Mag(x),Mag(y),Mag(z)";
                mFlexImuData = new FlexImuData();
                break;
            case FLEX_ONLY:
                mHeader = "Device Address,Timestamp,Thumb,Index,Middle,Ring,Pinky";
                mFlexOnlyData = new FlexOnlyData();
                break;
            default:
                Log.w(TAG, "TexTronics Data did not Initialize Properly (Unknown Transmit Mode)");
                throw new IllegalArgumentException("Unknown Transmit Mode");
        }

        // Initialize Device Type Specific Data Members
        switch (mTexTronicsDevice) {
            case SMART_GLOVE:
                fileName = "glove.csv";
                break;
            case SMART_SOCKS:
                fileName = "sock.csv";
                break;
            default:
                Log.w(TAG, "TexTronics Data did not Initialize Properly (Unknown Device Type)");
                throw new IllegalArgumentException("Unknown Device Type");
        }

        // Create File to save CSV Files
        String path = dateTimeString + "_" + fileName;
        File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
        mFile = new File(parentFile, path);
    }

    public TexTronicsDevice getTexTronicsDevice() {
        return mTexTronicsDevice;
    }

    public TexTronicsMode getTexTronicsMode() throws TransmitModeException {
        return mTexTronicsMode;
    }

    public void clear() {
        switch (mTexTronicsMode) {
            case FLEX_IMU:
                mFlexImuData.clear();
                break;
            case FLEX_ONLY:
                mFlexOnlyData.clear();
            default:
                Log.w(TAG,"Error Setting Data (Wrong Transmit Mode)");
                try {
                    throw new TransmitModeException("Wrong Transmit Mode (" + mTexTronicsMode.toString() + ")");
                } catch (TransmitModeException e) {
                    Log.w(TAG, e.toString());
                }
        }
    }

    /**
     * This method allows the data to update the current data for the TexTronics device associated
     * with this class. If the previous data had not been logged it will be overwritten with
     * the given data.
     *
     * @param timestamp Timestamp provided by TexTronics Device.
     * @param thumbFlex The FlexSensor corresponding to the Thumb
     * @param indexFlex The FlexSensor corresponding to the Index Finger
     * @param middleFlex The FlexSensor corresponding to the Middle Finger
     * @param ringFlex The FlexSensor corresponding to the Ring Finger
     * @param pinkyFlex The FlexSensor corresponding to the Pinky Finger
     * @param accX The X-Axis Accelerometer Data
     * @param accY The Y-Axis Accelerometer Data
     * @param accZ The Z-Axis Accelerometer Data
     * @param gyrX The X-Axis Gyroscope Data
     * @param gyrY The Y-Axis Gyroscope Data
     * @param gyrZ The Z-Axis Gyroscope Data
     * @param magX The X-Axis Magnetometer Data
     * @param magY The Y-Axis Magnetometer Data
     * @param magZ The Z-Axis Magnetometer Data
     * @throws TransmitModeException Thrown when trying to set IMU data while not transmitting in FLEX_IMU mode.
     *
     * @since 1.0
     */
    public void setData(long timestamp, int thumbFlex, int indexFlex, int middleFlex, int ringFlex, int pinkyFlex, int accX, int accY, int accZ, int gyrX, int gyrY, int gyrZ, int magX, int magY, int magZ) throws TransmitModeException {
        if(mTexTronicsMode == TexTronicsMode.FLEX_IMU) {
            mFlexImuData.set(timestamp, thumbFlex, indexFlex, middleFlex, ringFlex, pinkyFlex, accX, accY, accZ, gyrX, gyrY, gyrZ, magX, magY, magZ);
        } else {
            Log.w(TAG,"Error Setting Data (Wrong Transmit Mode)");
            throw new TransmitModeException("Wrong Transmit Mode (" + mTexTronicsMode.toString() + ")");
        }
    }

    /**
     * This method allows the data to update the current data for the TexTronics device associated
     * with this class. If the previous data had not been logged it will be overwritten with
     * the given data.
     *
     * @param timestamp Timestamp provided by TexTronics Device.
     * @param thumbFlex The FlexSensor corresponding to the Thumb
     * @param indexFlex The FlexSensor corresponding to the Index Finger
     * @param middleFlex The FlexSensor corresponding to the Middle Finger
     * @param ringFlex The FlexSensor corresponding to the Ring Finger
     * @param pinkyFlex The FlexSensor corresponding to the Pinky Finger
     * @throws TransmitModeException Thrown when not setting IMU data while transmitting in FLEX_IMU mode.
     *
     * @since 1.0
     */
    public void setData(long timestamp, int thumbFlex, int indexFlex, int middleFlex, int ringFlex, int pinkyFlex) throws TransmitModeException {
        if(mTexTronicsMode == TexTronicsMode.FLEX_ONLY) {
            mFlexOnlyData.set(timestamp, thumbFlex, indexFlex, middleFlex, ringFlex, pinkyFlex);
        } else {
            Log.w(TAG,"Error Setting Data (Wrong Transmit Mode)");
            throw new TransmitModeException("Wrong Transmit Mode (" + mTexTronicsMode.toString() + ")");
        }
    }

    /*public void setTimestamp(long timestamp) {
        if(mTexTronicsMode == TexTronicsMode.FLEX_ONLY) {
            mFlexOnlyData.set(timestamp, thumbFlex, indexFlex, middleFlex, ringFlex, pinkyFlex);
        } else {
            Log.w(TAG,"Error Setting Data (Wrong Transmit Mode)");
            throw new TransmitModeException("Wrong Transmit Mode (" + mTexTronicsMode.toString() + ")");
        }
    }*/

    /**
     * This method logs the current data being held by this object. Once it is logged, it automatically
     * clears its current data. This method logs the data to a CSV file using the DataLog Service.
     *
     * @param context The Caller's Context.
     * @throws TransmitModeException Thrown when in an invalid transmitting mode.
     *
     * @since 1.0
     */
    public void logData(WeakReference<Context> context) throws TransmitModeException {
        if(context != null && context.get() != null) {
            String data;
            switch (mTexTronicsMode) {
                case FLEX_IMU:
                    data = mFlexImuData.toString();     // Set Data to Log
                    mFlexImuData.clear();               // Clear Data now that we've logged it
                    break;
                case FLEX_ONLY:
                    data = mFlexOnlyData.toString();
                    mFlexOnlyData.clear();
                default:
                    Log.w(TAG,"Error Setting Data (Wrong Transmit Mode)");
                    throw new TransmitModeException("Wrong Transmit Mode (" + mTexTronicsMode.toString() + ")");
            }

            DataLogService.log(context.get(), mFile, data, mHeader);
        }
    }
}
