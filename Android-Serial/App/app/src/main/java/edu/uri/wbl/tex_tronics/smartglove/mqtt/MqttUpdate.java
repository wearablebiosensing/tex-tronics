package edu.uri.wbl.tex_tronics.smartglove.mqtt;

/**
 * Created by mcons on 2/28/2018.
 */

public enum MqttUpdate {
    started ("uri.wbl.tex_tronics.mqtt.started"),
    connected ("uri.wbl.tex_tronics.mqtt.connected"),
    published ("uri.wbl.tex_tronics.mqtt.published"),
    disconnected ("uri.wbl.tex_tronics.mqtt.disconnected"),
    stopped ("uri.wbl.tex_tronics.mqtt.stopped");

    private final String mUpdate;

    private MqttUpdate(String update) {
        mUpdate = update;
    }

    public static MqttUpdate getUpdate(String update) {
        switch (update) {
            case "uri.wbl.tex_tronics.mqtt.started":
                return started;
            case "uri.wbl.tex_tronics.mqtt.connected":
                return connected;
            case "uri.wbl.tex_tronics.mqtt.published":
                return published;
            case "uri.wbl.tex_tronics.mqtt.disconnected":
                return disconnected;
            case "uri.wbl.tex_tronics.mqtt.stopped":
                return stopped;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mUpdate;
    }
}
