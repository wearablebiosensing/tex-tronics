package andrewpeltier.wbl_arduino.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class BluetoothService extends Service
{
    private static final String EXTRA_DEVICE = "example.android.software_engineering_305.application.device";
    private static final String EXTRA_DATA = "example.android.software_engineering_305.application.data";
    private static final String HEADER = "Date, Time, Val1, Val2";
    private static final String FILENAME = "test.csv";
    private static final String TAG = "BluetoothService";
    public static boolean connected = false;
    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothSocket serialSocket;
    private InputStream serialInputStream;
    private OutputStream serialOutputStream;
    private byte[] readBuffer;
    private int readBufferPosition;

    /**
     *                  --onCreate()--
     * Called when the service is created. Just sets the context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    /**                         --onStartCommand(...)--
     *
     * Called whenever an intent is called via "startService(...)"
     * This is responsible for identifying what intent was called, and calls that intent's
     * respective method in the service.
     *
     * @param intent: The intent that was started. This allows the operation to run in the
     *              background. The intent also holds extra information of type String.
     * @param flags
     * @param startID
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Checks to see if intent was null
        if (intent == null) {
            return START_REDELIVER_INTENT;
        }

        // Gets the extra data from the intent
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);
        String data = intent.getStringExtra(EXTRA_DATA);
        // Gets the action from BluetoothAction
        BluetoothAction action = BluetoothAction.getAction(intent.getAction());
        // Checks to see what intent was called, then calls its respective method
        switch (action)
        {
            case connect:
                connect(deviceAddress);
                break;
            case disconnect:
                disconnect();
                break;
            case write:
                write(data);
                break;
            case read:
                read();
                break;
        }
        return START_REDELIVER_INTENT;
    }

    /**             --connect(String deviceAddress)--
     * This is the method (now running in the background) that connects to the device
     * by using its address.
     *
     * @param deviceAddress: The address to the bluetooth device that the user selected
     */
    public void connect(String deviceAddress)
    {
        Log.d(TAG, "connect: Connecting...");
        // Checks to see if we are currently connected
        if(connected)
        {
            Log.i(TAG,"Failed: Connection request while already connected");
            return;
        }
        // Checks to see if Bluetooth is working
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Log.i(TAG,"Failed: Bluetooth Adapter Error");
            return;
        }
        // Gets paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices) {
                Log.i(TAG, "Paired device found: " + device.getAddress());

                // Gets device that has the same address as selected
                if(device.getAddress().equals(deviceAddress))
                    mDevice = device;
            }
        }
        // Checks to see if the device is active
        if(mDevice == null)
        {
            Log.e(TAG,"Failed: Device could not be found");
            return;
        }

        try{
            // Connects to the Bluetooth Serial port
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Serial Port UUID
            serialSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
            serialSocket.connect();
            // Gathers the port's input and output streams
            serialInputStream = serialSocket.getInputStream();
            serialOutputStream = serialSocket.getOutputStream();
            connected = true;
            Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();
            // Writes all commands that
            //DevDataTransfer.writeLookCommands(mContext);
            Log.e(TAG,"Success! Connected to " + mDevice.getName());
        }
        catch (IOException e)
        {
            serialSocket = null;
            serialInputStream = null;
            serialOutputStream = null;
            e.printStackTrace();
        }

    }

    /**                     --disconnect()--
     * Disconnects the device from app by closing all streams and the serial
     * socket that the device is currently connected to.
     *
     */
    public void disconnect()
    {
        try
        {
            connected = false;
            serialInputStream.close();
            serialOutputStream.close();
            serialSocket.close();
            Log.e(TAG, "Disconnected from device");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**                 --write(String data)--
     * Writes a message to the Bluetooth device. At this point, it adds a new line.
     * This may need to be changed.
     *
     * @param data: The message that you want to write
     */
    public void write(String data)
    {
        try{
            data += '\n';
            serialOutputStream.write(data.getBytes());
            Log.i(TAG, "Wrote: " + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read()
    {
        try {
            readBufferPosition = 0;
            readBuffer = new byte[256];
            // Checks to see if a message is in the input stream
            int bytesAvailable = serialInputStream.available();
            if (bytesAvailable > 0) {
                // Reads the message
                int bytes = serialInputStream.read(readBuffer);
                String message = new String(readBuffer, 0, bytes);
                Log.i(TAG, "Read: \n" + message);
                //DevDataTransfer.parseResponse(message);
                if(!message.equals(""))
                {
                    Date date = Calendar.getInstance().getTime();
                    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
                    String timeString = new SimpleDateFormat("kk:mm:ss:SSS", Locale.US).format(date);
                    String fileData = dateString + ',' + timeString + "," + message;
//                    DataLogService.log(mContext, new File(GenerateDirectory.getRootFile(mContext), FILENAME), fileData, HEADER);
                }
            }
            else
                Log.i(TAG, "Input Stream clear. No message available");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------
     *              Intent Methods
     * -----------------------------------------------
     *
     * These methods are called from the app's activities. They start the
     * necessary Bluetooth operations in a background thread.
     *
     */
    public static void connect(Context context, String deviceAddress)
    {
        Log.d(TAG, "connect: Trying to connect....");
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(BluetoothAction.connect.toString());
        context.startService(intent);
    }
    public static void disconnect(Context context)
    {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.setAction(BluetoothAction.disconnect.toString());
        context.startService(intent);
    }
    public static void write(Context context, String data)
    {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.putExtra(EXTRA_DATA, data);
        intent.setAction(BluetoothAction.write.toString());
        context.startService(intent);
    }

    public static void read(Context context)
    {
        Intent intent = new Intent(context, BluetoothService.class);
        intent.setAction(BluetoothAction.read.toString());
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}
}
