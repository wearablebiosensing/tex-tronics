package andrewpeltier.smartglovefragments.tex_tronics;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import andrewpeltier.smartglovefragments.ble.BluetoothLeConnectionService;
import andrewpeltier.smartglovefragments.ble.GattCharacteristics;
import andrewpeltier.smartglovefragments.ble.GattDevices;
import andrewpeltier.smartglovefragments.ble.GattServices;
import andrewpeltier.smartglovefragments.database.UpdateData;
import andrewpeltier.smartglovefragments.database.UserRepository;
import andrewpeltier.smartglovefragments.fragments.patientfrags.DeviceExerciseFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.ExerciseInstructionFragment;
import andrewpeltier.smartglovefragments.fragments.patientfrags.SurveyFragment;
import andrewpeltier.smartglovefragments.io.IOUtil;
import andrewpeltier.smartglovefragments.io.SmartGloveInterface;
import andrewpeltier.smartglovefragments.main_activity.MainActivity;
import andrewpeltier.smartglovefragments.mqtt.MqttConnectionService;
import andrewpeltier.smartglovefragments.mqtt.MqttUpdateReceiver;
import andrewpeltier.smartglovefragments.mqtt.UpdateType;
import andrewpeltier.smartglovefragments.tex_tronics.devices.SmartGlove;
import andrewpeltier.smartglovefragments.tex_tronics.devices.TexTronicsDevice;
import andrewpeltier.smartglovefragments.tex_tronics.enums.Action;
import andrewpeltier.smartglovefragments.tex_tronics.enums.DeviceType;
import andrewpeltier.smartglovefragments.tex_tronics.enums.ExerciseMode;
import andrewpeltier.smartglovefragments.tex_tronics.exceptions.IllegalDeviceType;
import andrewpeltier.smartglovefragments.visualize.Choice;
import andrewpeltier.smartglovefragments.visualize.Exercise;

/** ======================================
 *
 *     TexTronicsManagerService Class
 *
 *  ======================================
 *
 *  Similar to how the Main Activity manages every fragment, the TexTronics Manager Service
 *  manages each connection service we use, being the BLE Connection Service and the MQTT Connection
 *  Service. The main components of this manager service are as follows:
 *
 *  -Static Intent Methods: Methods that can be called by all other components in the application so that they
 *  can interact with the manager
 *
 *  -Service Methods: Methods that invoke the use of a connection service, being the BLE or MQTT Connection Services
 *
 *  -Service Connection Classes: Classes that bind a particular service to this manager service
 *
 *  -Update Receivers: Updates this manager service whenever a broadcast is received, allowing it to react to changes
 *  in connection or receive collected data.
 *
 * @author Matthew Constant
 * @version 1.0, 02/28/2018
 */
/* NEW PUSH*/

public class TexTronicsManagerService extends Service
{
    /**
     * The tag used to log messages to the LogCat.
     *
     * @since 1.0
     */
    private static final String TAG = "TexTronics Service";

    /**
     * Used to identity the device address to connect to.
     *
     * @since 1.0
     */
    public static final String EXTRA_DEVICE = "tex_tronics.wbl.uri.ble.device";

    /**
     * Used to identify the transmit mode.
     *
     * @since 1.0
     */
    public static final String EXTRA_MODE = "tex_tronics.wbl.uri.ble.mode";

    /**
     * Used to identify the device type.
     *
     * @since 1.0
     */
    public static final String EXTRA_TYPE = "tex_tronics.wbl.uri.ble.type";
    /**
     * The packet ID for the first packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     *
     * @since 1.0
     */

    /**
     * The choice of exercise being done
     */
    public static final String EXTRA_CHOICE = "tex_tronics.wbl.uri.ble.choice";

    /**
     * The UUID of the current exercise
     */
    public static final String EXTRA_EX_ID = "tex_tronics.wbl.uri.ble.ex_id";

    /**
     * The UUID of the routine
     */
    public static final String EXTRA_ROUTINE_ID  = "tex_tronics.wbl.uri.ble.routine_id";

    public static final byte PACKET_ID_1 = 0x01;

    /**
     * The packet ID for the second packet transmitted when communicating in Flex+IMU mode.
     * This will be the first byte of the packet.
     *
     * @since 1.0
     */
    public static final byte PACKET_ID_2 = 0x02;

    /**
     * The value to return in onStartCommand
     *
     * https://developer.android.com/reference/android/app/Service.html
     *
     * @since 1.0
     */

    public static final int INTENT_RETURN_POLICY = START_STICKY;
    public static Context context;
    public static String deviceAddress;
    public static Choice choice;
    public static ExerciseMode exerciseMode;
    public static DeviceType deviceType;


    /** =====================================================
     *
     *                  Static Intent Methods
     *
     *  =====================================================
     */

