package edu.uri.wbl.smartglove;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import edu.uri.wbl.smartglove.ble.models.BluetoothCharacteristicModel;
import edu.uri.wbl.smartglove.ble.models.BluetoothLeModel;
import edu.uri.wbl.smartglove.ble.receivers.BleUpdateReceiver;
import edu.uri.wbl.smartglove.ble.services.BleConnectionService;
import edu.uri.wbl.smartglove.ble.services.BluetoothLeService;

public class MainActivity extends AppCompatActivity {
    private final String BT_ADDR = "F6:D7:BF:73:72:D3";

    Context mContext;
    //BluetoothLeModel mBluetoothLeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        //mBluetoothLeModel = BluetoothLeModel.CREATE(BT_ADDR);
        registerReceiver(mBleUpdateReceiver, BleUpdateReceiver.INTENT_FILTER);

        Button startButton = (Button) findViewById(R.id.start_btn);
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        Button connectButton = (Button) findViewById(R.id.connect_btn);
        Button disconnectButton = (Button) findViewById(R.id.disconnect_btn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothLeService.START(mContext);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothLeService.STOP(mContext);
            }
        });
    }

    private BleUpdateReceiver mBleUpdateReceiver = new BleUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(BleUpdateReceiver.EXTRA_UPDATE, -1);
            switch(update) {
                case BleUpdateReceiver.UPDATE_CONNECTED:

                    //BleConnectionService.DISCOVER_SERVICES(mContext, mBluetoothLeModel);
                    break;
                case BleUpdateReceiver.UPDATE_DISCONNECTED:

                    break;
                case BleUpdateReceiver.UPDATE_SERVICES_DISCOVERED:
                    /*BluetoothLeModel bluetoothLeModel = (BluetoothLeModel) intent.getSerializableExtra(BleUpdateReceiver.EXTRA_DEVICE);
                    if(bluetoothLeModel == null) {
                        Log.d("BLE Update Receiver", "Error Retrieving BluetoothLeModel");
                        return;
                    }
                    Log.d(this.getClass().getSimpleName(), "Discovered Services on " + bluetoothLeModel.getBluetoothDeviceAddress());
                    BluetoothCharacteristicModel bluetoothCharacteristicModel = bluetoothLeModel.getCharacteristic(UUID.fromString("00004003-0000-1000-8000-00805f9b34fb"));
                    if(bluetoothCharacteristicModel == null) {
                        Log.d("BLE Update Receiver", "Error finding Characteristic");
                        return;
                    }
                    byte[] value = bluetoothCharacteristicModel.getValue();
                    Log.d("BLE Update Receiver", value.toString());*/
                    break;
                default:

                    break;
            }
        }
    };
}
