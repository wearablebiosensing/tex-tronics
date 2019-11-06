package edu.uri.wbl.tex_tronics.smartglove.visualize;

import java.util.Arrays;
import java.util.List;

import edu.uri.wbl.tex_tronics.smartglove.R;

public class ExerciseList
{
    public static ExerciseList get() {
        return new ExerciseList();
    }

    private ExerciseList() {
    }

    public List<Exercise> getExercises() {
        return Arrays.asList(
                new Exercise("Finger Tap", R.drawable.fingertap_animate, "Finger Tap", Choice.FINGER_TAP),
                new Exercise("Closed Grip", R.drawable.closed_grip_icon, "Closed Grip", Choice.CLOSED_GRIP),
                new Exercise("Hand Flip", R.drawable.hand_flip_animate, "Hand Flip", Choice.HAND_FLIP),
                new Exercise("Screen Tap", R.drawable.screen_tap_animate, "Screen Tap", Choice.SCREEN_TAP),
                new Exercise("Heel Tap", R.drawable.heeltap_animate, "Heel Tap", Choice.HEEL_TAP),
                new Exercise("Toe Tap", R.drawable.toetap_animate, "Toe Tap", Choice.TOE_TAP),
                new Exercise("Foot Stomp", R.drawable.footstomp_animate, "Foot Stomp", Choice.FOOT_STOMP),
                new Exercise("Walk Steps", R.drawable.walksteps_animate, "Walk Steps", Choice.WALK_STEPS));
    }
}