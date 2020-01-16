package andrewpeltier.smartglovefragments.tex_tronics.enums;

/** ======================================
 *
 *              DeviceType Enum
 *
 *  ======================================
 *
 *  Enum that returns one of the two types of devices we can use for data collection,
 *  being either a Smart Glove or a Smart Sock.
 *
 * @author mcons on 2/28/2018.
 * @version 1.0
 */

public enum DeviceType
{
    /**
     * The device is a glove type meant for the user's hands
     */
    SMART_GLOVE ("smart_glove");
    /**
     * The device is a shoe or sock type meant for the user's feet
     */
    //SMART_SOCK ("smart_sock");

    private final String mType;

    private DeviceType(String type) {
        mType = type;
    }

    private static final String SMART_GLOVE_STRING = "smart_glove";
    //private static final String SMART_SOCK_STRING = "smart_sock";

    public static DeviceType getDevicetype(String device) {
        switch (device) {
            case SMART_GLOVE_STRING:
                return SMART_GLOVE;
            //case SMART_SOCK_STRING:
            //    return SMART_SOCK;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.mType;
    }
}
