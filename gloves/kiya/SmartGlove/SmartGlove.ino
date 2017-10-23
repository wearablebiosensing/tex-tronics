#include <BLE_API.h>
#include "SmartGlove.h"
#include "SFE_LSM9DS0_SG.h"

BLE ble;                                                // BLE Module
LSM9DS0 dof(MODE_I2C, LSM9DS0_G, LSM9DS0_XM);           // IMU Module
Ticker ticker_task1;                                    // Timer for Periodic Callback (used instead of delay in loop)
uint8_t setup_successful;

// List of Services Available, used in Advertising Data to display Services
static const uint16_t uuid16_list[] = {
  UUID_IMU_SERVICE, 
  UUID_SMART_SERVICE
};

// Create IMU Service and Characteristics
static imu_data_t acc_x_data, acc_y_data, acc_z_data;
static uint8_t acc_data[13] = {
  0x00, 
  acc_x_data.b[3], acc_x_data.b[2], acc_x_data.b[1], acc_x_data.b[0], 
  acc_y_data.b[3], acc_y_data.b[2], acc_y_data.b[1], acc_y_data.b[0], 
  acc_z_data.b[3], acc_z_data.b[2], acc_z_data.b[1], acc_z_data.b[0]
};

static imu_data_t gyro_x_data, gyro_y_data, gyro_z_data;
static uint8_t gyro_data[13] = {
  0x00, 
  gyro_x_data.b[3], gyro_x_data.b[2], gyro_x_data.b[1], gyro_x_data.b[0], 
  gyro_y_data.b[3], gyro_y_data.b[2], gyro_y_data.b[1], gyro_y_data.b[0], 
  gyro_z_data.b[3], gyro_z_data.b[2], gyro_z_data.b[1], gyro_z_data.b[0]
};

static imu_data_t mag_x_data, mag_y_data, mag_z_data;
static uint8_t mag_data[13] = {
  0x00, 
  mag_x_data.b[3], mag_x_data.b[2], mag_x_data.b[1], mag_x_data.b[0], 
  mag_y_data.b[3], mag_y_data.b[2], mag_y_data.b[1], mag_y_data.b[0], 
  mag_z_data.b[3], mag_z_data.b[2], mag_z_data.b[1], mag_z_data.b[0]
};

