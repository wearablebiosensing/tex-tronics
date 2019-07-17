package edu.uri.wbl.tex_tronics.smartglove.visualize;

public class Exercise {

    private final String exerciseName;
    private final int exerciseIcon;
    private final String textName;
    private final Choice choice;

    public Exercise(String exerciseName, int exerciseIcon, String textName, Choice choice) {
        this.exerciseName = exerciseName;
        this.exerciseIcon = exerciseIcon;
        this.textName = textName;
        this.choice = choice;
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
}