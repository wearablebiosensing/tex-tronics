package andrewpeltier.smartglovefragments.mqtt;

/** ======================================
 *
 *          JsonData Class
 *
 *  ======================================
 *
 *  Takes as parameters the data required to create a json file and uses this to publish the data
 *  from an exercise to the MQTT server.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public class JsonData
{
    private String mDate, mSensorId, mChoiceId, mExerciseID, mRoutineID, mData;

    public JsonData(String date, String sensorId, String choiceID, String exerciseID, String routrineID, String data) {
        mDate = date;
        mSensorId = sensorId;
        mChoiceId = choiceID;
        mExerciseID = exerciseID;
        mRoutineID = routrineID;
        mData = data;
    }

    @Override
    public String toString()
    {
        return "{\"Date\": \"" + mDate + "\", " +
                "\"Sensor_ID\": \"" + mSensorId + "\", " +
                "\"Exercise_ID\": \"" + mChoiceId + "\", " +
                "\"Exercise_UUID\": \"" + mExerciseID + "\", " +
                "\"Routine_UUID\": \"" + mRoutineID + "\", " +
                "\"Data\": " + "\"" + mData + "\"" + "}";
    }
}