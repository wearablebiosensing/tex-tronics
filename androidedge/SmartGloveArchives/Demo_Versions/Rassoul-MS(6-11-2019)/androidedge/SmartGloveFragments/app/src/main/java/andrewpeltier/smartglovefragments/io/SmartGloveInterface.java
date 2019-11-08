package andrewpeltier.smartglovefragments.io;


import andrewpeltier.smartglovefragments.R;

/**
 * Created by Andrew on 7/20/17.
 */

public interface SmartGloveInterface
{
    /**
     * Interface options for graphing
     */
    interface Graph
    {
        int READY = 1;
        int READY2 = 2;
    }

    /**
     * Interface options for Shared Preferences
     */
    interface Preferences
    {
        String NAME = "name";
        String GLOVE = "glove_address";
        String SOCK = "sock_address";
    }

    /**
     *  Interface options for displaying instructions for exercises
     */
    interface InstructionsText
    {
        String FINGER_TAP_TEXT = "Tap your index finger to your thumb 10x";
        String CLOSED_GRIP_TEXT = "Close and open your hand 10x";
        String HAND_FLIP_TEXT = "Flip your hand 10x";
        String SCREEN_TAP_TEXT = "Tap the screen 10x";
        String HEEL_TAP_TEXT = "Tap your heel against the floor 10x";
        String TOE_TAP_TEXT = "Tap your toe against the floor 10x";
        String FOOT_STOMP_TEXT = "Stomp your feet against the floor 10x";
        String WALK_STEPS_TEXT = "Walk 30 steps";
    }

    /**
     *  Interface options for displaying instructions for exercises
     */
    interface StudyInstructionsText
    {
        String RESTING_HANDS_TEXT = "Count backwards from 100";
        String HOLD_HANDS_OUT_TEXT = "Hold hands straight out";
        String FINGER_TONOSE_TEXT = "Finger to nose touching";
        String FINGER_TAP_TEXT = "Finger tap";
        String CLOSED_GRIP_TEXT = "Open/Close hands";
        String HAND_FLIP_TEXT = "Hand flipping";
        String HEEL_STOMP_TEXT = "Heel stomp";
        String TOE_TAP_TEXT = "Toe tap";
        String GAIT_TEXT = "Walk steps";
    }

    interface StudyInstructionsImage
    {
        int RESTING_HANDS_GIF = R.drawable.resting_hands_thighs_new;
        int FINGER_TAP_GIF = R.drawable.finger_tap_new;
        int CLOSED_GRIP_GIF = R.drawable.close_grip_new;
        int HANDS_HOLD_GIF = R.drawable.holding_hands_out_new;
        int HAND_FLIP_GIF = R.drawable.hand_flip_new;
        int SCREEN_TAP_GIF =R.drawable.finger_to_nose_new;
        int HEEL_TAP_GIF = R.drawable.heel_stomp_new;
        int TOE_TAP_GIF = R.drawable.toe_tap_new;
        int FOOT_STOMP_GIF = R.drawable.heel_stomp_new;
        int WALK_STEPS_GIF =R.drawable.walk_steps_new;
    }

    /**
     *  Interface options for displaying the animated gifs for exercises
     */
    interface InstructionsImage
    {
        int FINGER_TAP_GIF = R.drawable.finger_taps;
        int CLOSED_GRIP_GIF = R.drawable.hand_grasps;
        int RESTING_HANDS_GIF = R.drawable.resting_hands_thighs_new;
        int HAND_FLIP_GIF = R.drawable.hand_flip;
        int SCREEN_TAP_GIF = R.drawable.screen_tap_gif;
        int HEEL_TAP_GIF = R.drawable.heel_stomp_new;
        int TOE_TAP_GIF = R.drawable.toe_tap_new;
        int FOOT_STOMP_GIF = R.drawable.footstomp_gif;
        int WALK_STEPS_GIF = R.drawable.walk_steps_new;
    }
}
