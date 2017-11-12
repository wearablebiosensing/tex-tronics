package edu.uri.wbl.smartglove.ble.models;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by mcons on 11/12/2017.
 */

public class BluetoothServiceModel {
    public static BluetoothServiceModel CREATE(String deviceAddress, UUID uuid, List<BluetoothGattCharacteristic> characteristics) {
        return new BluetoothServiceModel(deviceAddress, uuid, characteristics);
    }

    private String mDeviceAddress;
    private UUID mUUID;
    private HashMap<UUID, BluetoothCharacteristicModel> mCharacteristics;

    private BluetoothServiceModel(String deviceAddress, UUID uuid, List<BluetoothGattCharacteristic> characteristics) {
        mCharacteristics = new HashMap<>(characteristics.size());
        mDeviceAddress = deviceAddress;
        mUUID = uuid;
        for(BluetoothGattCharacteristic model : characteristics) {
            Log.d(this.getClass().getSimpleName(), "UUID: " + model.getUuid().toString());
            BluetoothCharacteristicModel characteristicModel = BluetoothCharacteristicModel.CREATE(model.getUuid());
            if(characteristicModel == null) {
                Log.d(this.getClass().getSimpleName(), "Error Creating Characteristic Model!!");
                return;
            }
            mCharacteristics.put(model.getUuid(), characteristicModel);
        }
    }

    public UUID getUUID() {
        return mUUID;
    }

    public BluetoothCharacteristicModel[] getCharacteristics() {
        Collection<BluetoothCharacteristicModel> characteristics = mCharacteristics.values();
        ArrayList<BluetoothCharacteristicModel> characteristicsList = new ArrayList<>(characteristics);
        return characteristicsList.toArray(new BluetoothCharacteristicModel[characteristicsList.size()]);
    }

    private void putCharacteristic(BluetoothCharacteristicModel characteristic) {
        mCharacteristics.put(characteristic.getUUID(), characteristic);
    }
}
