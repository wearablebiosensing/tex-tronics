package edu.uri.wbl.smartglove.ble.receivers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by mcons on 11/12/2017.
 */

public abstract class BleUpdateReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("wbl.tex_tronics.ble_connection_update_filter");
    public static final String EXTRA_UPDATE = "wbl.tex_tronics.ble_update";
    public static final String EXTRA_DEVICE = "wbl.library.ble_device";

    public static final int UPDATE_CONNECTED = 0;
    public static final int UPDATE_DISCONNECTED = 1;
    public static final int UPDATE_SERVICES_DISCOVERED = 2;
    public static final int UPDATE_CHARACTERISTIC_READ = 3;
}
