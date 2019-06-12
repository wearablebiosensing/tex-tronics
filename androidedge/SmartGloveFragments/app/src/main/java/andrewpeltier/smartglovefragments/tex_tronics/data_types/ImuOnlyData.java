package andrewpeltier.smartglovefragments.tex_tronics.data_types;

/**
 *
 * Created 4/6/19
**/

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

public class ImuOnlyData extends TexTronicsData {
        //private long mTimestamp;
        private int mAccX, mAccY, mAccZ, mGyrX, mGyrY, mGyrZ;

        public ImuOnlyData() {
            super();
            mAccX = 0;
            mAccY = 0;
            mAccZ = 0;
            mGyrX = 0;
            mGyrY = 0;
            mGyrZ = 0;
        }

        public void set(int AccX, int AccY, int AccZ, int GyrX, int GyrY, int GyrZ) {
            mAccX = AccX;
            mAccY = AccY;
            mAccZ = AccZ;
            mGyrX = GyrX;
            mGyrY = GyrY;
            mGyrZ = GyrZ;
        }

        public void clear() {
            mAccX = 0;
            mAccY = 0;
            mAccZ = 0;
            mGyrX = 0;
            mGyrY = 0;
            mGyrZ = 0;
        }

    @Override
    public long getTimestamp() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setTimestamp(long timestamp) throws IllegalDeviceType {

    }

    @Override
        public String toString() {
            return mAccX + "," + mAccY + "," + mAccZ + "," + mGyrX +
                    "," + mGyrY + "," + mGyrZ;
        }

        @Override
        public int getmAccX() { return mAccX; }

        @Override
        public void setmAccX(int AccX) { mAccX = AccX; }

        @Override
        public int getmAccY() { return mAccY; }

        @Override
        public void setmAccY(int AccY) { mAccY = AccY; }

        @Override
        public int getmAccZ() { return mAccZ; }

        @Override
        public void setmAccZ(int AccZ) { mAccZ = AccZ; }

        @Override
        public int getmGYrX() { return mGyrX; }

        @Override
        public void setmGyrX(int GyrX) { mGyrX = GyrX; }

        @Override
        public int getmGYrY() { return mGyrY; }

        @Override
        public void setmGyrY(int GyrY) { mGyrY = GyrY; }

        @Override
        public int getmGYrZ() { return mGyrZ; }

        @Override
        public void setmGyrZ(int GyrZ) { mGyrZ = GyrZ; }

        @Override
        public int getThumbFlex() throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public void setThumbFlex(int thumbFlex) throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public int getIndexFlex() throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public void setIndexFlex(int indexFlex) throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public int getMiddleFlex() throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public void setMiddleFlex(int middleFlex) throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public int getRingFlex() throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public void setRingFlex(int ringFlex) throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public int getPinkyFlex() throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

        @Override
        public void setPinkyFlex(int pinkyFlex) throws IllegalDeviceType {
            throw new IllegalDeviceType("Illegal Device Type");
        }

    @Override
    public int getAccX() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setAccX(int accX) throws IllegalDeviceType {

    }

    @Override
    public int getAccY() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setAccY(int accY) throws IllegalDeviceType {

    }

    @Override
    public int getAccZ() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setAccZ(int accZ) throws IllegalDeviceType {

    }

    @Override
    public int getGyrX() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setGyrX(int gyrX) throws IllegalDeviceType {

    }

    @Override
    public int getGyrY() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setGyrY(int gyrY) throws IllegalDeviceType {

    }

    @Override
    public int getGyrZ() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setGyrZ(int gyrZ) throws IllegalDeviceType {

    }

    @Override
    public int getMagX() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setMagX(int magX) throws IllegalDeviceType {

    }

    @Override
    public int getMagY() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setMagY(int magY) throws IllegalDeviceType {

    }

    @Override
    public int getMagZ() throws IllegalDeviceType {
        return 0;
    }

    @Override
    public void setMagZ(int magZ) throws IllegalDeviceType {

    }


}

