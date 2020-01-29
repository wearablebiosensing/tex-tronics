package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

public class FlexImuData extends TexTronicsData {
    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mMiddleFlex, mRingFlex;// mPinkyFlex;
    private short mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ;  //, mMagX, mMagY, mMagZ;

    public FlexImuData() {
        super();
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        //mPinkyFlex = 0;
        mAccX = 0;
        mAccY = 0;
        mAccZ = 0;
        mGyrX = 0;
        mGyrY = 0;
        mGyrZ = 0;
    }

    public void set(long timestamp, short AccX, short AccY, short AccZ, short GyrX, short GyrY, short GyrZ, int thumbFlex, int indexFlex, int middleFlex, int ringFlex) {
        mTimestamp = timestamp;
        mAccX = AccX;
        mAccY = AccY;
        mAccY = AccZ;
        mGyrX = GyrX;
        mGyrY = GyrY;
        mGyrZ = GyrZ;
        mThumbFlex = thumbFlex;
        mIndexFlex = indexFlex;
        mMiddleFlex = middleFlex;
        mRingFlex = ringFlex;
        //mPinkyFlex = pinkyFlex;
        }

    public void clear() {
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
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
        return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex + "," + mRingFlex +
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
    public short getAccX() {
        return mAccX;
    }

    @Override
    public void setAccX(short AccX) {
        mAccX = AccX;
    }

    @Override
    public short getAccY() {
        return mAccY;
    }

    @Override
    public void setAccY(short AccY) {
        mAccY = AccY;
    }

    @Override
    public short getAccZ() {
        return mAccZ;
    }

    @Override
    public void setAccZ(short AccZ) {
        mAccZ = AccZ;
    }

    @Override
    public short getGyrX() {
        return mGyrX;
    }

    @Override
    public void setGyrX(short GyrX) {
        mGyrX = GyrX;
    }

    @Override
    public short getGyrY() {
        return mGyrY;
    }

    @Override
    public void setGyrY(short GyrY) {
        mGyrY = GyrY;
    }

    @Override
    public short getGyrZ() {
        return mGyrZ;
    }

    @Override
    public void setGyrZ(short GyrZ) {
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
    public int getMiddleFlex() {
        return mMiddleFlex;
    }

    @Override
    public void setMiddleFlex(int middleFlex) {
        mMiddleFlex = middleFlex;
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