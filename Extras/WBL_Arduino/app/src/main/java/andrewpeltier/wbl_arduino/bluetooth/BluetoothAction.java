package andrewpeltier.wbl_arduino.bluetooth;

public enum BluetoothAction
{
    connect ("andrewpeltier.joshserial.connect"),
    disconnect ("andrewpeltier.joshserial.disconnect"),
    write ("andrewpeltier.joshserial.write"),
    read ("andrewpeltier.joshserial.read");

    private final String mAction;

    BluetoothAction(String action) {
        mAction = action;
    }

    public static BluetoothAction getAction(String action) {
        switch (action)
        {
            case "andrewpeltier.joshserial.connect":
                return connect;
            case "andrewpeltier.joshserial.disconnect":
                return disconnect;
            case "andrewpeltier.joshserial.write":
                return write;
            case "andrewpeltier.joshserial.read":
                return read;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mAction;
    }
}