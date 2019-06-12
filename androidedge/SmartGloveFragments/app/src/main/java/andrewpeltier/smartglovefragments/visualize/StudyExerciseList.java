package andrewpeltier.smartglovefragments.visualize;

import java.util.Arrays;
import java.util.List;

import andrewpeltier.smartglovefragments.R;

public class StudyExerciseList {

    public static StudyExerciseList get() {
        return new StudyExerciseList();
    }

    private StudyExerciseList() {
    }

    public List<StudyExercise> getStudyExercises()
    {
        return Arrays.asList(
                new StudyExercise("Finger Tap",  "Finger Tap", StudyChoice.FINGER_TAP, "Glove"),
                new StudyExercise("Closed Grip", "Closed Grip", StudyChoice.CLOSED_GRIP, "Glove"),
                new StudyExercise("Hand Flip","Hand Flip", StudyChoice.HAND_FLIP, "Glove"),
                new StudyExercise("Finger to Nose", "Finger to Nose", StudyChoice.FINGER_TONOSE, "Glove"),
                new StudyExercise("Hold Hands Out", "Hold Hands Out", StudyChoice.HOLD_HANDS_OUT, "Glove"),
                new StudyExercise("Resting Hands on Thighs", "Resting Hands on Thighs", StudyChoice.RESTING_HANDS, "Glove"),
                new StudyExercise("Heel Stomp", "Heel Stomp", StudyChoice.HEEL_STOMP, "Shoe"),
                new StudyExercise("Toe Tap", "Toe Tap", StudyChoice.TOE_TAP, "Shoe"),
                new StudyExercise("Walk Steps",  "Walk Steps", StudyChoice.GAIT, "Shoe"));
    }
}
