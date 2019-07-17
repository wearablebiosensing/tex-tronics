package andrewpeltier.wbl_arduino.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import andrewpeltier.wbl_arduino.MainActivity;
import andrewpeltier.wbl_arduino.R;
import andrewpeltier.wbl_arduino.bluetooth.BluetoothService;
import andrewpeltier.wbl_arduino.fragments.commands.ButtonGridFrag;

public class HomeFragment extends Fragment
{
    private static final String TAG = "HomeFragment";
    private ListView mDeviceList;
    private Button connectButton, disconnectButton;
    private SimpleAdapter listAdapter;
    private String              connectionAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<HashMap<String,String>> myList = new ArrayList<HashMap<String,String>>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Sets up the paired device list
        mDeviceList = view.findViewById(R.id.device_list);
        getPairedDevices();
        if(myList.size() > 0)
        {
            // Creates a simple adapter with address strings and name strings, then sets that to list
            listAdapter = new SimpleAdapter(getActivity(), myList, R.layout.activity_scan_list_items, new String[] { "address","name"},
                    new int[] {R.id.address, R.id.name});
            mDeviceList.setAdapter(listAdapter);
        }
        else
            Log.i(TAG, "Paired Devices array error.");

        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                connectionAddress = ((HashMap<String,String>)adapterView.getItemAtPosition(i)).get("address");
                BluetoothService.connect(getActivity(), connectionAddress);
                Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
                //connectPrompt.setText("Connect to " + connectionAddress + "?");
            }
        });

        // Sets up the connect button
        connectButton = view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!BluetoothService.connected)
                {
                    //TODO: Change later
                    //Toast.makeText(getActivity(), "Please select a device.", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).addFragment(new ButtonGridFrag(), "ButtonGridFrag");
                }
                else
                {
                    //TODO: Fix
//                    BluetoothService.read(getActivity());
//                    // Leaves this activity, goes to SettingsActivity
//                    Intent intent = new Intent(getActivity(), CommandsActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();
                }
                Log.e(TAG, "Connected: " + BluetoothService.connected);
            }
        });
        disconnectButton = view.findViewById(R.id.disconnect_button);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(BluetoothService.connected)
                {
                    Toast.makeText(getActivity(), "Disconnecting...", Toast.LENGTH_SHORT).show();
                    BluetoothService.disconnect(getActivity());
                }
                else
                    Toast.makeText(getActivity(), "Not Currently Connected", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i(TAG, "onCreate: activity created.");
        return view;
    }

    /**                             --getPairedDevices()--
     *
     *  Bluetooth serial communication seems to need the device to be paired to your
     *  phone before communication starts. This activity enables bluetooth if it's disabled,
     *  then gets a list of each bluetooth address in your paired devices.
     */
    private void getPairedDevices()
    {
        // Gets the Bluetooth adapter, used to perform any Bluetooth task
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.d(TAG, "Bluetooth adaptor is NULL.");
        }

        // Checks if Bluetooth is enabled, enables it if not
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        // Gets paired devices, lists the addresses, then stores them in array
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, String> btDevice = new HashMap<>(); //need to use a hashmap for the multiline display
                Log.i(TAG, "Paired device found: " + device.getAddress());
                btDevice.put("name", device.getName());
                btDevice.put("address", device.getAddress());
                myList.add(btDevice);
            }
        }
    }
}
