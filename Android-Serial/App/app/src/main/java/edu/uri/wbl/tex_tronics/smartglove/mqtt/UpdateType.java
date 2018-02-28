package edu.uri.wbl.tex_tronics.smartglove.mqtt;

import edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums.Action;

/**
 * Created by mcons on 2/28/2018.
 */

public enum UpdateType {
    connected ("uri.wbl.tex_tronics.mqtt.connected"),
    disconnected ("uri.wbl.tex_tronics.mqtt.disconnected");

    private final String mUpdate;

    private UpdateType(String update) {
        mUpdate = update;
    }

    public static UpdateType getUpdate(String update) {
        switch (update) {
            case "uri.wbl.tex_tronics.ble.connect":
                return connected;
            case "uri.wbl.tex_tronics.ble.disconnect":
                return disconnected;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mUpdate;
    }
}
