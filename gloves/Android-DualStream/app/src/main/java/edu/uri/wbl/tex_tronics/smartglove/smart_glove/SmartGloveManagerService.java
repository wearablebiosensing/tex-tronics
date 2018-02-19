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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.uri.wbl.tex_tronics.smartglove.ble.BluetoothLeConnectionService;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattCharacteristics;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices;
import edu.uri.wbl.tex_tronics.smartglove.ble.GattServices;
import edu.uri.wbl.tex_tronics.smartglove.io.DataLogService;

import static edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices.SMART_GLOVE_DEVICE_NOT_PCB;
import static edu.uri.wbl.tex_tronics.smartglove.ble.GattDevices.SMART_GLOVE_DEVICE_PCB;

/**
 * Created by mcons on 2/8/2018.
 */

public class SmartGloveManagerService extends Service {
    private static final String EXTRA_DEVICE = "tex_tronics.wbl.uri.ble.sg.device";
    private static final String HEADER = "Device Address,Timestamp,Thumb,Index";
//    private static final byte PACKET_ID_1 = 0x01;
//    private static final byte PACKET_ID_2 = 0x02;

    private Context mContext;
//    private SmartGloveData mSmartGloveData;
    private File mGloveFile, mSockFile;
    private boolean mServiceBound;
    private BluetoothLeConnectionService mService;
    private ServiceConnection mServiceConnection;

    private SmartGloveData mSmartGlove, mSmartSock;

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BLE", "Received Update");
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);
            switch (action) {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    switch (deviceAddress) {
                        case SMART_GLOVE_DEVICE_NOT_PCB:
                            mSmartGlove = new SmartGloveData(deviceAddress);
                            break;
                        case SMART_GLOVE_DEVICE_PCB:
                            mSmartSock = new SmartGloveData(deviceAddress);
                            break;
                        default:
                            log("Not a valid Smart Device");
                            return;
                    }

                    mService.discoverServices(deviceAddress);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:

                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    BluetoothGattCharacteristic characteristic = mService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                    if (characteristic != null) {
                        mService.enableNotifications(deviceAddress, characteristic);
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)) {
                        // Read Packet Data and Log in CSV File
                        // this can be done in separate Thread if needed
                        Log.d("SmartGlove", "Data Received");
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        switch (deviceAddress) {
                            case SMART_GLOVE_DEVICE_NOT_PCB:
                                // First Data Set
                                mSmartGlove.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                                mSmartGlove.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                                mSmartGlove.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));

                                DataLogService.log(mContext, mGloveFile, mSmartGlove.toString(), HEADER);

                                // Second Data Set
                                mSmartGlove.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                                mSmartGlove.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                                mSmartGlove.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                                DataLogService.log(mContext, mGloveFile, mSmartGlove.toString(), HEADER);

                                // Third Data Set
                                mSmartGlove.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                                mSmartGlove.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                                mSmartGlove.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                                DataLogService.log(mContext, mGloveFile, mSmartGlove.toString(), HEADER);
                                break;
                            case SMART_GLOVE_DEVICE_PCB:
                                // First Data Set
                                mSmartSock.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                                mSmartSock.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                                mSmartSock.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));

                                DataLogService.log(mContext, mSockFile, mSmartSock.toString(), HEADER);

                                // Second Data Set
                                mSmartSock.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                                mSmartSock.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
                                mSmartSock.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));

                                DataLogService.log(mContext, mSockFile, mSmartSock.toString(), HEADER);

                                // Third Data Set
                                mSmartSock.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                                mSmartSock.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                                mSmartSock.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                                DataLogService.log(mContext, mSockFile, mSmartSock.toString(), HEADER);
                                break;
                        }

//                        // First Data Set
//                        mSmartGloveData.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
//                        mSmartGloveData.setThumbFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
//                        mSmartGloveData.setIndexFlex((((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF))));
//
//                        DataLogService.log(mContext, mFile, mSmartGloveData.toString(), HEADER);
//
//                        // Second Data Set
//                        mSmartGloveData.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
//                        mSmartGloveData.setThumbFlex((((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF))));
//                        mSmartGloveData.setIndexFlex((((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF))));
//
//                        DataLogService.log(mContext, mFile, mSmartGloveData.toString(), HEADER);
//
//                        // Third Data Set
//                        mSmartGloveData.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
//                        mSmartGloveData.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
//                        mSmartGloveData.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));
//
//                        DataLogService.log(mContext, mFile, mSmartGloveData.toString(), HEADER);




