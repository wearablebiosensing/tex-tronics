package andrewpeltier.smarttrousers.visualize;

import java.util.Arrays;
import java.util.List;


public class ExerciseList
{
    public static ExerciseList get() {
        return new ExerciseList();
    }

    private ExerciseList() {
    }

    public List<Exercise> getExercises() {

        new Exercise("Smart Trousers", 0, "Smart Trousers", Choice.SMARTTROUSERS, "Glove");

        return null;
    }
}