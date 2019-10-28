package andrewpeltier.smartglovefragments.tex_tronics.exceptions;

/**
 * Created by mcons on 2/28/2018.
 */

public class IllegalDeviceType extends Exception {
    private String mMessage;

    public IllegalDeviceType(String message) {
        mMessage = message;
    }

    @Override
    public String toString() {
        return mMessage;
    }
}