//                        if(data[0] == PACKET_ID_1) {
//                            mSmartGloveData = new SmartGloveData();
//                            mSmartGloveData.setTimestamp(((data[1] & 0x00FF) << 24) | ((data[2] & 0x00FF) << 16) | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
//                            //mSmartGloveData.setTimestamp(((long)(data[1] & 0x00FF) << 24) | ((long)(data[2] & 0x00FF) << 16) | ((long)data[3] << 8) | ((long)data[4]));
//                            mSmartGloveData.setThumbFlex((((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
//                            mSmartGloveData.setIndexFlex((((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
//
//                            String contents1 = deviceAddress + "," + mSmartGloveData.toString();
//
//                            mSmartGloveData.setTimestamp(((data[9] & 0x00FF) << 24) | ((data[10] & 0x00FF) << 16) | ((data[11] & 0x00FF) << 8) | (data[12] & 0x00FF));
//                            //mSmartGloveData.setTimestamp(((long)(data[1] & 0x00FF) << 24) | ((long)(data[2] & 0x00FF) << 16) | ((long)data[3] << 8) | ((long)data[4]));
//                            mSmartGloveData.setThumbFlex((((data[13] & 0x00FF) << 8) | ((data[14] & 0x00FF))));
//                            mSmartGloveData.setIndexFlex((((data[15] & 0x00FF) << 8) | ((data[16] & 0x00FF))));
//
//                            String contents2 = deviceAddress + "," + mSmartGloveData.toString();
//
//                            String contents = contents1 + "\n" + contents2 + "\n";
//                            DataLogService.log(mContext, mFile, contents, HEADER);
//
//                            // TODO: Add rest of fingers
//                        } else if(data[0] == PACKET_ID_2) {
//                            mSmartGloveData.setTimestamp(((data[1] & 0x00FF) << 24) | ((data[2] & 0x00FF) << 16) | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
//                            //mSmartGloveData.setTimestamp(((long)(data[1] & 0x00FF) << 24) | ((long)(data[2] & 0x00FF) << 16) | ((long)data[3] << 8) | ((long)data[4]));
//                            mSmartGloveData.setThumbFlex((((data[5] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
//                            mSmartGloveData.setIndexFlex((((data[7] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
//
//                            String contents3 = deviceAddress + "," + mSmartGloveData.toString();
//
//                            mSmartGloveData.setTimestamp(((data[9] & 0x00FF) << 24) | ((data[10] & 0x00FF) << 16) | ((data[11] & 0x00FF) << 8) | (data[12] & 0x00FF));
//                            //mSmartGloveData.setTimestamp(((long)(data[1] & 0x00FF) << 24) | ((long)(data[2] & 0x00FF) << 16) | ((long)data[3] << 8) | ((long)data[4]));
//                            mSmartGloveData.setThumbFlex((((data[13] & 0x00FF) << 8) | ((data[14] & 0x00FF))));
//                            mSmartGloveData.setIndexFlex((((data[15] & 0x00FF) << 8) | ((data[16] & 0x00FF))));
//
//                            String contents4 = deviceAddress + "," + mSmartGloveData.toString();
//
//                            String contents = contents3 + "\n"+ contents4 + "\n";
//
//                            log("Data: " + mSmartGloveData.toString());
//
//                            DataLogService.log(mContext, mFile, contents, HEADER);
//                        } else {
//                            log("Invalid Data Packet (" + data[0] + ")");
//                            return;
//                        }
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

        Date date = Calendar.getInstance().getTime();
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);
        String datetime = dateString + "/" + timeString;

        File parentFile = new File("/storage/emulated/0/Documents");
        String gloveFileName = datetime + "_glove.csv";
        String sockFileName = datetime + "_sock.csv";
        mGloveFile = new File(parentFile, gloveFileName);
        mSockFile = new File(parentFile, sockFileName);

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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    }
}
