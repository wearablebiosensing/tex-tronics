package andrewpeltier.smartglovefragments.tex_tronics.data_types;

import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;

/**
 * Created by mcons on 2/28/2018.
 */

public abstract class TexTronicsData {
    public TexTronicsData() {
    }

    public abstract void clear();

    public abstract long getTimestamp() throws IllegalDeviceType;

    public abstract void setTimestamp(long timestamp) throws IllegalDeviceType;

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

    public abstract short getAccX() throws IllegalDeviceType;

    public abstract void setAccX(short accX) throws IllegalDeviceType;

    public abstract short getAccY() throws IllegalDeviceType;

    public abstract void setAccY(short accY) throws IllegalDeviceType;

    public abstract short getAccZ() throws IllegalDeviceType;

    public abstract void setAccZ(short accZ) throws IllegalDeviceType;

    public abstract short getGyrX() throws IllegalDeviceType;

    public abstract void setGyrX(short gyrX) throws IllegalDeviceType;

    public abstract short getGyrY() throws IllegalDeviceType;

    public abstract void setGyrY(short gyrY) throws IllegalDeviceType;

    public abstract short getGyrZ() throws IllegalDeviceType;

    public abstract void setGyrZ(short gyrZ) throws IllegalDeviceType;

//    public abstract int getMagX() throws IllegalDeviceType;
//
//    public abstract void setMagX(int magX) throws IllegalDeviceType;
//
//    public abstract int getMagY() throws IllegalDeviceType;
//
//    public abstract void setMagY(int magY) throws IllegalDeviceType;
//
//    public abstract int getMagZ() throws IllegalDeviceType;
//
//    public abstract void setMagZ(int magZ) throws IllegalDeviceType;
}
