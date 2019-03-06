package andrewpeltier.smartglovefragments.tex_tronics.data_types;

/**
 * Created by mcons on 2/28/2018.
 */

public class FlexImuData extends TexTronicsData {
    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mMiddleFlex, mRingFlex, mPinkyFlex;
    private int mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ, mMagX, mMagY, mMagZ;

    public FlexImuData() {
        super();
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;
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

    public void set(FlexImuData data) {
        mTimestamp = data.getTimestamp();
        mThumbFlex = data.getThumbFlex();
        mIndexFlex = data.getIndexFlex();
        mMiddleFlex = data.getMiddleFlex();
        mRingFlex = data.getRingFlex();
        mPinkyFlex = data.getPinkyFlex();
        mAccX = data.getAccX();
        mAccY = data.getAccY();
        mAccZ = data.getAccZ();
        mGyrX = data.getGyrX();
        mGyrY = data.getGyrY();
        mGyrZ = data.getGyrZ();
        mMagX = data.getMagX();
        mMagY = data.getMagY();
        mMagZ = data.getMagZ();
    }

    public void clear() {
        mTimestamp = 0;
        mThumbFlex = 0;
        mIndexFlex = 0;
        mMiddleFlex = 0;
        mRingFlex = 0;
        mPinkyFlex = 0;
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

    @Override
    public String toString() {
        return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex +
                "," + mRingFlex + "," + mPinkyFlex + "," + mAccX + "," + mAccY + "," + mAccZ +
                "," + mGyrX + "," + mGyrY + "," + mGyrZ + "," + mMagX + "," + mMagY + "," + mMagZ;
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
