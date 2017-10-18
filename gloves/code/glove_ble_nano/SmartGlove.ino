#include <BLE_API.h>
#include "SmartGlove.h"

BLE ble;              // BLE Module
Ticker ticker_task1;  // Timer for Periodic Callback (used instead of delay in loop)

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
  if (ble.getGapState().connected) {
    /*
     * TODO:
     *  Currently, IMU data is just dummy data. We need to retreive the data
     *  from the IMU here and update the corresponding fields according to 
     *  the data retreived. Also, this is probably a very inefficient way
     *  of updating the characteristic, so it would be good to optimize this
     *  at some point.
     */
    acc_x_data.f = 100;
    acc_y_data.f = 101;
    acc_z_data.f = 102;

    gyro_x_data.f = 200;
    gyro_y_data.f = 201;
    gyro_z_data.f = 202;

    mag_x_data.f = 300;
    mag_y_data.f = 301;
    mag_z_data.f = 302;

    // These arrays are created solely to pass to the updateCharacteristicValue
    //  function. This could probably be optimized somehow.
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
    cnt++;
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

  // setup adv_data and srp_data
  ble.accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE);
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t*)uuid16_list, sizeof(uuid16_list));
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)BLE_DEVICE_NAME, sizeof(BLE_DEVICE_NAME));
  // set adv_type
  ble.setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
    // add service
  ble.addService(imu_service);
  ble.addService(smart_service);
  // set device name
  ble.setDeviceName((const uint8_t *)BLE_DEVICE_NAME);
  // set tx power,valid values are -40, -20, -16, -12, -8, -4, 0, 4
  ble.setTxPower(4);
  // set adv_interval, 100ms in multiples of 0.625ms.
  ble.setAdvertisingInterval(160);
  // set adv_timeout, in seconds
  ble.setAdvertisingTimeout(0);
  // start advertising
  ble.startAdvertising();
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
}

void loop() {
  ble.waitForEvent();
}

