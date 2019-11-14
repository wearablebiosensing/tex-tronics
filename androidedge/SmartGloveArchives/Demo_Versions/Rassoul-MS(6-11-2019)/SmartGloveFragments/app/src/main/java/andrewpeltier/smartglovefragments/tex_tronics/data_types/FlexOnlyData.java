package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by mcons on 2/28/2018.
 */

public class FlexOnlyData extends TexTronicsData {
    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mMiddleFlex, mRingFlex, mPinkyFlex;

    public FlexOnlyData() {
        super();
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;
    }

    public void set(long timestamp, int thumbFlex, int indexFlex, int middleFlex, int ringFlex, int pinkyFlex) {
        mTimestamp = timestamp;
        mThumbFlex = thumbFlex;
        mIndexFlex = indexFlex;
        mMiddleFlex = middleFlex;
        mRingFlex = ringFlex;
        mPinkyFlex = pinkyFlex;
    }

    public void clear() {
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;
    }

    @Override
    public String toString() {
        return mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex +
                "," + mRingFlex + "," + mPinkyFlex;
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
    public void setRingFlex(int ringFlex) {
        mRingFlex = ringFlex;
    }

    @Override
    public int getPinkyFlex() {
        return mPinkyFlex;
    }

    @Override
    public void setPinkyFlex(int pinkyFlex) {
        mPinkyFlex = pinkyFlex;
    }

    @Override
    public short getAccX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setAccX(short accX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public short getAccY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setAccY(short accY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public short getAccZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setAccZ(short accZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public short getGyrX() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setGyrX(short gyrX) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public short getGyrY() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setGyrY(short gyrY) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public short getGyrZ() throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

    @Override
    public void setGyrZ(short gyrZ) throws IllegalDeviceType {
        throw new IllegalDeviceType("Illegal Device Type");
    }

//    @Override
//    public int getMagX() throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
//
//    @Override
//    public void setMagX(int magX) throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
//
//    @Override
//    public int getMagY() throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
//
//    @Override
//    public void setMagY(int magY) throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
//
//    @Override
//    public int getMagZ() throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
//
//    @Override
//    public void setMagZ(int magZ) throws IllegalDeviceType {
//        throw new IllegalDeviceType("Illegal Device Type");
//    }
}
