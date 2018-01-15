package edu.uri.wbl.tex_tronics.mqtt;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by mcons on 12/6/2017.
 */

public class MqttConnectionManager extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;    // Do not restart service
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
