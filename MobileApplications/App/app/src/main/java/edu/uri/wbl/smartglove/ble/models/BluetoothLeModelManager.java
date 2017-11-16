package edu.uri.wbl.smartglove.ble.models;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mcons on 11/14/2017.
 */

public class BluetoothLeModelManager {
    private final static String TAG = "BluetoothLeModelManager";
    private static HashMap<String, BluetoothLeModel> sBluetoothLeModelHashMap;

    public static BluetoothLeModel CREATE(String bluetoothDeviceAddress) {
        return new BluetoothLeModel(bluetoothDeviceAddress);
    }

    public static BluetoothLeModel GET(String bluetoothDeviceAddress) {
        return new BluetoothLeModel(bluetoothDeviceAddress);
    }
}
