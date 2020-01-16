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

                new Exercise("Finger_Tap",  R.drawable.smartglovelogo,"Finger_Tap", Choice.FINGER_TAP, "Glove"),
                new Exercise("Closed_Grip", R.drawable.smartglovelogo,"Closed_Grip", Choice.CLOSED_GRIP, "Glove"),
                new Exercise("Hand_Flip", R.drawable.smartglovelogo,"Hand_Flip", Choice.HAND_FLIP, "Glove"),
                new Exercise("Finger_to_Nose", R.drawable.smartglovelogo,"Finger_to_Nose", Choice.FINGER_TONOSE, "Glove"),
                new Exercise("Hold_Hands_Out",R.drawable.smartglovelogo,"Hold_Hands_Out", Choice.HOLD_HANDS_OUT, "Glove"),
                new Exercise("Resting_Hands_on_Thighs",R.drawable.smartglovelogo,"Resting_Hands_on_Thighs", Choice.RESTING_HANDS, "Glove"),
                new Exercise("Heel_Stomp",R.drawable.smartglovelogo, "Heel_Stomp", Choice.HEEL_STOMP, "Glove"),
                new Exercise("Toe_Tap", R.drawable.smartglovelogo,"Toe_Tap", Choice.TOE_TAP, "Glove"),
                new Exercise("Walk_Steps", R.drawable.smartglovelogo, "Walk_Steps", Choice.GAIT, "Glove"),


                new Exercise("Finger_Tap", R.drawable.fingertap_animate, "Finger_Tap", Choice.FINGER_TAP, "Glove"),
                new Exercise("Closed_Grip", R.drawable.closed_grip_icon, "Closed_Grip", Choice.CLOSED_GRIP, "Glove"),
                new Exercise("Hand_Flip", R.drawable.hand_flip_animate, "Hand_Flip", Choice.HAND_FLIP, "Glove"));
//                new Exercise("Screen Tap", R.drawable.screen_tap_animate, "Screen Tap", Choice.SCREEN_TAP, "Glove"),
//                new Exercise("Heel Tap", R.drawable.heeltap_animate, "Heel Tap", Choice.HEEL_TAP, "Shoe"),
//                new Exercise("Toe Tap", R.drawable.toetap_animate, "Toe Tap", Choice.TOE_TAP, "Shoe"),
//                new Exercise("Foot Stomp", R.drawable.footstomp_animate, "Foot Stomp", Choice.FOOT_STOMP, "Shoe"),
//                new Exercise("Walk Steps", R.drawable.walksteps_animate, "Walk Steps", Choice.WALK_STEPS, "Shoe"));
    }
}