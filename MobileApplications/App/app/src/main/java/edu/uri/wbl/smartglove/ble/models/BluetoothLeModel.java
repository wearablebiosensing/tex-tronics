package edu.uri.wbl.smartglove.ble.models;

import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by mcons on 11/12/2017.
 */

public class BluetoothLeModel implements Serializable {
    private static BluetoothLeModel sBluetoothLeModel;
    private static String sBluetoothDeviceAddress;
    private transient HashMap<UUID, BluetoothServiceModel> sServices;

    public static BluetoothLeModel CREATE(String bluetoothDeviceAddress, List<BluetoothGattService> services) {
        if(sBluetoothLeModel == null)
            sBluetoothLeModel = new BluetoothLeModel(bluetoothDeviceAddress, services);

        return sBluetoothLeModel;
    }

    public static BluetoothLeModel CREATE(String bluetoothDeviceAddress) {
        if(sBluetoothLeModel == null)
            sBluetoothLeModel = new BluetoothLeModel(bluetoothDeviceAddress, null);

        return sBluetoothLeModel;
    }

    //private String mBluetoothDeviceAddress;
    //private transient HashMap<UUID, BluetoothServiceModel> mServices;           // Transient attribute required for Serializable

    public BluetoothLeModel(String bluetoothDeviceAddress) {

    }

    private BluetoothLeModel(String bluetoothDeviceAddress, List<BluetoothGattService> services) {
        sBluetoothDeviceAddress = bluetoothDeviceAddress;
        if(services != null) {
            sServices = new HashMap<>(services.size());
            for(BluetoothGattService service : services) {
                BluetoothServiceModel serviceModel = BluetoothServiceModel.CREATE(bluetoothDeviceAddress, service.getUuid(), service.getCharacteristics());
                sServices.put(service.getUuid(), serviceModel);
            }
        } else if(sServices == null) {
            sServices = new HashMap<>();
        }
    }

    public String getBluetoothDeviceAddress() {
        return sBluetoothDeviceAddress;
    }

    public Collection<BluetoothServiceModel> getServices() {
        return sServices.values();
    }

    public BluetoothCharacteristicModel getCharacteristic(UUID characteristicUUID) {
        if(sServices == null) {
            Log.d("BluetoothLeModel", "No Services Stored in Model");
            return null;    // No Services stored in model
        }
        for(BluetoothServiceModel bluetoothServiceModel : sServices.values()) {
                for(BluetoothCharacteristicModel bluetoothCharacteristicModel : bluetoothServiceModel.getCharacteristics()) {
                    if(bluetoothCharacteristicModel.getUUID().equals(characteristicUUID)) {
                        return bluetoothCharacteristicModel;
                    }
                }
        }
        Log.d("BluetoothLeModel", "Could not find Characteristic");
        return null;
    }
}
