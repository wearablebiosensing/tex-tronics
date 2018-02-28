package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

/**
 * Created by mcons on 2/27/2018.
 */

public class FlexOnlyData {
    private long mTimestamp;
    private int mThumbFlex, mIndexFlex, mMiddleFlex, mRingFlex, mPinkyFlex;

    public FlexOnlyData() {
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
        return mTimestamp + "," + mThumbFlex + "," + mIndexFlex + "," + mMiddleFlex +
                "," + mRingFlex + "," + mPinkyFlex;
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
}
