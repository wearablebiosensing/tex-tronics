package edu.uri.wbl.tex_tronics.ble;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by mcons on 11/28/2017.
 */

public abstract class BleUpdateReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("wbl.tex_tronics.ble_connection_update_filter");
    public static final String EXTRA_UPDATE = "wbl.tex_tronics.ble_update";
    public static final String EXTRA_DEVICE = "wbl.tex_tronics.ble_device";
    public static final String EXTRA_CHARACTERISTIC = "wbl.tex_tronics.ble_characteristic";
    public static final String EXTRA_VALUE = "wbl.tex_tronics.ble_value";
}
