package andrewpeltier.smartglovefragments.mqtt;

/** ======================================
 *
 *             UpdateType Enum
 *
 *  ======================================
 *
 * Holds the types of updates we can receive from the MQTT server, which in this case
 * are just "connected" and "disconnected" updates.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public enum UpdateType
{
    /**
     * Our MQTT-Android client has successfully established a connection
     */
    connected ("uri.wbl.tex_tronics.mqtt.connected"),
    /**
     * Our MQTT-Android client has disconnected our Android device from the MQTT server
     */
    disconnected ("uri.wbl.tex_tronics.mqtt.disconnected");

    private final String mUpdate;

    /**
     * Constructor just sets the update as a String
     *
     * @param update            -Type of update, either connected or disconnected
     */
    private UpdateType(String update) {
        mUpdate = update;
    }

    /** getUpdate()
     *
     * Returns the update in the form of UpdateType
     *
     * @param update
     * @return
     */
    public static UpdateType getUpdate(String update)
    {
        switch (update)
        {
            case "uri.wbl.tex_tronics.mqtt.connected":
                return connected;
            case "uri.wbl.tex_tronics.mqtt.disconnected":
                return disconnected;
            default:
                return null;
        }
    }

    /** toString()
     *
     * Returns the update set in the constructor in the form of a string.
     *
     * @return
     */
    public String toString() {
        return this.mUpdate;
    }
}
