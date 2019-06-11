package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

/** ======================================
 *
 *     TexTronicsData Abstract Class
 *
 *  ======================================
 *
 *  Abstract class meant to retrieve and format the data we get from each device. This
 *  is used by both the FlexIMU and FlexOnly device types.
 *
 *  The data is formatted so that it can be stored in a CSV file. The only difference between the child
 *  classes is the data being recorded.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public abstract class TexTronicsData {
    public TexTronicsData() {

    }

    public abstract void clear();

    public abstract long getTimestamp() throws IllegalDeviceType;

    public abstract void setTimestamp(long timestamp) throws IllegalDeviceType;

    public abstract int getmAccX();

    public abstract void setmAccX(int AccX);

    public abstract int getmAccY();

    public abstract void setmAccY(int AccY);

    public abstract int getmAccZ();

    public abstract void setmAccZ(int AccZ);

    public abstract int getmGYrX();

    public abstract void setmGyrX(int GyrX);

    public abstract int getmGYrY();

    public abstract void setmGyrY(int GyrY);

    public abstract int getmGYrZ();

    public abstract void setmGyrZ(int GyrZ);

    public abstract int getThumbFlex() throws IllegalDeviceType;

    public abstract void setThumbFlex(int thumbFlex) throws IllegalDeviceType;

    public abstract int getIndexFlex() throws IllegalDeviceType;

    public abstract void setIndexFlex(int indexFlex) throws IllegalDeviceType;

    public abstract int getMiddleFlex() throws IllegalDeviceType;

    public abstract void setMiddleFlex(int middleFlex) throws IllegalDeviceType;

    public abstract int getRingFlex() throws IllegalDeviceType;

    public abstract void setRingFlex(int ringFlex) throws IllegalDeviceType;

    public abstract int getPinkyFlex() throws IllegalDeviceType;

    public abstract void setPinkyFlex(int pinkyFlex) throws IllegalDeviceType;

    public abstract int getAccX() throws IllegalDeviceType;

    public abstract void setAccX(int accX) throws IllegalDeviceType;

    public abstract int getAccY() throws IllegalDeviceType;

    public abstract void setAccY(int accY) throws IllegalDeviceType;

    public abstract int getAccZ() throws IllegalDeviceType;

    public abstract void setAccZ(int accZ) throws IllegalDeviceType;

    public abstract int getGyrX() throws IllegalDeviceType;

    public abstract void setGyrX(int gyrX) throws IllegalDeviceType;

    public abstract int getGyrY() throws IllegalDeviceType;

    public abstract void setGyrY(int gyrY) throws IllegalDeviceType;

    public abstract int getGyrZ() throws IllegalDeviceType;

    public abstract void setGyrZ(int gyrZ) throws IllegalDeviceType;

    public abstract int getMagX() throws IllegalDeviceType;

    public abstract void setMagX(int magX) throws IllegalDeviceType;

    public abstract int getMagY() throws IllegalDeviceType;

    public abstract void setMagY(int magY) throws IllegalDeviceType;

    public abstract int getMagZ() throws IllegalDeviceType;

    public abstract void setMagZ(int magZ) throws IllegalDeviceType;
}
