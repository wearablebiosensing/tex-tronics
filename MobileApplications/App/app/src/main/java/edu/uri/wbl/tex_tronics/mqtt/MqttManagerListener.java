package edu.uri.wbl.tex_tronics.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @see <a href="https://github.com/eclipse/paho.mqtt.android">Eclipse Paho MQTT</a>
 */

public interface MqttManagerListener {
    void onMqttConnected();

    void onMqttDisconnected();

    void onMqttMessageArrived(String topic, MqttMessage mqttMessage);
}