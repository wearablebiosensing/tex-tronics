package andrewpeltier.smartglovefragments.tex_tronics.enums;

import android.util.Log;

/** ======================================
 *
 *           ExerciseMode Enum
 *
 *  ======================================
 *
 *  Enum that returns what mode of collection is used for the particular
 *  exercise, being Flex IMU or Flex Only.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public enum ExerciseMode
{
    /**
     * Collects data from flex sensors along with IMU data
     */
    FLEX_IMU ("Flex + IMU"),
    /**
     * Only collects data from flex sensors
     */
    FLEX_ONLY ("Flex Only"),

    IMU_ONLY ("Imu Only");

    private final String mExercise;

    ExerciseMode(String exercise)
    {
        mExercise = exercise;
    }

    public static ExerciseMode getExercise(String exercise) {
        switch (exercise) {
            case "Flex + IMU":
                Log.d("Demo", "Retruning FLEX IMU");
                return FLEX_IMU;
            case "Flex Only":
                Log.d("Demo", "Retruning FLEX ONLY");
                return FLEX_ONLY;
            case "Imu Only":
                Log.d("Demo", "Retruning IMU ONLY");
                return IMU_ONLY;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mExercise;
    }
}
