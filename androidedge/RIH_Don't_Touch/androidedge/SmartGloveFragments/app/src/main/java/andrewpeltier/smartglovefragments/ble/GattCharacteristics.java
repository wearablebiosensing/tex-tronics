package andrewpeltier.smartglovefragments.ble;

import java.util.UUID;

/**
 * Created by mcons on 2/8/2018.
 */

public class GattCharacteristics {
    // Smart Glove Nano Chars
    public static final UUID TX_CHARACTERISTIC = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID RX_CHARACTERISTIC = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID TX_CHARACTERISTIC2 = UUID.fromString("00001532-1212-EFDE-1523-785FEABCD123");
    public static final UUID RX_CHARACTERISTIC2 = UUID.fromString("00001531-1212-EFDE-1523-785FEABCD123");
}


// 00001530-1212-EFDE-1523-785FEABCD123

//tx 00001532-1212-EFDE-1523-785FEABCD123
//rx 00001531-1212-EFDE-1523-785FEABCD123