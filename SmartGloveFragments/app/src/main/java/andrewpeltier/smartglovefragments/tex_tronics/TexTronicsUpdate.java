package andrewpeltier.smartglovefragments.tex_tronics;

/**
 * Created by mcons on 2/28/2018.
 */

public enum TexTronicsUpdate
{
    started ("uri.wbl.tex_tronics.started"),
    ble_connected ("uri.wbl.tex_tronics.ble_connected"),
    ble_disconnected ("uri.wbl.tex_tronics.ble_disconnected"),
    ble_connecting ("uri.wbl.tex_tronics.ble_connecting"),
    ble_disconnecting ("uri.wbl.tex_tronics.ble_disconnecting"),
    mqtt_connected ("uri.wbl.tex_tronics.mqtt_connected"),
    mqtt_disconnected ("uri.wbl.tex_tronics.mqtt_disconnected");

    private static final String STARTED = "uri.wbl.tex_tronics.started";
    private static final String BLE_CONNECTED = "uri.wbl.tex_tronics.ble_connected";
    private static final String BLE_DISCONNECTED = "uri.wbl.tex_tronics.ble_disconnected";
    private static final String BLE_CONNECTING = "uri.wbl.tex_tronics.ble_connecting";
    private static final String BLE_DISCONNECTING = "uri.wbl.tex_tronics.ble_disconnecting";
    private static final String MQTT_CONNECTED = "uri.wbl.tex_tronics.mqtt_connected";
    private static final String MQTT_DISCONNECTED = "uri.wbl.tex_tronics.mqtt_disconnected";

    private final String mUpdate;

    private TexTronicsUpdate(String update) {
        mUpdate = update;
    }

    public static TexTronicsUpdate getUpdate(String update) {
        switch (update) {
            case STARTED:
                return started;
            case BLE_CONNECTED:
                return ble_connected;
            case BLE_DISCONNECTED:
                return ble_disconnected;
            case BLE_CONNECTING:
                return ble_connecting;
            case BLE_DISCONNECTING:
                return ble_disconnecting;
            case MQTT_CONNECTED:
                return mqtt_connected;
            case MQTT_DISCONNECTED:
                return mqtt_disconnected;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mUpdate;
    }
}
