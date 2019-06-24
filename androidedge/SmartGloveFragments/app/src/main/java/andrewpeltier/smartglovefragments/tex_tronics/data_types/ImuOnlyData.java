package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by Adityaraj on 6/21/2019.
 */

public class ImuOnlyData extends TexTronicsData {
    //private long mTimestamp;
    private int mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ;

    public ImuOnlyData() {
        super();
        //mTimestamp = 0;
        mAccX = 0;
        mAccY = 0;
        mAccY = 0;
        mGyrX = 0;
        mGyrY = 0;
        mGyrZ = 0;
    }

    public void set(long timestamp, int AccX, int AccY, int AccZ, int GyrX, int GyrY, int GyrZ) {
        //mTimestamp = timestamp;
        mAccX = AccX;
        mAccY = AccY;
        mAccY = AccZ;
        mGyrX = GyrX;
        mGyrY = GyrY;
        mGyrZ = GyrZ;
    }

    public void clear() {
        //mTimestamp = 0;
        mAccX = 0;
        mAccY = 0;
        mAccY = 0;
        mGyrX = 0;
        mGyrY = 0;
        mGyrZ = 0;
    }

    @Override
    public String toString() {
        return mAccX + "," + mAccY + "," + mAccZ +
                "," + mGyrX + "," + mGyrY + "," + mGyrZ;
    }

//    @Override
//    public long getTimestamp() {
//        return mTimestamp;
//    }
//
//    @Override
//    public void setTimestamp(long timestamp) {
//        mTimestamp = timestamp;
//    }

    @Override
    public int getAccX() {
        return mAccX;
    }

    @Override
    public void setAccX(int AccX) {
        mAccX = AccX;
    }

    @Override
    public int getAccY() {
        return mAccY;
    }

    @Override
    public void setAccY(int AccY) {
        mAccY = AccY;
    }

    @Override
    public int getAccZ() {
        return mAccZ;
    }

    @Override
    public void setAccZ(int AccZ) {
        mAccZ = AccZ;
    }

    @Override
    public int getGyrX() {
        return mGyrX;
    }

    @Override
    public void setGyrX(int GyrX) {
        mGyrX = GyrX;
    }

    @Override
    public int getGyrY() {
        return mGyrY;
    }

    @Override
    public void setGyrY(int GyrY) {
        mGyrY = GyrY;
    }

    @Override
    public int getGyrZ() {
        return mGyrZ;
    }

    @Override
    public void setGyrZ(int GyrZ) {
        mGyrZ = GyrZ;
    }

    @Override
    public int getThumbFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setThumbFlex(int ThumbFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public int getIndexFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setIndexFlex(int IndexFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public int getMiddleFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setMiddleFlex(int MiddleFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public int getRingFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setRingFlex(int RingFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public int getPinkyFlex() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setPinkyFlex(int PinkyFlex) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }
}
