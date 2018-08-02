package edu.uri.wbl.tex_tronics.smartglove.mqtt;

/**
 * Created by mcons on 2/28/2018.
 */

public class JsonData {
    private String mDate, mSensorId, mExerciseId, mData;

    public JsonData(String date, String sensorId, String exerciseId, String data) {
        mDate = date;
        mSensorId = sensorId;
        mExerciseId = exerciseId;
        mData = data;
    }

    @Override
    public String toString() {
        return "{\"Date\": \"" + mDate + "\", \"Sensor_ID\": \"" + mSensorId + "\", \"Exercise_ID\": \"" + mExerciseId + "\", \"Data\": " + "\"" + mData + "\"" + "}";
    }
}