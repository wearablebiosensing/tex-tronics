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
        String RESTING_HANDS_TEXT = "Sit still and count backwards from 100";
        String HOLD_HANDS_OUT_TEXT = "Extend hands and arms out to the front";
        String FINGER_TONOSE_TEXT = "Extend hand, bending elbow touch index finger to nose";
        String FINGER_TAP_TEXT = "Tap your index finger to your thumb big and fast";
        String CLOSED_GRIP_TEXT = "Close and open your hand, extend fingers big and fast";
        String HAND_FLIP_TEXT = "Extend hand and flip hand palm up and down fast";
        String HEEL_STOMP_TEXT = "Stomp with heel only big and fast";
        String TOE_TAP_TEXT = "Leave heel planted and tap up and down, big and fast";
        String GAIT_TEXT = "Walk 30 steps";
    }

    interface StudyInstructionsImage
    {
        int FINGER_TAP_GIF = R.drawable.smartglovelogo;
        int CLOSED_GRIP_GIF = R.drawable.smartglovelogo;
        int HAND_FLIP_GIF = R.drawable.smartglovelogo;
        int SCREEN_TAP_GIF =R.drawable.smartglovelogo;
        int HEEL_TAP_GIF = R.drawable.smartglovelogo;
        int TOE_TAP_GIF = R.drawable.smartglovelogo;
        int FOOT_STOMP_GIF = R.drawable.smartglovelogo;
        int WALK_STEPS_GIF =R.drawable.smartglovelogo;
    }

    /**
     *  Interface options for displaying the animated gifs for exercises
     */
    interface InstructionsImage
    {
        int FINGER_TAP_GIF = R.drawable.fingertap_gif;
        int CLOSED_GRIP_GIF = R.drawable.closed_grip_gif;
        int HAND_FLIP_GIF = R.drawable.hand_flip_gif;
        int SCREEN_TAP_GIF = R.drawable.screen_tap_gif;
        int HEEL_TAP_GIF = R.drawable.heeltap_gif;
        int TOE_TAP_GIF = R.drawable.toetap_gif;
        int FOOT_STOMP_GIF = R.drawable.footstomp_gif;
        int WALK_STEPS_GIF = R.drawable.walksteps_gif;
    }
}
