package andrewpeltier.smarttrousers.tex_tronics.enums;

/**
 * Created by mcons on 2/28/2018.
 */

public enum Action {
    connect ("uri.wbl.tex_tronics.ble.connect"),
    disconnect ("uri.wbl.tex_tronics.ble.disconnect"),
    publish("uri.wbl.tex_tronics.ble.publish"),
    start ("uri.wbl.tex_tronics.ble.start"),
    stop ("uri.wbl.tex_tronics.ble.stop");

    private final String mAction;

    private Action(String action) {
        mAction = action;
    }

    public static Action getAction(String action) {
        switch (action) {
            case "uri.wbl.tex_tronics.ble.connect":
                return connect;
            case "uri.wbl.tex_tronics.ble.disconnect":
                return disconnect;
            case "uri.wbl.tex_tronics.ble.publish":
                return publish;
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
