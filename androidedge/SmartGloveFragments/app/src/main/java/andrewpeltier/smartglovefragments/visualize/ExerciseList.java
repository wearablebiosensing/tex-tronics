package andrewpeltier.smartglovefragments.visualize;

import java.util.Arrays;
import java.util.List;

import andrewpeltier.smartglovefragments.R;

/** ======================================
 *
 *          ExerciseList Class
 *
 *  ======================================
 *
 *  Creates and returns a list of exercise objects. Each exercise contains all of the information needed to
 *  create the ExerciseView.
 *
 *  @author Andrew Peltier
 *  @version 1.0
 *
 */
public class ExerciseList
{
    public static ExerciseList get() {
        return new ExerciseList();
    }

    private ExerciseList() {
    }

    public List<Exercise> getExercises()
    {
        return Arrays.asList(

                new Exercise("Finger Tap",  R.drawable.smartglovelogo,"Finger Tap", Choice.FINGER_TAP, "Glove"),
                new Exercise("Closed Grip", R.drawable.smartglovelogo,"Closed Grip", Choice.CLOSED_GRIP, "Glove"),
                new Exercise("Hand Flip", R.drawable.smartglovelogo,"Hand Flip", Choice.HAND_FLIP, "Glove"),
                new Exercise("Finger to Nose", R.drawable.smartglovelogo,"Finger to Nose", Choice.FINGER_TONOSE, "Glove"),
                new Exercise("Hold Hands Out",R.drawable.smartglovelogo,"Hold Hands Out", Choice.HOLD_HANDS_OUT, "Glove"),
                new Exercise("Resting Hands on Thighs",R.drawable.smartglovelogo,"Resting Hands on Thighs", Choice.RESTING_HANDS, "Glove"),
                new Exercise("Heel Stomp",R.drawable.smartglovelogo, "Heel Stomp", Choice.HEEL_STOMP, "Shoe"),
                new Exercise("Toe Tap", R.drawable.smartglovelogo,"Toe Tap", Choice.TOE_TAP, "Shoe"),
                new Exercise("Walk Steps", R.drawable.smartglovelogo, "Walk Steps", Choice.GAIT, "Shoe"),


                new Exercise("Finger Tap", R.drawable.fingertap_animate, "Finger Tap", Choice.FINGER_TAP, "Glove"),
                new Exercise("Closed Grip", R.drawable.closed_grip_icon, "Closed Grip", Choice.CLOSED_GRIP, "Glove"),
                new Exercise("Hand Flip", R.drawable.hand_flip_animate, "Hand Flip", Choice.HAND_FLIP, "Glove"));
//                new Exercise("Screen Tap", R.drawable.screen_tap_animate, "Screen Tap", Choice.SCREEN_TAP, "Glove"),
//                new Exercise("Heel Tap", R.drawable.heeltap_animate, "Heel Tap", Choice.HEEL_TAP, "Shoe"),
//                new Exercise("Toe Tap", R.drawable.toetap_animate, "Toe Tap", Choice.TOE_TAP, "Shoe"),
//                new Exercise("Foot Stomp", R.drawable.footstomp_animate, "Foot Stomp", Choice.FOOT_STOMP, "Shoe"),
//                new Exercise("Walk Steps", R.drawable.walksteps_animate, "Walk Steps", Choice.WALK_STEPS, "Shoe"));
    }
}