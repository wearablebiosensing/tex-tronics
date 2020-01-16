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
                new StudyExercise("Finger_Tap",  "Finger_Tap", StudyChoice.FINGER_TAP, "Glove"),
                new StudyExercise("Closed_Grip", "Closed_Grip", StudyChoice.CLOSED_GRIP, "Glove"),
                new StudyExercise("Hand_Flip","Hand_Flip", StudyChoice.HAND_FLIP, "Glove"),
                new StudyExercise("Finger_to_Nose", "Finger_to_Nose", StudyChoice.FINGER_TONOSE, "Glove"),
                new StudyExercise("Hold_Hands_Out", "Hold_Hands_Out", StudyChoice.HOLD_HANDS_OUT, "Glove"),
                new StudyExercise("Resting_Hands_on_Thighs", "Resting_Hands_on_Thighs", StudyChoice.RESTING_HANDS, "Glove"),
                new StudyExercise("Heel_Stomp", "Heel_Stomp", StudyChoice.HEEL_STOMP, "Glove"),
                new StudyExercise("Toe_Tap", "Toe_Tap", StudyChoice.TOE_TAP, "Glove"),
                new StudyExercise("Walk_Steps",  "Walk_Steps", StudyChoice.GAIT, "Glove"));
    }
}
