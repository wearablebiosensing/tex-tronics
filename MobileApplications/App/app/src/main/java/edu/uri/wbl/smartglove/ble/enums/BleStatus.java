package edu.uri.wbl.smartglove.ble.enums;

/**
 * Created by mcons on 11/12/2017.
 */

public enum BleStatus {
    INITIALIZING("Initializing"),
    CONNECTED("Connected"),
    DISCONNECTED("Disconnected");

    private String mStatus;

    BleStatus(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }
}
