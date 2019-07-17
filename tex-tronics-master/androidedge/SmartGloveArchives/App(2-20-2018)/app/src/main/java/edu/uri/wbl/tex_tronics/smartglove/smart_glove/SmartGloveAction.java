package edu.uri.wbl.tex_tronics.smartglove.smart_glove;

/**
 * Created by mcons on 2/8/2018.
 */

public enum SmartGloveAction {
    connect ("uri.wbl.tex_tronics.ble.connect"),
    disconnect ("uri.wbl.tex_tronics.ble.disconnect"),
    scan ("uri.wbl.tex_tronics.ble.scan");

    private final String mAction;

    private SmartGloveAction(String action) {
        mAction = action;
    }

    public static SmartGloveAction getAction(String action) {
        switch (action) {
            case "uri.wbl.tex_tronics.ble.connect":
                return connect;
            case "uri.wbl.tex_tronics.ble.disconnect":
                return disconnect;
            case "uri.wbl.tex_tronics.ble.scan":
                return scan;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mAction;
    }
}
