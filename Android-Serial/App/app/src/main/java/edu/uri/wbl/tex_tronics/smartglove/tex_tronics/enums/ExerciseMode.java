package edu.uri.wbl.tex_tronics.smartglove.tex_tronics.enums;

/**
 * Created by mcons on 2/28/2018.
 */

public enum ExerciseMode {
    FLEX_IMU ("Flex + IMU"),
    FLEX_ONLY ("Flex Only");

    private final String mExercise;

    private ExerciseMode(String exercise) {
        mExercise = exercise;
    }

    public static ExerciseMode getExercise(String exercise) {
        switch (exercise) {
            case "Flex + IMU":
                return FLEX_IMU;
            case "Flex Only":
                return FLEX_ONLY;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mExercise;
    }
}
