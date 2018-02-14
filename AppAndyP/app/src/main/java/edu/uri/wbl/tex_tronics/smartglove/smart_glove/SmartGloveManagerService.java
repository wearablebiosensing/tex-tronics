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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    private static final String HEADER = "Date,Time,Data";

    private Context mContext;
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
                        Log.d("SmartGlove", "Data Received");

                        // Takes the first 4 packets from ticks and stores them
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);
                        // Check to see if byte[0] is equal to one
//                        byte[] ticks = new byte[] {data[1], data[2], data[3], data[4]};
//                        ByteBuffer buffer = ByteBuffer.wrap(ticks);
//                        buffer.order(ByteOrder.LITTLE_ENDIAN);
//                        long realticks = buffer.getLong();
//                        System.out.println("Real Ticks: " + realticks);

                        DataLogService.log(mContext, mFile, data.toString(), HEADER);
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

    public static void scan(Context context)
    {
        Intent intent = new Intent(context, SmartGloveManagerService.class);
        intent.setAction(SmartGloveAction.scan.toString());
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mFile = new File("/storage/emulated/0/Documents/session1.csv");
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

        switch (action)
        {
            case connect:
                connect(deviceAddress);
                break;
            case disconnect:
                disconnect(deviceAddress);
                break;
            case scan:
                scan();
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

    private void scan()
    {
        if (mServiceBound) {
            System.out.println("Started scan from Manager");
            mService.scan(true);
        }
        else
            log("Could not Scan - BLE Connection Service is not bound!");
    }

    private void log(String message) {
        Log.d("SmartGloveManager", message);
    }

    private class BleServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBound = true;
            mService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();

            // This line of code connects it to the smart glove no matter what

//            mService.connect(GattDevices.SMART_GLOVE_DEVICE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    }
}
