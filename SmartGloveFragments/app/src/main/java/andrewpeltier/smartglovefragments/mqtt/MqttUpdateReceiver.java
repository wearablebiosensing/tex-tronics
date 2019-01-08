package andrewpeltier.smartglovefragments.mqtt;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by mcons on 2/28/2018.
 */

public abstract class MqttUpdateReceiver extends BroadcastReceiver
{
    public static final String INTENT_FILTER_STRING = "uri.wbl.tex_tronics.mqtt.update_intent_filter";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(INTENT_FILTER_STRING);

    public static final String UPDATE_TYPE = "uri.wbl.tex_tronics.mqtt.update_type";
}
