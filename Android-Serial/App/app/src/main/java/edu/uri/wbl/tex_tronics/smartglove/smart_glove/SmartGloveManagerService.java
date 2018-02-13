package edu.uri.wbl.tex_tronics.smartglove.smart_glove;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattServices;
import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;

/**
 * Created by mcons on 2/8/2018.
 */

public class SmartGloveManagerService extends Service {
    private static final String EXTRA_DEVICE = "tex_tronics.wbl.uri.ble.sg.device";
    private static final String HEADER = "Phone Date,Phone Time,Device Address,Timestamp,Thumb,Index,Middle,Ring,Pinky,Acc(x),Acc(y),Acc(z),Gyr(x),Gyr(y),Gyr(z),Mag(x),Mag(y),Mag(z)";
    private static final byte PACKET_ID_1 = 0x01;
    private static final byte PACKET_ID_2 = 0x02;

    private Context mContext;
    private SmartGloveData mSmartGloveData;
    private File mFile;
    private boolean mServiceBound;
    private BluetoothLeConnectionService mService;
    private ServiceConnection mServiceConnection;

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BLE", "Received Update");
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            switch (action) {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    mService.discoverServices(GattDevices.SMART_GLOVE_DEVICE);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:

                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    BluetoothGattCharacteristic characteristic = mService.getCharacteristic(GattDevices.SMART_GLOVE_DEVICE, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                    if (characteristic != null) {
                        mService.enableNotifications(GattDevices.SMART_GLOVE_DEVICE, characteristic);
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                        // Read Packet Data and Log in CSV File
                        // this can be done in separate Thread if needed
                        Log.d("SmartGlove", "Data Received");
                        String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        if(data[0] == PACKET_ID_1) {
                            mSmartGloveData = new SmartGloveData();
                            mSmartGloveData.setTimestamp(((long)data[1] << 24) | ((long)data[2] << 16) | ((long)data[3] << 8) | ((long)data[4]));
                            mSmartGloveData.setThumbFlex((((int)data[5] << 8) | ((int)data[6])) & 0x0000FFFF);
                            mSmartGloveData.setIndexFlex((((int)data[7] << 8) | ((int)data[8])) & 0x0000FFFF);
                            // TODO: Add rest of fingers
                        } else if(data[0] == PACKET_ID_2) {
                            mSmartGloveData.setAccX(((int)data[1] << 8) | ((int)data[2]));
                            mSmartGloveData.setAccY(((int)data[3] << 8) | ((int)data[4]));
                            mSmartGloveData.setAccZ(((int)data[5] << 8) | ((int)data[6]));
                            mSmartGloveData.setGyrX(((int)data[7] << 8) | ((int)data[8]));
                            mSmartGloveData.setGyrY(((int)data[9] << 8) | ((int)data[10]));
                            mSmartGloveData.setGyrZ(((int)data[11] << 8) | ((int)data[12]));
                            mSmartGloveData.setMagX(((int)data[13] << 8) | ((int)data[14]));
                            mSmartGloveData.setMagY(((int)data[15] << 8) | ((int)data[16]));
                            mSmartGloveData.setMagZ(((int)data[17] << 8) | ((int)data[18]));

                            String contents = deviceAddress + "," + mSmartGloveData.toString();

                            log("Data: " + mSmartGloveData.toString());

                            DataLogService.log(mContext, mFile, contents, HEADER);
                        } else {
                            log("Invalid Data Packet");
                            return;
                        }
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_READ:
                    break;
                case BluetoothLeConnectionService.GATT_DESCRIPTOR_WRITE:
                    break;
                case BluetoothLeConnectionService.GATT_NOTIFICATION_TOGGLED:
                    break;
                case BluetoothLeConnectionService.GATT_DEVICE_INFO_READ:
                    break;
            }
        }
    };

    public static void connect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, SmartGloveManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(SmartGloveAction.connect.toString());
        context.startService(intent);
    }

    public static void disconnect(Context context, String deviceAddress) {
        Intent intent = new Intent(context, SmartGloveManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(SmartGloveAction.disconnect.toString());
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        File parentFile = new File("/storage/emulated/0/Documents");
        int count = parentFile.list().length;
        mFile = new File(parentFile, "Session" + count + ".csv");

        mServiceConnection = new BleServiceConnection();

        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        bindService(new Intent(this, BluetoothLeConnectionService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (intent == null) {
            return START_REDELIVER_INTENT;
        }

        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);
        SmartGloveAction action = SmartGloveAction.getAction(intent.getAction());

        switch (action) {
            case connect:
                connect(deviceAddress);
                break;
            case disconnect:
                disconnect(deviceAddress);
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        unregisterReceiver(mBLEUpdateReceiver);

        super.onDestroy();
    }

    private void connect(String deviceAddress) {
        if (mServiceBound) {
            mService.connect(deviceAddress);
        } else {
            log("Cannot Connect - BLE Connection Service is not bound yet!");
        }
    }

    private void disconnect(String deviceAddress) {
        if (mServiceBound) {
            mService.disconnect(deviceAddress);
        } else {
            log("Could not Disconnect - BLE Connection Service is not bound!");
        }
    }

    private void log(String message) {
        Log.d("SmartGloveManager", message);
    }

    private class BleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBound = true;
            mService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();
            mService.connect(GattDevices.SMART_GLOVE_DEVICE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    }
}
