package edu.uri.wbl.smartglove;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.uri.wbl.smartglove.ble.models.BluetoothLeModel;
import edu.uri.wbl.smartglove.ble.receivers.BleUpdateReceiver;
import edu.uri.wbl.smartglove.ble.services.BleConnectionService;

public class MainActivity extends AppCompatActivity {
    private final String BT_ADDR = "F6:D7:BF:73:72:D3";

    Context mContext;
    BluetoothLeModel mBluetoothLeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mBluetoothLeModel = BluetoothLeModel.CREATE(BT_ADDR);
        registerReceiver(mBleUpdateReceiver, BleUpdateReceiver.INTENT_FILTER);

        Button startButton = (Button) findViewById(R.id.start_btn);
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        Button connectButton = (Button) findViewById(R.id.connect_btn);
        Button disconnectButton = (Button) findViewById(R.id.disconnect_btn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleConnectionService.START(mContext);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleConnectionService.STOP(mContext);
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleConnectionService.CONNECT(mContext, mBluetoothLeModel);
            }
        });
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleConnectionService.DISCONNECT(mContext, mBluetoothLeModel);
            }
        });
    }

    private BleUpdateReceiver mBleUpdateReceiver = new BleUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(BleUpdateReceiver.EXTRA_UPDATE, -1);
            switch(update) {
                case BleUpdateReceiver.UPDATE_CONNECTED:
                    BleConnectionService.DISCOVER_SERVICES(mContext, mBluetoothLeModel);
                    break;
                case BleUpdateReceiver.UPDATE_DISCONNECTED:

                    break;
                case BleUpdateReceiver.UPDATE_SERVICES_DISCOVERED:
                    BluetoothLeModel bluetoothLeModel = (BluetoothLeModel) intent.getSerializableExtra(BleUpdateReceiver.EXTRA_DEVICE);
                    if(bluetoothLeModel != null) {
                        Log.d(this.getClass().getSimpleName(), "Discovered Services on " + bluetoothLeModel.getBluetoothDeviceAddress());
                    }
                    break;
                default:

                    break;
            }
        }
    };
}
