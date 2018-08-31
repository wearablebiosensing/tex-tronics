package andrewpeltier.smartglovefragments.tex_tronics.enums;

/**
 * Created by mcons on 2/28/2018.
 */

public enum DeviceType {
    SMART_GLOVE ("smart_glove"),
    SMART_SOCK ("smart_sock");

    private final String mType;

    private DeviceType(String type) {
        mType = type;
    }

    private static final String SMART_GLOVE_STRING = "smart_glove";
    private static final String SMART_SOCK_STRING = "smart_sock";

    public static DeviceType getDevicetype(String device) {
        switch (device) {
            case SMART_GLOVE_STRING:
                return SMART_GLOVE;
            case SMART_SOCK_STRING:
                return SMART_SOCK;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.mType;
    }
}
