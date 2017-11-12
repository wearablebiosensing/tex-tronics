package edu.uri.wbl.smartglove.ble.models;

import android.bluetooth.BluetoothGattService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by mcons on 11/12/2017.
 */

public class BluetoothLeModel implements Serializable {
    public static BluetoothLeModel CREATE(String bluetoothDeviceAddress, List<BluetoothGattService> services) {
        return new BluetoothLeModel(bluetoothDeviceAddress, services);
    }

    public static BluetoothLeModel CREATE(String bluetoothDeviceAddress) {
        return new BluetoothLeModel(bluetoothDeviceAddress, null);
    }

    private String mBluetoothDeviceAddress;
    private transient HashMap<UUID, BluetoothServiceModel> mServices;           // Transient attribute required for Serializable

    private BluetoothLeModel(String bluetoothDeviceAddress, List<BluetoothGattService> services) {
        mBluetoothDeviceAddress = bluetoothDeviceAddress;
        if(services != null) {
            mServices = new HashMap<>(services.size());
            for(BluetoothGattService service : services) {
                BluetoothServiceModel serviceModel = BluetoothServiceModel.CREATE(bluetoothDeviceAddress, service.getUuid(), service.getCharacteristics());
                mServices.put(service.getUuid(), serviceModel);
            }
        }
    }

    public String getBluetoothDeviceAddress() {
        return mBluetoothDeviceAddress;
    }

    public BluetoothCharacteristicModel getCharacteristic(UUID characteristicUUID) {
        for(BluetoothServiceModel bluetoothServiceModel : mServices.values()) {
                for(BluetoothCharacteristicModel bluetoothCharacteristicModel : bluetoothServiceModel.getCharacteristics()) {
                    if(bluetoothCharacteristicModel.getUUID().equals(characteristicUUID)) {
                        return bluetoothCharacteristicModel;
                    }
                }
        }
        return null;
    }
}
