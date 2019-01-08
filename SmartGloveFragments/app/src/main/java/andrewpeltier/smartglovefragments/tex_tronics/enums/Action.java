package andrewpeltier.smartglovefragments.tex_tronics.enums;

/** ======================================
 *
 *              Action Enum
 *
 *  ======================================
 *
 *  Enum that contains several BLE and MQTT actions.
 *
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public enum Action
{
    /**
     * BLE action to connect to a device
     */
    connect ("uri.wbl.tex_tronics.ble.connect"),
    /**
     * BLE action to disconnect from a device
     */
    disconnect ("uri.wbl.tex_tronics.ble.disconnect"),
    /**
     * MQTT action to publish data from exercise to our server
     */
    publish("uri.wbl.tex_tronics.ble.publish"),
    /**
     * No apparent uses as of right now
     */
    start ("uri.wbl.tex_tronics.ble.start"),
    /**
     * Stops the TexTronics Manager service / disconnects all devices
     */
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
