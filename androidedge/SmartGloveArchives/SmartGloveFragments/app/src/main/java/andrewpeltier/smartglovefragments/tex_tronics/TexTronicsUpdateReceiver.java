package andrewpeltier.smartglovefragments.tex_tronics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by mcons on 2/27/2018.
 */

public abstract class TexTronicsUpdateReceiver extends BroadcastReceiver {
    public static final String INTENT_FILTER_STRING = "uri.wbl.tex_tronics.update_intent_filter";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(INTENT_FILTER_STRING);

    public static final String UPDATE_TYPE = "uri.wbl.tex_tronics.update_type";
    public static final String UPDATE_DEVICE = "uri.wbl.tex_tronics.device";

    public static void update(Context context, String deviceAddress, TexTronicsUpdate update) {
        Intent intent = new Intent(INTENT_FILTER_STRING);
        intent.putExtra(UPDATE_DEVICE, deviceAddress);
        intent.putExtra(UPDATE_TYPE, update);
        context.sendBroadcast(intent);
    }
}
