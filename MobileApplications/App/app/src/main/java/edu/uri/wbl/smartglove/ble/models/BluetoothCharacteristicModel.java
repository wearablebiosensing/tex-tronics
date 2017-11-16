package edu.uri.wbl.smartglove.ble.models;

import java.util.UUID;

/**
 * Created by mcons on 11/12/2017.
 */

public class BluetoothCharacteristicModel {
    public static BluetoothCharacteristicModel CREATE(UUID uuid, byte[] value) {
        return new BluetoothCharacteristicModel(uuid, value);
    }

    private UUID mUUID;
    private byte[] mValue;

    private BluetoothCharacteristicModel(UUID uuid, byte[] value) {
        mUUID = uuid;
        mValue = value;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] value) {
        mValue = value;
    }
}
