package edu.uri.wbl.tex_tronics.smartglove.ui;

/**
 * Created by mcons on 2/28/2018.
 */

public class BleDeviceModel {
    private String mDeviceAddress;
    private String mDeviceName;

    public BleDeviceModel(String deviceAddress, String deviceName) {
        mDeviceAddress = deviceAddress;
        mDeviceName = deviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public String getDeviceName() {
        return mDeviceName;
    }
}
