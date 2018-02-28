package edu.uri.wbl.tex_tronics.smartglove.tex_tronics.exception;

/**
 * Created by mcons on 2/27/2018.
 */

public class TransmitModeException extends Exception {
    private String mMessage;

    public TransmitModeException(String message) {
        mMessage = message;
    }

    @Override
    public String toString() {
        return mMessage;
    }
}
