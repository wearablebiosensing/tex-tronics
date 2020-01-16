package andrewpeltier.smartglovefragments.visualize;

/** ======================================
 *
 *            Exercise Class
 *
 *  ======================================
 *
 * Object containing all of the information from an exercise. This is primarily used to
 * create the ExerciseView, although some of this information is used to create the SmartGlove device
 * object.
 *
 * @author Andrew Peltier
 * @version 1.0
 */
public class Exercise
{
    private final String exerciseName;          // Name of the exercise
    private final int exerciseIcon;             // Icon used to represent the exercise
    private final String textName;              // Exercise name used in text field
    private final Choice choice;                // Choice, containing the instructions for the exercise
    private final String device;                // Type of device

    public Exercise(String exerciseName, int exerciseIcon, String textName, Choice choice, String device)
    {
        this.exerciseName = exerciseName;
        this.exerciseIcon = exerciseIcon;
        this.textName = textName;
        this.choice = choice;
        this.device = device;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getExerciseIcon() {
        return exerciseIcon;
    }

    public String getTextName() {
        return textName;
    }

    public Choice getChoice() {
        return choice;
    }

    public String getDevice() { return device; }

    public static String getDeviceName(String choice)
    {
        for (Exercise exercise : ExerciseList.get().getExercises())
        {
            if (exercise.getExerciseName().equals(choice)) {
                return exercise.getDevice();
            }
        }
        return null;
    }
}