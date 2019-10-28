package andrewpeltier.smartglovefragments.visualize;

public class StudyExercise {
    private final String exerciseName;          // Name of the exercise
   // private final int exerciseIcon;             // Icon used to represent the exercise
    private final String textName;              // Exercise name used in text field
    private final StudyChoice choice;                // Choice, containing the instructions for the exercise
    private final String device;                // Type of device

    public StudyExercise(String exerciseName, String textName, StudyChoice choice, String device)
    {
        this.exerciseName = exerciseName;
        //this.exerciseIcon = exerciseIcon;
        this.textName = textName;
        this.choice = choice;
        this.device = device;
    }

    public String getExerciseName() {
        return exerciseName;
    }

//    public int getExerciseIcon() {
//        return exerciseIcon;
//    }

    public String getTextName() {
        return textName;
    }

    public StudyChoice getChoice() {
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