GattCharacteristic acc_char(UUID_ACCEL_CHAR, acc_data, sizeof(acc_data), sizeof(acc_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic gyro_char(UUID_GYRO_CHAR, gyro_data, sizeof(gyro_data), sizeof(gyro_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic mag_char(UUID_MAG_CHAR, mag_data, sizeof(mag_data), sizeof(mag_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic *imu_chars[] = {&acc_char, &gyro_char, &mag_char, };
GattService        imu_service(UUID_IMU_SERVICE, imu_chars, sizeof(imu_chars) / sizeof(GattCharacteristic *));

// Create Smart Service and Characteristics
uint8_t cnt;
static uint8_t counter[2] = {0x00, cnt};
GattCharacteristic data_ready_char(UUID_DATA_READY_CHAR, counter, sizeof(counter), sizeof(counter), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);
GattCharacteristic *smart_chars[] = {&data_ready_char, };
GattService        smart_service(UUID_SMART_SERVICE, smart_chars, sizeof(smart_chars) / sizeof(GattCharacteristic *));


/**
 * This callback is invoked every time the device
 * is disconnected from. For now, just start
 * advertising again.
 */
void disconnectionCallBack(const Gap::DisconnectionCallbackParams_t *params) {
  Serial.println("Disconnected!");
  Serial.println("Restarting the advertising process");
  ble.startAdvertising();
}

/**
 * This callback is invoked every DATA_REFRESH_MS
 * milliseconds. This value can be changed in
 * SmartGlove.h. Currently, we use a Ticker to
 * invoke this callback, however the Ticker class
 * does not support microseconds. We may change to
 * using the TaskScheduler class in the future
 * as this is much more precise (millisecond
 * resolution when using sleep_on_idle).
 */
void periodicCallback() {
  if (ble.getGapState().connected && setup_successful) {
    /*
     * TODO:
     *  Currently, IMU data is just dummy data. We need to retreive the data
     *  from the IMU here and update the corresponding fields according to 
     *  the data retreived. Also, this is probably a very inefficient way
     *  of updating the characteristic, so it would be good to optimize this
     *  at some point.
     */
    dof.readAccel();
    dof.readGyro();
    dof.readMag();
    
    acc_x_data.f = dof.ax;
    acc_y_data.f = dof.ay;
    acc_z_data.f = dof.az;
    
    gyro_x_data.f = dof.gx;
    gyro_y_data.f = dof.gy;
    gyro_z_data.f = dof.gz;
    
    mag_x_data.f = dof.mx;
    mag_y_data.f = dof.my;
    mag_z_data.f = dof.mz;

    // These arrays are created solely to pass to the updateCharacteristicValue
    //  function. This could probably be optimized somehow.
    /*acc_data[0] = 0x00;
    acc_data[1] = acc_x_data.b[3];
    acc_data[2] = acc_x_data.b[2];
    acc_data[3] = acc_x_data.b[1];
    acc_data[4] = acc_x_data.b[0];
    acc_data[5] = acc_y_data.b[3];
    acc_data[6] = acc_y_data.b[2];
    acc_data[7] = acc_y_data.b[1];
    acc_data[8] = acc_y_data.b[0];
    acc_data[9] = acc_z_data.b[3];
    acc_data[10] = acc_z_data.b[2];
    acc_data[11] = acc_z_data.b[1];
    acc_data[12] = acc_z_data.b[0];*/
    
    uint8_t temp_acc_data[13] = {
      0x00, 
      acc_x_data.b[3], acc_x_data.b[2], acc_x_data.b[1], acc_x_data.b[0], 
      acc_y_data.b[3], acc_y_data.b[2], acc_y_data.b[1], acc_y_data.b[0], 
      acc_z_data.b[3], acc_z_data.b[2], acc_z_data.b[1], acc_z_data.b[0]
    };
    uint8_t temp_gyro_data[13] = {
      0x00, 
      gyro_x_data.b[3], gyro_x_data.b[2], gyro_x_data.b[1], gyro_x_data.b[0], 
      gyro_y_data.b[3], gyro_y_data.b[2], gyro_y_data.b[1], gyro_y_data.b[0], 
      gyro_z_data.b[3], gyro_z_data.b[2], gyro_z_data.b[1], gyro_z_data.b[0]
    };
    uint8_t temp_mag_data[13] = {
      0x00, 
      mag_x_data.b[3], mag_x_data.b[2], mag_x_data.b[1], mag_x_data.b[0], 
      mag_y_data.b[3], mag_y_data.b[2], mag_y_data.b[1], mag_y_data.b[0], 
      mag_z_data.b[3], mag_z_data.b[2], mag_z_data.b[1], mag_z_data.b[0]
    };

    // Update Characteristic values
    ble.updateCharacteristicValue(acc_char.getValueAttribute().getHandle(), temp_acc_data, sizeof(temp_acc_data));
    ble.updateCharacteristicValue(gyro_char.getValueAttribute().getHandle(), temp_gyro_data, sizeof(temp_gyro_data));
    ble.updateCharacteristicValue(mag_char.getValueAttribute().getHandle(), temp_mag_data, sizeof(temp_mag_data));

    // This Characteristic is used to notify the Client that all the data is
    //  ready to be read. This is safer than each data characteristic notifying
    //  the Client individually since the Client could mismatch some data points.
    counter[1] = ++cnt;
    ble.updateCharacteristicValue(data_ready_char.getValueAttribute().getHandle(), counter, sizeof(counter));
  } else {
    // If the IMU is not connected properly, data ready will always have a value of 0 (no increment)
    counter[1] = cnt;
    ble.updateCharacteristicValue(data_ready_char.getValueAttribute().getHandle(), counter, sizeof(counter));
  }
}

/**
 * This function initializes the BLE Module. It sets the
 * advertising data and then puts the Peripheral in advertising
 * mode.
 * 
 * TODO: Set the scan response data?
 */
void init_ble() {
  ble.init();
  ble.onDisconnection(disconnectionCallBack);

  // Set the Advertising Data
  ble.accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE);
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t*)uuid16_list, sizeof(uuid16_list));
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)BLE_DEVICE_NAME, sizeof(BLE_DEVICE_NAME));
  ble.setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
  // Add the Services to the Peripheral
  ble.addService(imu_service);
  ble.addService(smart_service);
  // Set the Local Device Name
  ble.setDeviceName((const uint8_t *)BLE_DEVICE_NAME);
  // Set Tx Power
  ble.setTxPower(4);
  // Set Advertising Interval (multiples of 0.625ms)
  ble.setAdvertisingInterval(160);
  // Aet Advertising Timeout (seconds)
  ble.setAdvertisingTimeout(0);
  // Begin Advertising
  ble.startAdvertising();
}

void init_imu() {
  uint16_t status;
  if( (status = dof.begin()) != IMU_CHECK_KEY) {
    // IMU Module not wired properly!
    // TODO: Handle this error somehow
    setup_successful = 0;
  } else {
    setup_successful = 1;
  }

  Serial.print("IMU Status: ");
  Serial.println(status);
}

/**
 * The setup function initializes the timer to allow
 * for the periodic callback function to be invoked.
 * It also initializes the BLE module to start 
 * advertising.
 */
void setup() {
  Serial.begin(9600);
  Serial.println("Initializing SmartGlove...");
  ticker_task1.attach(periodicCallback, 1);       // Initialize Timer
  init_ble();                                     // Initialize BLE Module
  init_imu();                                     // Initialize IMU Module
}

void loop() {
  ble.waitForEvent();
}

