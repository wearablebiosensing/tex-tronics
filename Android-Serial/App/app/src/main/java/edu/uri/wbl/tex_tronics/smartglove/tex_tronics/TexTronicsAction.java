package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

/**
 * Created by mcons on 2/28/2018.
 */

public enum TexTronicsAction {
    connect ("uri.wbl.tex_tronics.ble.connect"),
    publish ("uri.wbl.tex_tronics.tex_tronics.publish"),
    disconnect ("uri.wbl.tex_tronics.ble.disconnect"),
    start ("uri.wbl.tex_tronics.ble.start"),
    stop ("uri.wbl.tex_tronics.ble.stop");

    private final String mAction;

    private TexTronicsAction(String action) {
        mAction = action;
    }

    public static TexTronicsAction getAction(String action) {
        switch (action) {
            case "uri.wbl.tex_tronics.ble.connect":
                return connect;
            case "uri.wbl.tex_tronics.tex_tronics.publish":
                return publish;
            case "uri.wbl.tex_tronics.ble.disconnect":
                return disconnect;
            case "uri.wbl.tex_tronics.ble.start":
                return start;
            case "uri.wbl.tex_tronics.ble.stop":
                return stop;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mAction;
    }
}
