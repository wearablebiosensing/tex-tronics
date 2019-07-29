package andrewpeltier.smarttrousers.visualize;

public class Exercise {
    private final String exerciseName;
    private final int exerciseIcon;
    private final String textName;
    private final Choice choice;
    private final String device;

    public Exercise(String exerciseName, int exerciseIcon, String textName, Choice choice, String device) {
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