package edu.uri.wbl.smartglove.ble.enums;

/**
 * Created by mcons on 11/12/2017.
 */

public enum BleActions {
    START("wbl.text_tronics.start"),
    STOP("wbl.text_tronics.stop"),
    CONNECT("wbl.tex_tronics.connect"),
    DISCONNECT("wbl.tex_tronics.disconnect"),
    DISCOVER_SERVICES("wbl.tex_tronics.discover_services"),
    READ_CHARACTERISTIC("wbl.tex_tronics.read_characteristic");

    public static BleActions GET(String action) {
        switch (action) {
            case "wbl.text_tronics.start":
                return START;
            case "wbl.text_tronics.stop":
                return STOP;
            case "wbl.tex_tronics.connect":
                return CONNECT;
            case "wbl.tex_tronics.disconnect":
                return DISCONNECT;
            case "wbl.tex_tronics.discover_services":
                return DISCOVER_SERVICES;
            case "wbl.tex_tronics.read_characteristic":
                return READ_CHARACTERISTIC;
            default:
                return null;
        }
    }

    private String mAction;

    BleActions(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }
}
