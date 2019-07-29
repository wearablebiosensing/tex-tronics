package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

public class FlexImuData extends TexTronicsData {
    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mRingFlex;// mPinkyFlex;
    private int mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ;  //, mMagX, mMagY, mMagZ;

    public FlexImuData() {
        super();
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        //mMiddleFlex = 0;
        mRingFlex = 0;
        //mPinkyFlex = 0;
        mAccX = 0;
        mAccY = 0;
        mAccZ = 0;
        mGyrX = 0;
        mGyrY = 0;
        mGyrZ = 0;
    }

    public void set(long timestamp, int AccX, int AccY, int AccZ, int GyrX, int GyrY, int GyrZ, int thumbFlex, int indexFlex, int ringFlex) {
        mTimestamp = timestamp;
        mAccX = AccX;
        mAccY = AccY;
        mAccY = AccZ;
        mGyrX = GyrX;
        mGyrY = GyrY;
        mGyrZ = GyrZ;
        mThumbFlex = thumbFlex;
        mIndexFlex = indexFlex;
        //mMiddleFlex = middleFlex;
        mRingFlex = ringFlex;
        //mPinkyFlex = pinkyFlex;
        }

    public void clear() {
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        //mMiddleFlex = 0;
        mRingFlex = 0;
        //mPinkyFlex = 0;
        mAccX = 0;
        mAccY = 0;
        mAccZ = 0;
        mGyrX = 0;
        mGyrY = 0;
        mGyrZ = 0;
//        mMagX = 0;
//        mMagY = 0;
//        mMagZ = 0;
    }

    @Override
    public String toString() {
        return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mRingFlex +
                "," + mAccX + "," + mAccY + "," + mAccZ +
                "," + mGyrX + "," + mGyrY + "," + mGyrZ;
    }

    @Override
    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

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
    public int getThumbFlex() {
        return mThumbFlex;
    }

    @Override
    public void setThumbFlex(int thumbFlex) {
        mThumbFlex = thumbFlex;
    }

    @Override
    public int getIndexFlex() {
        return mIndexFlex;
    }

    @Override
    public void setIndexFlex(int indexFlex) {
        mIndexFlex = indexFlex;
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
    public int getRingFlex() {
        return mRingFlex;
    }

    @Override
    public void setRingFlex(int RingFlex) {
        mRingFlex = RingFlex;
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