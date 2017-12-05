package edu.uri.wbl.tex_tronics.ble;

/**
 * This enum enumerates the updates made available by the BluetoothLeService and received by
 * the BleUpdateReceiver.
 *
 * @author Matthew Constant
 * @version 1.0, 12/04/2017
 */

public enum BleUpdate {
    CONNECTED,
    DISCONNECTED,
    SERVICES_DISCOVERED,
    CHARACTERISTIC_READ,
    CHARACTERISTIC_WRITTEN,
    CHARACTERISTIC_UPDATED
}
