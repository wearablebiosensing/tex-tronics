package andrewpeltier.smarttrousers.io;


import andrewpeltier.smarttrousers.R;

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
        String SMART_TROUSERS_TEXT = "This is example text for Smart Trousers!";
    }

    /**
     *  Interface options for displaying the animated gifs for exercises
     */
    interface InstructionsImage
    {
//        int FINGER_TAP_GIF = R.drawable.fingertap_gif;
//        int CLOSED_GRIP_GIF = R.drawable.closed_grip_gif;
//        int HAND_FLIP_GIF = R.drawable.hand_flip_gif;
//        int SCREEN_TAP_GIF = R.drawable.screen_tap_gif;
//        int HEEL_TAP_GIF = R.drawable.heeltap_gif;
//        int TOE_TAP_GIF = R.drawable.toetap_gif;
//        int FOOT_STOMP_GIF = R.drawable.footstomp_gif;
//        int WALK_STEPS_GIF = R.drawable.walksteps_gif;
    }
}
