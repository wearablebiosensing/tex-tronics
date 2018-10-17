package andrewpeltier.smarttrousers.visualize;

import andrewpeltier.smarttrousers.io.SmartGloveInterface;

public enum Choice implements SmartGloveInterface {

    SMARTTROUSERS(InstructionsText.SMART_TROUSERS_TEXT);

    static ExerciseList exercises = ExerciseList.get();

    private String displayName;

    Choice(String displayName) {
        this.displayName = displayName;
    }

    public static Choice getChoice() {
        return SMARTTROUSERS;
    }

    public static String toString(Choice choice) {
        return "Smart Trousers";
    }

    public String getDisplayName() {
        return displayName;
    }
}