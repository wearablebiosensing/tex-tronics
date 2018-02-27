package edu.uri.wbl.tex_tronics.smartglove.smart_glove;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;

/**
 * Created by mcons on 2/12/2018.
 */

public class SmartGloveData {
    private final SmartGloveMode mSmartGloveMode;
    private final TexTronicsDevice mTexTronicsDevice;
    private final String mHeader;
    private File mFile;

    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mMiddleFlex, mRingFlex, mPinkyFlex;
    private int mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ, mMagX, mMagY, mMagZ;


    public SmartGloveData(@NonNull String deviceAddress, TexTronicsDevice texTronicsDevice, @NonNull SmartGloveMode mode) {
        // Set the SmartGlove's Transmission Mode (This cannot be changed)
        mSmartGloveMode = mode;
        mTexTronicsDevice = texTronicsDevice;

        // Initialize timestamp, save location, and analog data
        Date date = Calendar.getInstance().getTime();
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);
        String dateTimeString = dateString + "/" + timeString;

        String fileName;
        switch (mTexTronicsDevice) {
            case SMART_GLOVE:
                fileName = "glove.csv";
                break;
            case SMART_SOCK:
                fileName = "sock.csv";
                break;
            default:
                fileName = "unknown.csv";
                break;
        }

        String path = dateTimeString + "_" + fileName;

        File parentFile = new File("/storage/emulated/0/Documents");
        mFile = new File(parentFile, path);

        if(mSmartGloveMode == SmartGloveMode.FLEX_IMU) {
            mHeader = "Device Address,Timestamp,Thumb,Index,Middle,Ring,Pinky,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z),Mag(x),Mag(y),Mag(z)";
        } else {
            mHeader = "Device Address,Timestamp,Thumb,Index,Middle,Ring,Pinky";
        }

        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;

        if(mode == SmartGloveMode.FLEX_IMU) {
            // If configured for FLEX+IMU mode, Initialize IMU data
            mAccX = 0;
            mAccY = 0;
            mAccZ = 0;
            mGyrX = 0;
            mGyrY = 0;
            mGyrZ = 0;
            mMagX = 0;
            mMagY = 0;
            mMagZ = 0;
        }
    }

    public void log(Context context) {
        DataLogService.log(context, mFile, this.toString(), mHeader);
    }

    public void clear() {
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;

        if(mSmartGloveMode == SmartGloveMode.FLEX_IMU) {
            // If configured for FLEX+IMU mode, Initialize IMU data
            mAccX = 0;
            mAccY = 0;
            mAccZ = 0;
            mGyrX = 0;
            mGyrY = 0;
            mGyrZ = 0;
            mMagX = 0;
            mMagY = 0;
            mMagZ = 0;
        }
    }

    public String getHeader() {
        return mHeader;
    }

    public SmartGloveMode getSmartGloveMode() {
        return mSmartGloveMode;
    }

    public TexTronicsDevice getTexTronicsDevice() {
        return mTexTronicsDevice;
    }

    @Override
    public String toString() {
        if(mSmartGloveMode == SmartGloveMode.FLEX_ONLY) {
            return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex +
                    "," + mRingFlex + "," + mPinkyFlex;
        } else {
            return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex +
                    "," + mRingFlex + "," + mPinkyFlex + "," + mAccX + "," + mAccY + "," + mAccZ +
                    "," + mGyrX + "," + mGyrY + "," + mGyrZ + "," + mMagX + "," + mMagY + "," + mMagZ;
        }
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public int getThumbFlex() {
        return mThumbFlex;
    }

    public void setThumbFlex(int thumbFlex) {
        mThumbFlex = thumbFlex;
    }

    public int getIndexFlex() {
        return mIndexFlex;
    }

    public void setIndexFlex(int indexFlex) {
        mIndexFlex = indexFlex;
    }

    public int getMiddleFlex() {
        return mMiddleFlex;
    }

    public void setMiddleFlex(int middleFlex) {
        mMiddleFlex = middleFlex;
    }

    public int getRingFlex() {
        return mRingFlex;
    }

    public void setRingFlex(int ringFlex) {
        mRingFlex = ringFlex;
    }

    public int getPinkyFlex() {
        return mPinkyFlex;
    }

    public void setPinkyFlex(int pinkyFlex) {
        mPinkyFlex = pinkyFlex;
    }

    public int getAccX() {
        return mAccX;
    }

    public void setAccX(int accX) {
        mAccX = accX;
    }

    public int getAccY() {
        return mAccY;
    }

    public void setAccY(int accY) {
        mAccY = accY;
    }

    public int getAccZ() {
        return mAccZ;
    }

    public void setAccZ(int accZ) {
        mAccZ = accZ;
    }

    public int getGyrX() {
        return mGyrX;
    }

    public void setGyrX(int gyrX) {
        mGyrX = gyrX;
    }

    public int getGyrY() {
        return mGyrY;
    }

    public void setGyrY(int gyrY) {
        mGyrY = gyrY;
    }

    public int getGyrZ() {
        return mGyrZ;
    }

    public void setGyrZ(int gyrZ) {
        mGyrZ = gyrZ;
    }

    public int getMagX() {
        return mMagX;
    }

    public void setMagX(int magX) {
        mMagX = magX;
    }

    public int getMagY() {
        return mMagY;
    }

    public void setMagY(int magY) {
        mMagY = magY;
    }

    public int getMagZ() {
        return mMagZ;
    }

    public void setMagZ(int magZ) {
        mMagZ = magZ;
    }
}