    /** connect()
     *
     * This static method is provided for other components to use in order to interact with this
     * service. The connect method requests this service attempts to connect to the BLE device
     * with the given device address.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     * @param deviceAddress Device Address of BLE Device to connect to.
     *
     * @since 1.0
     */
    public static void connect_devices(Context context, String deviceAddress, Choice choice, ExerciseMode exerciseMode, DeviceType deviceType, UUID exerciseID, UUID routineID) {
        TexTronicsManagerService.context = context;
        TexTronicsManagerService.deviceAddress = deviceAddress;
        TexTronicsManagerService.choice = choice;
        TexTronicsManagerService.exerciseMode = exerciseMode;
        TexTronicsManagerService.deviceType = deviceType;
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.putExtra(EXTRA_MODE, exerciseMode);
        intent.putExtra(EXTRA_TYPE, deviceType);
        intent.putExtra(EXTRA_CHOICE, choice);
        intent.putExtra(EXTRA_EX_ID, exerciseID.toString());
        intent.putExtra(EXTRA_ROUTINE_ID, routineID.toString());
        intent.setAction(Action.connect.toString());
        context.startService(intent);
    }

    /** disconnect()
     *
     * This static method is provided for other components to use in order to interact with this
     * service. The disconnect method requests this service attempts to disconnect from the
     * previously connected BLE device with the given device address. This method will only work
     * if the given devicehas already been successfully connected to using the connect method
     * provided by this service.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     * @param deviceAddress Device Address of BLE Device to disconnect from.
     *
     * @since 1.0
     */
    public static void disconnect(Context context, String deviceAddress) {
        Log.v(TAG, "Disconnecting!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(Action.disconnect.toString());
        context.startService(intent);
    }



    public void create_datalog(String deviceAddress, String exerciseMode, DeviceType deviceType, Choice choice, String exerciseID, String routineID){
        SmartGlove smartGlove;

        // Only connects if we have a bounded BLE service, which we should if this service has started
        if (mBleServiceBound) {

            List<Integer> ident = new ArrayList<>();
            try {
                ident = UserRepository.getInstance(this.getApplicationContext()).getAllIdentities();
            } catch (Exception e) {
                Log.d(TAG, "onClick: Error with identities");
            }

            String exerciseName;
            //ExerciseMode exerciseMode1 = device.getExerciseMode().toString() ;

            // TODO Modify TexTronicsDevice to have static method to determine DeviceType to Use
            switch (deviceType) {
                case SMART_GLOVE:
                    //Log.e("Data log EXERCISE===", choice.toString());
                    if (MainActivity.exercise_name != null) {
                        exerciseName = MainActivity.exercise_name;
                        Log.e(" EXERCISE===", exerciseName);
                        Log.e(" FLAG===" , String.valueOf(ExerciseInstructionFragment.flag));
//
//                        String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
//                        // Find out the exercise mode (i.e what type of data we are collecting)
//                        TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
//                        ExerciseMode exerciseMode1 = null;
//                        if(device != null)
//                            exerciseMode1 = device.getExerciseMode();
                        // TODO Assume connection will be successful, if connection fails we must remove it from list.
                        smartGlove = new SmartGlove(ident.size(),exerciseName,3,deviceAddress, MainActivity.exercise_mode, choice, exerciseID, routineID);
                        mTexTronicsList.put(deviceAddress, smartGlove);
                    }

                    break;
                // Add Different Devices Here
//                case SMART_SOCK:
//
//                    if (MainActivity.exercise_name != null) {
//                        exerciseName = MainActivity.exercise_name;
//                        Log.e("Data log EXERCISE===", exerciseName);
//                        Log.e(" FLAG===" , String.valueOf(ExerciseInstructionFragment.flag));
//
//                        // TODO Assume connection will be successful, if connection fails we must remove it from list.
//                        smartGlove = new SmartGlove(ident.size(),exerciseName,3,deviceAddress, MainActivity.exercise_mode, choice, exerciseID, routineID);
//                        mTexTronicsList.put(deviceAddress, smartGlove);
//
//                    }
//                    // Added the Smart Sock code, just copied from above
//                    //smartGlove = new SmartGlove(ident.size(),choice.toString(), ExerciseInstructionFragment.flag,deviceAddress, exerciseMode, choice, exerciseID, routineID);
//                    //mTexTronicsList.put(deviceAddress, smartGlove);
//                    break;
                default:

                    break;
            }
        }

    }


    public void transmit_flags(){
        // Write to the txChar to notify the device
        BluetoothGattCharacteristic txChar = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.TX_CHARACTERISTIC);/**---------------------------------------------------------------------------------------------------------*/
        /**
         * added to sent 1 or 2 or 3 to the bluetooth device
         */
        if(ExerciseInstructionFragment.flag==1)
        {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x01});
            Log.d(TAG, "Data sent via flex");}
        else if(ExerciseInstructionFragment.flag==2)
        {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x02});
            Log.d(TAG, "Data sent via imu");}
        else if(ExerciseInstructionFragment.flag==3)
        {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x03});}

    }


    public static void publish(Context context, String deviceAddress)
    {
        Log.w(TAG, "publish: Publishing!!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.putExtra(EXTRA_DEVICE, deviceAddress);
        intent.setAction(Action.publish.toString());
        context.startService(intent);
    }

    /** start()
     *
     * This static method is provided for other components to use in order to interact with this
     * service. While the operation to be performed does not explicitly execute anything, it causes
     * this service to be created. All other
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     *
     * @since 1.0
     */
    public static void start(Context context)
    {
        Log.v(TAG, "Starting!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(Action.start.toString());
        context.startService(intent);
    }

    /** stop()
     *
     * This static method is provided for other components to use in order to interact with this
     * service. The stop method disconnects from all devices and tasks this service to stop itself
     * and its connected services.
     *
     * TODO Convert Context to WeakReference<Context>
     *
     * @param context Context of the calling component
     *
     * @since 1.0
     */
    public static void stop(Context context)
    {
        Log.v(TAG, "Stopping!!");
        Intent intent = new Intent(context, TexTronicsManagerService.class);
        intent.setAction(Action.stop.toString());
        context.startService(intent);
    }

    /**
     * ID for this service used for Notification builder
     */
    private final int SERVICE_ID = 111;
    /**
     * ID for this channel used for Notification builder
     */
    private final String CHANNEL_ID = "uri.wbl.tex_tronics.notification";

    /**
     * Used by inner classes to refer to this Service's Context. Weak Reference should not be needed
     * unless this Service implements multi-threading in future.
     */
    private Context mContext;                               // State of the component in the application
    private boolean mBleServiceBound = false;               // Whether or not we have a bounded BLE service
    private boolean mMqttServiceBound = false;              // Whether or not we have a bounded MQTT servcie
    private BluetoothLeConnectionService mBleService;       // A service object for BLE connections
    private MqttConnectionService mMqttService;             // A service object for MQTT connections
    private ServiceConnection mBleServiceConnection, mMqttServiceConnection; // Each service connection to be managed and bound

    /**
     * Contains reference to each connected Tex-Tronics Device.
     */
    private HashMap<String, TexTronicsDevice> mTexTronicsList;


    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "Service Created");

        // Create and build the notification to appear on the device's screen
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.presence_online)
                .setContentTitle("TexTronics Service")
                .setContentText("TexTronics is Active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(SERVICE_ID, notification);

        /* Store reference to this Service's context (good practice so that all methods/inner
         *  classes use a consistent Context object). This will need to be converted to a
         *  WeakReference object if multi-threading is implemented.
         *
         *  https://developer.android.com/reference/java/lang/ref/WeakReference.html
         *  https://community.oracle.com/blogs/enicholas/2006/05/04/understanding-weak-references
         *
         *  Possible Alternatives to WeakReference: https://medium.com/google-developer-experts/weakreference-in-android-dd1e66b9be9d
         */
        mContext = this;

        // Initialize the Connection Service to interface to BluetoothLeService
        mBleServiceConnection = new BleServiceConnection();
        // Initialize the Connection Service to interface to MQTT Service
        mMqttServiceConnection = new MqttServiceConnection();

        // Initialize Container for Tex-Tronic Connected Devices (set the initial capacity to 4 - 2 gloves, 2 socks)
        mTexTronicsList = new HashMap<>(4);

        // Register BLE Update Receiver to Receive Information back from BluetoothLeService
        registerReceiver(mBLEUpdateReceiver, new IntentFilter(BluetoothLeConnectionService.INTENT_FILTER_STRING));
        registerReceiver(mMqttUpdateReceiver, MqttUpdateReceiver.INTENT_FILTER);
        // Bind to BluetoothLeService. This Service provides the methods required to interact with BLE devices.
        bindService(new Intent(this, BluetoothLeConnectionService.class), mBleServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, MqttConnectionService.class), mMqttServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // Do not allow binding. All components should interact with this Service via the static methods provided.
        return null;
    }

    /** onStartCommand()
     *
     * Called whenever an intent is sent to this service. The action of the intent is gathered, after which
     * more information is retrieved from the intent based on its action and the required operation is executed.
     *
     * @param intent        -Operation to be performed. Also carries the data to perform required action
     * @param flags
     * @param startID
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID)
    {
        // Initial Check of Action Packet to make sure it contains the device address and Action
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Invalid Action Packet Received");
            return INTENT_RETURN_POLICY;
        }

        // Action to be performed on the TexTronics Device
        Action action = Action.getAction(intent.getAction());


        // String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
        // Find out the exercise mode (i.e what type of data we are collecting)
        TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
//        ExerciseMode exerciseMode1 = null;
//        if(device != null)
//            exerciseMode1 = device.getExerciseMode();

        // Make sure it is a valid Action
        if(action == null) {
            Log.w(TAG, "Invalid Action Packet Received");
            return INTENT_RETURN_POLICY;
        }

        String exerciseID = (String) intent.getSerializableExtra(EXTRA_EX_ID);
        String routineID = (String) intent.getSerializableExtra(EXTRA_ROUTINE_ID);


        // Device Address of the BLE Device corresponding to this Action Packet
        String deviceAddress = intent.getStringExtra(EXTRA_DEVICE);


        // Execute Action Packet (this can be done with multi-threading to be able to Service multiple Action Packets at once)
        switch (action)
        {
            case connect:
                {
                // Attempt to connect to BLE Device (Device Type and Transmitting Mode should be obtained during scan)
                if (!intent.hasExtra(EXTRA_TYPE) || !intent.hasExtra(EXTRA_MODE))
                {
                    Log.w(TAG, "Invalid connect Action Packet Received");
                    return INTENT_RETURN_POLICY;
                }
                // Get all connection info from the intent
                ExerciseMode exerciseMode = (ExerciseMode) intent.getSerializableExtra(EXTRA_MODE);
                DeviceType deviceType = (DeviceType) intent.getSerializableExtra(EXTRA_TYPE);
                Choice choice = (Choice) intent.getSerializableExtra(EXTRA_CHOICE);

                // Use data to connect to device
                connect(deviceAddress, exerciseMode, deviceType, choice, exerciseID, routineID);
                //transmit_flags();
                create_datalog(deviceAddress, MainActivity.exercise_mode, deviceType, choice, exerciseID, routineID);
                publish(deviceAddress);
                    //
                }
            break;
            case disconnect:
                // Attempt to disconnect from a currently connected BLE Device
                disconnect(deviceAddress);
                break;
            case publish:
                publish(deviceAddress);

                //connect(deviceAddress, exerciseMode, deviceType, choice, exerciseID, routineID);
                create_datalog(deviceAddress, MainActivity.exercise_mode, deviceType, choice, exerciseID, routineID);

                //     transmit_flags();


                break;
            case stop:
                // Disconnect from each device, then stop services
                if(MainActivity.getmDeviceAddressList() != null &&
                        MainActivity.getmDeviceAddressList().length != 0)
                {
                    for(String address : MainActivity.getmDeviceAddressList())
                        disconnect(address);
                }
                stopSelf();
        }

        return INTENT_RETURN_POLICY;
    }

    /** onDestroy()
     *
     * Called when the application gets killed. All receivers are unregistered, and all connection
     * services are unbound to this before it is destroyed.
     *
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mBLEUpdateReceiver);
        unregisterReceiver(mMqttUpdateReceiver);
        unbindService(mBleServiceConnection);
        unbindService(mMqttServiceConnection);

        Log.d(TAG,"Manager Service Destroyed");

        super.onDestroy();
    }



    /** =====================================================
     *
     *                  Service Methods
     *
     *  =====================================================
     */


    /** connect()
     *
     * Puts the input device into the device list then uses the BLE connection service to connect to it. Input
     * parameters are essentially all of the information needed to create a SmartGlove object so that we can
     * publish the data we collect from this device to the MQTT server.
     *
     * @param deviceAddress         -MAC address of connected device
     * @param exerciseMode          -Type of data that can be collected
     * @param deviceType            -Type of device, either glove or sock
     * @param choice                -Choice of exercise
     * @param exerciseID            -ID of exercise
     * @param routineID             -ID of exercise routine
     */
    private void connect(String deviceAddress, ExerciseMode exerciseMode, DeviceType deviceType, Choice choice, String exerciseID, String routineID)
    {
        //SmartGlove smartGlove;

        // Only connects if we have a bounded BLE service, which we should if this service has started
        if (mBleServiceBound)
        {
//
//            List<Integer> ident = new ArrayList<>();
//            try {
//                ident  = UserRepository.getInstance(this.getApplicationContext()).getAllIdentities();
//            }
//            catch(Exception e){
//                Log.d(TAG, "onClick: Error with identities");
//            }
//
//           String exerciseName;
//
//            // TODO Modify TexTronicsDevice to have static method to determine DeviceType to Use
//            switch (deviceType)
//            {
//                case SMART_GLOVE:
//                    Log.e("Data log EXERCISE===", choice.toString());
//                    if(MainActivity.exercise_name != null) {
//                        exerciseName = MainActivity.exercise_name;
//                        Log.e("Data log EXERCISE===", exerciseName);
//
//                       // TODO Assume connection will be successful, if connection fails we must remove it from list.
//                        smartGlove = new SmartGlove(ident.size(),exerciseName, ExerciseInstructionFragment.flag,deviceAddress, exerciseMode, choice, exerciseID, routineID);
//                        mTexTronicsList.put(deviceAddress, smartGlove);
//
//                    }
//
//                    break;
//                // Add Different Devices Here
//                case SMART_SOCK:
//
//                    if(MainActivity.exercise_name != null) {
//                        exerciseName = MainActivity.exercise_name;
//                        Log.e("Data log EXERCISE===", exerciseName);
//
//                        // TODO Assume connection will be successful, if connection fails we must remove it from list.
//                         smartGlove = new SmartGlove(ident.size(),exerciseName, ExerciseInstructionFragment.flag,deviceAddress, exerciseMode, choice, exerciseID, routineID);
//                        mTexTronicsList.put(deviceAddress, smartGlove);
//
//                    }
//                    // Added the Smart Sock code, just copied from above
//                    //smartGlove = new SmartGlove(ident.size(),choice.toString(), ExerciseInstructionFragment.flag,deviceAddress, exerciseMode, choice, exerciseID, routineID);
//                    //mTexTronicsList.put(deviceAddress, smartGlove);
//                   break;
//                default:
//
//                    break;
//            }

            mBleService.connect(deviceAddress); //add_connected
        }
        else {
            Log.w(TAG,"Cannot Connect - BLE Connection Service is not bound yet!");
        }
    }

    /** disconnect()
     *
     * Calls the BLE service to disconnect the input device
     *
     * @param deviceAddress     MAC address of the device to disconnect from
     */
    private void disconnect(String deviceAddress)
    {
        if (mBleServiceBound)
        {
            mBleService.disconnect(deviceAddress);

        } else {
            Log.w(TAG,"Could not Disconnect - BLE Connection Service is not bound!");
        }
    }

    /** publish()
     *
     * Calls the MQTT service to publish the data from the input device's exercise.
     *
     * @param deviceAddress    MAC address of the device to publish
     */

    private void publish(String deviceAddress)
    {
        if(mMqttServiceBound)
        {
            try
            {
                TexTronicsDevice device = mTexTronicsList.get(deviceAddress);
                if(device != null)
                {
                    // Get the byte array from the CSV file stored in the device
                    byte[] buffer = IOUtil.readFile(device.getCsvFile());
                    // Create a new string using device info and new byte array
                    String json = MqttConnectionService.generateJson(device.getDate(),
                            device.getDeviceAddress(),
                            Choice.toString(device.getChoice()),
                            device.getExerciseID(),
                            device.getRoutineID(),
                            new String(buffer));
                    Log.d("SmartGlove", "JSON: " + json);
                    // Call the service to publish this string, now in JSON format
                    mMqttService.publishMessage(json);
                    String exe_nm = MainActivity.exercise_name;

                    List<Integer> ids = new ArrayList<>();
                    try{
                        ids = UserRepository.getInstance(getApplicationContext()).getAllIdentities();
                    }
                    catch (Exception e){
                        Log.e(TAG, "onCreate: ", e);
                    }

                    int current = ids.size();

                    Log.d(TAG, "UpdateData: logging the data");

                    if (exe_nm.equals("Finger_Tap") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_finTap_left(json,current);
                    }
                    else if (exe_nm.equals("Closed_Grip") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_opCl_left(json,current);
                    }
                    else if (exe_nm.equals("Hand_Flip") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_h_flip_left(json,current);
                    }
                    else if (exe_nm.equals("Finger_to_Nose") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_finNose_left(json,current);
                    }
                    else if (exe_nm.equals("Hold_Hands_Out") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_handout_left(json,current);
                    }
                    else if (exe_nm.equals("Resting_Hands_on_Thighs") && deviceAddress.equals("LEFT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_handrest_left(json,current);
                    }
                    else if (exe_nm.equals("Finger_Tap") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_finTap_right(json,current);
                    }
                    else if (exe_nm.equals("Closed_Grip") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_opCl_right(json,current);
                    }
                    else if (exe_nm.equals("Hand_Flip") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_h_flip_right(json,current);
                    }
                    else if (exe_nm.equals("Finger_to_Nose") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_finNose_right(json,current);
                    }
                    else if (exe_nm.equals("Hold_Hands_Out") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_handout_right(json,current);
                    }
                    else if (exe_nm.equals("Resting_Hands_on_Thighs") && deviceAddress.equals("RIGHT_GLOVE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_handrest_right(json,current);
                    }
                    else if (exe_nm.equals("Heel_Stomp") && deviceAddress.equals("RIGHT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_heelStmp_right(json,current);
                    }
                    else if (exe_nm.equals("Toe_Tap") && deviceAddress.equals("RIGHT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_toeTap_right(json,current);
                    }
                    else if (exe_nm.equals("Walk_Steps") && deviceAddress.equals("RIGHT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_gait_right(json,current);
                    }
                    else if (exe_nm.equals("Heel_Stomp") && deviceAddress.equals("LEFT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_heelStmp_left(json,current);
                    }
                    else if (exe_nm.equals("Toe_Tap") && deviceAddress.equals("LEFT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_toeTap_left(json,current);
                    }
                    else if (exe_nm.equals("Walk_Steps") && deviceAddress.equals("LEFT_SHOE_ADDR"))
                    {
                        UserRepository.getInstance(context).updateData_gait_left(json,current);
                    }
                }
                else {
                    Log.d(TAG, "publish: Publish failed. Device is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** =====================================================
     *
     *              Service Connection Classes
     *
     *  =====================================================
     */

    // Binds BLE service to the manager
    private class BleServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mBleServiceBound = true;
            mBleService = ((BluetoothLeConnectionService.BLEConnectionBinder) iBinder).getService();
            TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.started);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleServiceBound = false;
        }
    }

    // Binds MQTT service to the manager
    private class MqttServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            mMqttServiceBound = true;
            mMqttService = ((MqttConnectionService.MqttConnectionBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mMqttServiceBound = false;
        }
    }

    /** =====================================================
     *
     *                   Update Receivers
     *
     *  =====================================================
     */

    private BroadcastReceiver mBLEUpdateReceiver = new BroadcastReceiver()
    {
        /** onReceive()
         *
         * Called whenever an update occurs in our connected BLE device(s). Because this is a part
         * of the manager service, we want to keep track of every type of update, since this gives us
         * information on the connectivity and transferal of data between our Android device and the
         * Smart Glove device.
         *
         * @param context           -State of the application
         * @param intent            -Operation containing type of update and the data that comes with it
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Received BLE Update");
            // Get the MAC address and action from the intent
            String deviceAddress = intent.getStringExtra(BluetoothLeConnectionService.INTENT_DEVICE);
            String action = intent.getStringExtra(BluetoothLeConnectionService.INTENT_EXTRA);

            Log.d(TAG, "Received BLE Update ACTION====="+ action);

            /*
             * The action holds what type of update we are dealing with. For each action, we
             * need to update the TT update receiver.
             */
            switch (action)
            {
                case BluetoothLeConnectionService.GATT_STATE_CONNECTING:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_connecting);
                    break;
                case BluetoothLeConnectionService.GATT_STATE_CONNECTED:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_connected);
                    // Once connected, we need to discover the services to find our characteristics
                    mBleService.discoverServices(deviceAddress);

                    MainActivity.CONNECTED = true;
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTING:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_disconnecting);

                    // Before we disconnect, we need to publish any exercise data. Get the disconnecting device
                    TexTronicsDevice disconnectingDevice = mTexTronicsList.get(deviceAddress);

                    //TODO: DEBUG ME!!!!!

                    // Send to Server via MQTT
                    if(mMqttServiceBound && DeviceExerciseFragment.START_LOG)
                    {
                        try
                        {
                            byte[] buffer = IOUtil.readFile(disconnectingDevice.getCsvFile());
                            String json = MqttConnectionService.generateJson(disconnectingDevice.getDate(),
                                    disconnectingDevice.getDeviceAddress(),
                                    Choice.toString(disconnectingDevice.getChoice()) ,
                                    disconnectingDevice.getExerciseID(),
                                    disconnectingDevice.getRoutineID(),
                                    new String(buffer));
                            Log.d("SmartGlove", "JSON: " + json);
                            mMqttService.publishMessage(json);
                            DeviceExerciseFragment.START_LOG = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BluetoothLeConnectionService.GATT_STATE_DISCONNECTED:
                    TexTronicsUpdateReceiver.update(mContext, deviceAddress, TexTronicsUpdate.ble_disconnected);

                    // It has now been disconnected and we've published the data, now we remove the device from our list
                    TexTronicsDevice disconnectDevice = mTexTronicsList.get(deviceAddress);
                    if(disconnectDevice == null) {
                        Log.w(TAG, "Device not Found");
                        return;
                    }
                    mTexTronicsList.remove(deviceAddress);

                    MainActivity.CONNECTED = false;

                    break;
                case BluetoothLeConnectionService.GATT_DISCOVERED_SERVICES:
                    // Enable notifications on our RX characteristic which sends our data packets

//                    if(deviceAddress.equals(GattDevices.LEFT_GLOVE_ADDR)){
//                        BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE2, GattCharacteristics.RX_CHARACTERISTIC2);
//                        if (characteristic != null) {
//                            mBleService.enableNotifications(deviceAddress, characteristic);
//                        }
//                    }
//                    if(deviceAddress.equals(GattDevices.RIGHT_GLOVE_ADDR)||deviceAddress.equals(GattDevices.LEFT_SHOE_ADDR)||deviceAddress.equals(GattDevices.RIGHT_SHOE_ADDR)) {
                        BluetoothGattCharacteristic characteristic = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.RX_CHARACTERISTIC);
                        if (characteristic != null) {
                            mBleService.enableNotifications(deviceAddress, characteristic);
                        }
//                    }


//                    // Write to the txChar to notify the device
//                    BluetoothGattCharacteristic txChar = mBleService.getCharacteristic(deviceAddress, GattServices.UART_SERVICE, GattCharacteristics.TX_CHARACTERISTIC);/**---------------------------------------------------------------------------------------------------------*/
//                    /**
//                     * added to sent 1 or 2 or 3 to the bluetooth device
//                     */
//                    if(ExerciseInstructionFragment.flag==1)
//                    {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x01});
//                        Log.d(TAG, "Data sent via flex");}
//                    else if(ExerciseInstructionFragment.flag==2)
//                    {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x02});
//                        Log.d(TAG, "Data sent via imu");}
//                    else if(ExerciseInstructionFragment.flag==3)
//                    {mBleService.writeCharacteristic(deviceAddress, txChar, new byte[] {0x03});}
//
//                    /*-----------------------------------------------------------------------------------------------------------------------------------------*/

                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_NOTIFY:
                    /* Called whenever the value of our RX characteristic is changed, which
                     * is equal to the sampling rate of our connected device.
                     */
                    UUID characterUUID = UUID.fromString(intent.getStringExtra(BluetoothLeConnectionService.INTENT_CHARACTERISTIC));
                    if(characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC)||characterUUID.equals(GattCharacteristics.RX_CHARACTERISTIC2))
                    {
                        // Get the data packet
                        Log.d(TAG, "Data Received");
                        byte[] data = intent.getByteArrayExtra(BluetoothLeConnectionService.INTENT_DATA);

                        // Find out the exercise mode (i.e what type of data we are collecting)
                        TexTronicsDevice device = mTexTronicsList.get(deviceAddress);


                        //String exerciseMode = null;
                        String exerciseMode = null;
                        if(device != null)
                            //exerciseMode1 = device.getExerciseMode();
                            exerciseMode = MainActivity.exercise_mode;

                        if(exerciseMode != null)
                        {
                            try
                            {
                                /** alli edit - how the csv files is logged */
                              /*  if(exerciseMode.equals("Flex + Imu")){
                                    device.setThumbFlex((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));
                                    device.setIndexFlex((((data[2] & 0x00FF) << 8) | ((data[3] & 0x00FF))));
                                    device.setAccX(((data[4] & 0x00FF) << 8) | ((data[5] & 0x00FF)));
                                    device.setAccY(((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF)));
                                    device.setAccZ(((data[8] & 0x00FF) << 8) | ((data[9] & 0x00FF)));
                                    device.setGyrX(((data[10] & 0x00FF) << 8) | ((data[11] & 0x00FF)));
                                    device.setGyrY(((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF)));
                                    device.setGyrZ(((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF)));

                                    Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));

                                    // If in exercise, log this data to CSV file
                                    if(DeviceExerciseFragment.START_LOG){
                                        device.logData(mContext);
                                    } else {
                                        Log.w(TAG, "Invalid Data Packet");
                                        return;
                                    }
                                }*/
//                                else if(exerciseMode.equals("Flex Only")){
//                                    Log.e("MODE:::--" ,exerciseMode);
//                                    device.setThumbFlex((((data[1] & 0x00FF) << 8) | ((data[0] & 0x00FF))));
//                                    device.setIndexFlex((((data[3] & 0x00FF) << 8) | ((data[2] & 0x00FF))));
//                                    device.setMiddleFlex((((data[5] & 0x00FF) << 8) | ((data[4] & 0x00FF))));
//                                    device.setRingFlex((((data[7] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
//                                    device.setPinkyFlex((((data[9] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
//
//                                    Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));
//
//                                    if(DeviceExerciseFragment.START_LOG)
//                                        device.logData(mContext);
//
//
//                                    // Second Data Set
//                                    //device.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
//                                    device.setThumbFlex((((data[11] & 0x00FF) << 8) | ((data[10] & 0x00FF))));
//                                    device.setIndexFlex((((data[13] & 0x00FF) << 8) | ((data[12] & 0x00FF))));
//                                    device.setMiddleFlex((((data[15] & 0x00FF) << 8) | ((data[14] & 0x00FF))));
//                                    device.setRingFlex((((data[17] & 0x00FF) << 8) | ((data[16] & 0x00FF))));
//                                    device.setPinkyFlex((((data[19] & 0x00FF) << 8) | ((data[18] & 0x00FF))));
//                                    Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));
//
//                                    if(DeviceExerciseFragment.START_LOG)
//                                        device.logData(mContext);
//                                }
//                                else if(exerciseMode.equals("Imu Only")){
//                                    Log.e("MODE:::--" ,exerciseMode);
//                                    device.setAccX(((data[1] & 0x00FF) << 8) | ((data[0] & 0x00FF)));
//                                    device.setAccY(((data[3] & 0x00FF) << 8) | ((data[2] & 0x00FF)));
//                                    device.setAccZ(((data[5] & 0x00FF) << 8) | ((data[4] & 0x00FF)));
//                                    device.setGyrX(((data[7] & 0x00FF) << 8) | ((data[6] & 0x00FF)));
//                                    device.setGyrY(((data[9] & 0x00FF) << 8) | ((data[8] & 0x00FF)));
//                                    device.setGyrZ(((data[11] & 0x00FF) << 8) | ((data[10] & 0x00FF)));
//
//                                    Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));
//
//                                    if(DeviceExerciseFragment.START_LOG)
//                                        device.logData(mContext);
//
                                //}

                                Log.e("MODE",MainActivity.exercise_mode);
                                Log.e("MODE:::--" ,device.getExerciseMode().toString());

                                switch (exerciseMode)
                                {
                                    case "Flex + IMU":
                                        // Move data processing into Data Model?
//                                        //if (data[0] == PACKET_ID_1) {

                                            device.setTimestamp(((data[1] & 0x00FF) << 8) | ((data[0] & 0x00FF)));// | ((data[3] & 0x00FF) << 8) | (data[4] & 0x00FF));
                                            device.setThumbFlex((((data[3] & 0x00FF) << 8) | ((data[2] & 0x00FF))));
                                            device.setIndexFlex((((data[5] & 0x00FF) << 8) | ((data[4] & 0x00FF))));
                                            device.setRingFlex((((data[7] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                            device.setAccX((short)(((data[9] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
                                            device.setAccY((short)(((data[11] & 0x00FF) << 8) | ((data[10] & 0x00FF))));
                                            device.setAccZ((short)(((data[13] & 0x00FF) << 8) | ((data[12] & 0x00FF))));
                                            device.setGyrX((short)(((data[15] & 0x00FF) << 8) | ((data[14] & 0x00FF))));
                                            device.setGyrY((short)(((data[17] & 0x00FF) << 8) | ((data[16] & 0x00FF))));
                                            device.setGyrZ((short)(((data[19] & 0x00FF) << 8) | ((data[18] & 0x00FF))));



                                            Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));

                                            // If in exercise, log this data to CSV file
                                            if(DeviceExerciseFragment.START_LOG){
                                                    device.logData(mContext);
                                            } else {
                                                Log.w(TAG, "Invalid Data Packet");
                                                return;
                                            }
                                            break;
                                    case "Flex Only":
                                        // First Data Set
                                        //device.setTimestamp((((data[0] & 0x00FF) << 8) | ((data[1] & 0x00FF))));

                                        device.setThumbFlex((((data[1] & 0x00FF) << 8) | ((data[0] & 0x00FF))));
                                        device.setIndexFlex((((data[3] & 0x00FF) << 8) | ((data[2] & 0x00FF))));
                                        device.setMiddleFlex((((data[5] & 0x00FF) << 8) | ((data[4] & 0x00FF))));
                                        device.setRingFlex((((data[7] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                        device.setPinkyFlex((((data[9] & 0x00FF) << 8) | ((data[8] & 0x00FF))));

                                        Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));

                                        if(DeviceExerciseFragment.START_LOG)
                                            device.logData(mContext);


                                        // Second Data Set
                                        //device.setTimestamp((((data[6] & 0x00FF) << 8) | ((data[7] & 0x00FF))));
                                        device.setThumbFlex((((data[11] & 0x00FF) << 8) | ((data[10] & 0x00FF))));
                                        device.setIndexFlex((((data[13] & 0x00FF) << 8) | ((data[12] & 0x00FF))));
                                        device.setMiddleFlex((((data[15] & 0x00FF) << 8) | ((data[14] & 0x00FF))));
                                        device.setRingFlex((((data[17] & 0x00FF) << 8) | ((data[16] & 0x00FF))));
                                        device.setPinkyFlex((((data[19] & 0x00FF) << 8) | ((data[18] & 0x00FF))));
                                            Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));

                                        if(DeviceExerciseFragment.START_LOG)
                                            device.logData(mContext);
                                        break;
                                        // Third Data Set
                                        //device.setTimestamp((((data[12] & 0x00FF) << 8) | ((data[13] & 0x00FF))));
                                        //device.setThumbFlex((((data[14] & 0x00FF) << 8) | ((data[15] & 0x00FF))));
                                        //device.setIndexFlex((((data[16] & 0x00FF) << 8) | ((data[17] & 0x00FF))));

                                        // If in exercise, log data to CSV
                                        //if(DeviceExerciseFragment.START_LOG)
                                         //   device.logData(mContext);

                                    case "Imu Only":
                                        device.setAccX((short) (((data[1] & 0x00FF) << 8) | ((data[0] & 0x00FF))));
                                        device.setAccY(( short)(((data[3] & 0x00FF) << 8) | ((data[2] & 0x00FF))));
                                        device.setAccZ((short)(((data[5] & 0x00FF) << 8) | ((data[4] & 0x00FF))));
                                        device.setGyrX(( short)(((data[7] & 0x00FF) << 8) | ((data[6] & 0x00FF))));
                                        device.setGyrY(( short)(((data[9] & 0x00FF) << 8) | ((data[8] & 0x00FF))));
                                        device.setGyrZ(( short)(((data[11] & 0x00FF) << 8) | ((data[10] & 0x00FF))));

                                        Log.d("START_LOG:::--" ,String.valueOf(DeviceExerciseFragment.START_LOG));

                                        if(DeviceExerciseFragment.START_LOG)
                                            device.logData(mContext);
                                        break;
                                }
                            } catch (IllegalDeviceType | IOException e) {
                                Log.e(TAG, e.toString());
                                // TODO Handle Error Event
                                return;
                            }
                        }
                    }
                    break;
                case BluetoothLeConnectionService.GATT_CHARACTERISTIC_READ:
                    break;
                case BluetoothLeConnectionService.GATT_DESCRIPTOR_WRITE:
                    break;
                case BluetoothLeConnectionService.GATT_NOTIFICATION_TOGGLED:
                    break;
                case BluetoothLeConnectionService.GATT_DEVICE_INFO_READ:
                    break;
            }
        }
    };

    private MqttUpdateReceiver mMqttUpdateReceiver = new MqttUpdateReceiver()
    {
        /** onReceive()
         *
         * Called whenever a change in connection happens. For each change, we simply send an update to
         * the TexTronics update receiver.
         *
         * @param context           -State of the application
         * @param intent            -Operation to be performed, which holds the update type
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Received MQTT Update");
            if(!intent.hasExtra(UPDATE_TYPE)) {
                Log.w(TAG, "Update is empty");
                return;
            }

            UpdateType updateType = (UpdateType)intent.getSerializableExtra(UPDATE_TYPE);
            switch (updateType) {
                case connected:
                    Log.d(TAG, "MQTT Connected");
                    TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.mqtt_connected);
                    break;
                case disconnected:
                    Log.d(TAG, "MQTT Disconnected");
                    TexTronicsUpdateReceiver.update(mContext, null, TexTronicsUpdate.mqtt_disconnected);
                    break;
                default:
                    Log.d(TAG, "Unknown MQTT Update");
                    break;
            }
        }
    };
}
