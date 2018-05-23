package edu.uri.wbl.tex_tronics.smartglove.mqtt;

/**
 * Created by mcons on 2/28/2018.
 */

public class JsonData {
    private String mDate, mSensorId, mData;

    public JsonData(String date, String sensorId, String data) {
        mDate = date;
        mSensorId = sensorId;
        mData = data;
    }

    @Override
    public String toString() {
        return "{\"Date\": \"" + mDate + "\", \"Sensor_ID\": \"" + mSensorId + "\", \"Data\": " + mData + "}";
    }
}
