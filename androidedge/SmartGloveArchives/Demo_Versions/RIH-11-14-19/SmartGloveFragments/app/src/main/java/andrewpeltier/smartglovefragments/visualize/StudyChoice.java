package andrewpeltier.smartglovefragments.visualize;

import andrewpeltier.smartglovefragments.io.SmartGloveInterface;

public enum StudyChoice implements SmartGloveInterface {

    /**
     * Enum containing for each of the instructions text, using the
     * SmartGloveInterface
     */

    RESTING_HANDS(StudyInstructionsText.RESTING_HANDS_TEXT),
    HOLD_HANDS_OUT(StudyInstructionsText.HOLD_HANDS_OUT_TEXT),
    FINGER_TONOSE(StudyInstructionsText.FINGER_TONOSE_TEXT),
    FINGER_TAP(StudyInstructionsText.FINGER_TAP_TEXT),
    CLOSED_GRIP(StudyInstructionsText.CLOSED_GRIP_TEXT),
    HAND_FLIP(StudyInstructionsText.HAND_FLIP_TEXT),
    HEEL_STOMP(StudyInstructionsText.HEEL_STOMP_TEXT),
    TOE_TAP(StudyInstructionsText.TOE_TAP_TEXT),
    GAIT(StudyInstructionsText.GAIT_TEXT);

    /**
     * Gets all of the information from the exercises, including choice
     */
    static StudyExerciseList exercises = StudyExerciseList.get();

    // Name to be displayed on the exercise icon in the Exercise Selection Fragment
    private String displayName;

    StudyChoice(String displayName)
    {
        this.displayName = displayName;
    }

    /** getChoice()
     *
     * Called by the Main Activity. We return the choice from the enum based
     * on the name of the exercised. We need this to create the Smart Glove
     * object.
     *
     * @param choice            -Name of exercise
     * @return              Value of the choice enum
     */
    public static StudyChoice getChoice(String choice)
    {
        // Look through the list of devices to find the correct exercise
        for (StudyExercise exercise : exercises.getStudyExercises())
        {
            // Return the corresponding choice
            if (exercise.getExerciseName().equals(choice))
            {
                return exercise.getChoice();
            }
        }
        return null;
    }

    /** toString()
     *
     * Called by the TexTronics Manager Service. This is the opposite of getChoice(),
     * since it takes a choice as a parameter and returns the name of the exercise.
     *
     * @param choice                -Choice enum value
     * @return                  Name of the exercise
     */
    public static String toString(StudyChoice choice)
    {
        // Looks through the list to find the exercise name
        for (StudyExercise exercise : exercises.getStudyExercises())
        {
            if (exercise.getChoice().equals(choice))
            {
                return exercise.getExerciseName();
            }
        }
        return null;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}


