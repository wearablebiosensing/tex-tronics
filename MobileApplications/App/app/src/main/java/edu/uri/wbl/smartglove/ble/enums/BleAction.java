package edu.uri.wbl.smartglove.ble.enums;

import android.app.Application;
import android.content.Context;

import edu.uri.wbl.smartglove.R;

/**
 * Created by mcons on 11/12/2017.
 */

public enum BleAction {
    START("wbl.textronics.ble.action.start"),
    STOP("wbl.textronics.ble.action.stop"),
    CONNECT("wbl.textronics.ble.action.connect"),
    DISCONNECT("wbl.textronics.ble.action.disconnect"),
    DISCOVER_SERVICES("wbl.textronics.ble.action.discover_services"),
    REQUEST_READ("wbl.textronics.ble.action.request_read"),
    REQUEST_WRITE("wbl.textronics.ble.action.request_write"),
    ENABLE_NOTIFICATION("wbl.textronics.ble.action.enable_notification"),
    DISABLE_NOTIFICATION("wbl.textronics.ble.action.disable_notification");

    public static BleAction GET(String action) {
        switch (action) {
            case "wbl.textronics.ble.action.start":
                return START;
            case "wbl.textronics.ble.action.stop":
                return STOP;
            case "wbl.textronics.ble.action.connect":
                return CONNECT;
            case "wbl.textronics.ble.action.disconnect":
                return DISCONNECT;
            case "wbl.textronics.ble.action.discover_services":
                return DISCOVER_SERVICES;
            case "wbl.textronics.ble.action.request_read":
                return REQUEST_READ;
            case "wbl.textronics.ble.action.request_write":
                return REQUEST_WRITE;
            case "wbl.textronics.ble.action.enable_notification":
                return ENABLE_NOTIFICATION;
            case "wbl.textronics.ble.action.disable_notification":
                return DISABLE_NOTIFICATION;
            default:
                return null;
        }
    }

    private String mAction;

    BleAction(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }
}
