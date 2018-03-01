package edu.uri.wbl.tex_tronics.smartglove.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uri.wbl.tex_tronics.smartglove.R;

/**
 * Created by mcons on 2/28/2018.
 */

public class BleDeviceAdapter extends ArrayAdapter<BleDeviceModel> {
    public BleDeviceAdapter(Context context, ArrayList<BleDeviceModel> bleDevices) {
        super(context, 0, bleDevices);
    }

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        BleDeviceModel bleDevice = getItem(position);

        // Check if new view is being used (new views must be inflated)
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ble_device_list_item, parent, false);
        }

        TextView bleDeviceAddressTextView = convertView.findViewById(R.id.ble_device_address);
        TextView bleDeviceNameTextView = convertView.findViewById(R.id.ble_device_name);

        bleDeviceAddressTextView.setText(bleDevice.getDeviceAddress());
        bleDeviceNameTextView.setText(bleDevice.getDeviceName());

        return convertView;
    }
}
