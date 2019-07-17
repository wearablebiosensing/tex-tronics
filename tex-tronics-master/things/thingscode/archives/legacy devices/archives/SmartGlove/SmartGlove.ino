#include <BLE_API.h>
#include "Wire_SG.h"
#include <SPI.h>
#include "SparkFunLSM9DS1_SG.h"
#include "SmartGlove.h"

BLE ble;                      // BLE Module
LSM9DS1 imu;                  // IMU Module
Ticker ticker_task1;          // Timer for Periodic Callback (used instead of delay in loop)
uint8_t setup_successful;


// Measure the voltage at 5V and the actual resistance of your
// 47k resistor, and enter them below:
const float VCC = 3.3; // Measured voltage of Ardunio 5V line
const float R_DIV = 10000.0; // Measured resistance of 3.3k resistor
//const float R_DIV2 = 1200000.0; // Measured resistance of 3.3k resistor


// Upload the code, then try to adjust these values to more
// accurately calculate bend degree.
const float STRAIGHT_RESISTANCE = 52000.0; // resistance when straight
const float BEND_RESISTANCE = 155000.0; // resistance at 90 deg



// List of Services Available, used in Advertising Data to display Services
static const uint16_t uuid16_list[] = {
  UUID_IMU_SERVICE, 
  UUID_FLEX_SERVICE,
  UUID_SMART_SERVICE
};

// Create IMU Service and Characteristics
static imu_data_t acc_x_data, acc_y_data, acc_z_data;
static uint8_t acc_data[7] = {
  0x00, 
  acc_x_data.b[1], acc_x_data.b[0], 
  acc_y_data.b[1], acc_y_data.b[0], 
  acc_z_data.b[1], acc_z_data.b[0]
};

static imu_data_t gyro_x_data, gyro_y_data, gyro_z_data;
static uint8_t gyro_data[7] = {
  0x00, 
  gyro_x_data.b[1], gyro_x_data.b[0], 
  gyro_y_data.b[1], gyro_y_data.b[0], 
  gyro_z_data.b[1], gyro_z_data.b[0]
};

static imu_data_t mag_x_data, mag_y_data, mag_z_data;
static uint8_t mag_data[7] = {
  0x00, 
  mag_x_data.b[1], mag_x_data.b[0], 
  mag_y_data.b[1], mag_y_data.b[0], 
  mag_z_data.b[1], mag_z_data.b[0]
};

GattCharacteristic acc_char(UUID_ACCEL_CHAR, acc_data, sizeof(acc_data), sizeof(acc_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic gyro_char(UUID_GYRO_CHAR, gyro_data, sizeof(gyro_data), sizeof(gyro_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic mag_char(UUID_MAG_CHAR, mag_data, sizeof(mag_data), sizeof(mag_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic *imu_chars[] = {&acc_char, &gyro_char, &mag_char, };
GattService        imu_service(UUID_IMU_SERVICE, imu_chars, sizeof(imu_chars) / sizeof(GattCharacteristic *));

// Create Flex Service and Characteristics
uint8_t thumb_flex, index_flex, middle_flex, ring_flex, pinky_flex;
static uint8_t thumb_data[2] = {0x00, thumb_flex};
static uint8_t index_data[2] = {0x00, index_flex};
static uint8_t middle_data[2] = {0x00, middle_flex};
static uint8_t ring_data[2] = {0x00, ring_flex};
static uint8_t pinky_data[2] = {0x00, pinky_flex};

uint8_t temp_acc_data[7];
uint8_t temp_gyro_data[7];
uint8_t temp_mag_data[7];


GattCharacteristic thumb_char(UUID_THUMB_CHAR, thumb_data, sizeof(thumb_data), sizeof(thumb_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic index_char(UUID_INDEX_CHAR, index_data, sizeof(index_data), sizeof(index_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic middle_char(UUID_MIDDLE_CHAR, middle_data, sizeof(middle_data), sizeof(middle_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic ring_char(UUID_RING_CHAR, ring_data, sizeof(ring_data), sizeof(ring_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic pinky_char(UUID_PINKY_CHAR, pinky_data, sizeof(pinky_data), sizeof(pinky_data), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic *flex_chars[] = {&thumb_char, &index_char, &middle_char, &ring_char, &pinky_char, };
GattService        flex_service(UUID_FLEX_SERVICE, flex_chars, sizeof(flex_chars) / sizeof(GattCharacteristic *));

// Create Smart Service and Characteristics
uint8_t cnt;
sg_time_t ticks;
static uint8_t counter[2] = {0x00, cnt};
static uint8_t timestamp[5] = {0x00, ticks.b[0], ticks.b[1], ticks.b[2], ticks.b[3] };
GattCharacteristic data_ready_char(UUID_DATA_READY_CHAR, counter, sizeof(counter), sizeof(counter), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);
GattCharacteristic timestamp_char(UUID_TIMESTAMP_CHAR, timestamp, sizeof(timestamp), sizeof(timestamp), GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic *smart_chars[] = {&data_ready_char, &timestamp_char, };
GattService        smart_service(UUID_SMART_SERVICE, smart_chars, sizeof(smart_chars) / sizeof(GattCharacteristic *));

/**
 * This callback is invoked every time the device
 * is disconnected from. For now, just start
 * advertising again.
 */
void disconnectionCallBack(const Gap::DisconnectionCallbackParams_t *params) {
  //Serial.println("Disconnected!");
  //Serial.println("Restarting the advertising process");
  ble.startAdvertising();
}

/**
 * This callback is invoked every DATA_REFRESH_MS
 * milliseconds. This value can be changed in
 * SmartGlove.h.
 */
void periodicCallback() {
  if(!setup_successful) {
    counter[1] = 0;
  } else {
    // This Characteristic is used to notify the Client that all the data is
    //  ready to be read. This is safer than each data characteristic notifying
    //  the Client individually since the Client could mismatch some data points.
    counter[1] = ++cnt;
    if(cnt == 0) {
      // Make sure Data Ready is never 0 when working properly
      counter[1] = ++cnt;
    }
  }
  
  if (ble.getGapState().connected) {
    // Update IMU Characteristic values
    ble.updateCharacteristicValue(acc_char.getValueAttribute().getHandle(), temp_acc_data, sizeof(temp_acc_data));
    ble.updateCharacteristicValue(gyro_char.getValueAttribute().getHandle(), temp_gyro_data, sizeof(temp_gyro_data));
    ble.updateCharacteristicValue(mag_char.getValueAttribute().getHandle(), temp_mag_data, sizeof(temp_mag_data));

    // Update Flex Characteristic values
    ble.updateCharacteristicValue(thumb_char.getValueAttribute().getHandle(), thumb_data, sizeof(thumb_data));
    ble.updateCharacteristicValue(index_char.getValueAttribute().getHandle(), index_data, sizeof(index_data));
    
    // Update counter
    ble.updateCharacteristicValue(data_ready_char.getValueAttribute().getHandle(), counter, sizeof(counter));

    ticks.value = millis();
    timestamp[1] = ticks.b[3];
    timestamp[2] = ticks.b[2];
    timestamp[3] = ticks.b[1];
    timestamp[4] = ticks.b[0];
    ble.updateCharacteristicValue(timestamp_char.getValueAttribute().getHandle(), timestamp, sizeof(timestamp));

    ble.updateCharacteristicValue(middle_char.getValueAttribute().getHandle(), middle_data, sizeof(middle_data));

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
  ble.addService(flex_service);
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
  imu.settings.device.commInterface = IMU_MODE_I2C;
  imu.settings.device.mAddress = LSM9DS1_M;
  imu.settings.device.agAddress = LSM9DS1_AG;

  if(!imu.begin()) {
    // IMU Module not wired properly!
    // TODO: Handle this Error Somehow
    setup_successful = FALSE;
  } else {
    setup_successful = TRUE;
  }
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
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);
  init_ble();                                     // Initialize BLE Module
  digitalWrite(13, HIGH);
  init_imu();                                     // Initialize IMU Module
  ticker_task1.attach_us(periodicCallback, DATA_REFRESH_RATE_MS * 1000); // Initialize Timer (calls periodic_callback)
}

void loop() {
  long start_time, stop_time;
  if (ble.getGapState().connected && setup_successful) {
    start_time = millis();
    imu.readAccel();                  // Read IMU Data (16 Bit ADC Resolution)
    imu.readGyro();
    imu.readMag();

    // Read the ADC, and calculate voltage and resistance from it
    int flexADC = analogRead(A3);
    float flexV = flexADC * VCC / 1023.0;
    float flexR = R_DIV * (VCC / flexV - 1.0);

    int flexADC2 = analogRead(A4);
    float flexV2 = flexADC2 * VCC / 1023.0;
    float flexR2 = R_DIV * (VCC / flexV2 - 1.0);

    // Use the calculated resistance to estimate the sensor's
    // bend angle:
    thumb_flex = map(flexR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,0, 90.0);
    index_flex = map(flexR2, STRAIGHT_RESISTANCE, BEND_RESISTANCE,0, 90.0);

    thumb_data[1] = thumb_flex;
    index_data[1] = index_flex;

    //thumb_flex = map(analogRead(A3), 140, 200, 0, 90);      // Read Flex Sensor Values
    //index_flex = map(analogRead(A4), 140, 200, 0, 90);
    // TODO: Add rest of fingers
    
    acc_x_data.f = imu.ax + 32767;            // Update Accelerometer Data
    acc_x_data.b[0] = (uint8_t)(acc_x_data.f & 0xff);
    acc_x_data.b[1] = (uint8_t)(acc_x_data.f >> 8);
    //Serial.println(imu.ax);
    //Serial.println((int16_t)((((uint16_t)acc_x_data.b[1] << 8) | acc_x_data.b[0])-32767));  //this is the undo process
    acc_y_data.f = imu.ay + 32767;
    acc_y_data.b[0] = (uint8_t)(acc_y_data.f & 0xff);
    acc_y_data.b[1] = (uint8_t)(acc_y_data.f >> 8);
    acc_z_data.f = imu.az + 32767;
    acc_z_data.b[0] = (uint8_t)(acc_z_data.f & 0xff);
    acc_z_data.b[1] = (uint8_t)(acc_z_data.f >> 8);
    
    gyro_x_data.f = imu.gx + 32767;           // Update Gyroscope Data
    gyro_x_data.b[0] = (uint8_t)(gyro_x_data.f & 0xff);
    gyro_x_data.b[1] = (uint8_t)(gyro_x_data.f >> 8);
    gyro_y_data.f = imu.gy + 32767;
    gyro_y_data.b[0] = (uint8_t)(gyro_y_data.f & 0xff);
    gyro_y_data.b[1] = (uint8_t)(gyro_y_data.f >> 8);
    gyro_z_data.f = imu.gz + 32767;
    gyro_z_data.b[0] = (uint8_t)(gyro_z_data.f & 0xff);
    gyro_z_data.b[1] = (uint8_t)(gyro_z_data.f >> 8);
    
    mag_x_data.f = imu.mx + 32767;            // Update Magnetometer Data
    mag_x_data.b[0] = (uint8_t)(mag_x_data.f & 0xff);
    mag_x_data.b[1] = (uint8_t)(mag_x_data.f >> 8);
    mag_y_data.f = imu.my + 32767;
    mag_y_data.b[0] = (uint8_t)(mag_y_data.f & 0xff);
    mag_y_data.b[1] = (uint8_t)(mag_y_data.f >> 8);
    mag_z_data.f = imu.mz + 32767;
    mag_z_data.b[0] = (uint8_t)(mag_z_data.f & 0xff);
    mag_z_data.b[1] = (uint8_t)(mag_z_data.f >> 8);

    /**
     * TODO:
     *  This is probably a very inefficient way
     *  of updating the characteristic, so it would be 
     *  good to optimize this at some point.
     */
     temp_acc_data[0] = 0x00;
     temp_acc_data[1] = acc_x_data.b[1];
     temp_acc_data[2] = acc_x_data.b[0];
     temp_acc_data[3] = acc_y_data.b[1];
     temp_acc_data[4] = acc_y_data.b[0];
     temp_acc_data[5] = acc_z_data.b[1];
     temp_acc_data[6] = acc_z_data.b[0];

     temp_gyro_data[0] = 0x00;
     temp_gyro_data[1] = gyro_x_data.b[1];
     temp_gyro_data[2] = gyro_x_data.b[0];
     temp_gyro_data[3] = gyro_y_data.b[1];
     temp_gyro_data[4] = gyro_y_data.b[0];
     temp_gyro_data[5] = gyro_z_data.b[1];
     temp_gyro_data[6] = gyro_z_data.b[0];

     temp_mag_data[0] = 0x00;
     temp_mag_data[1] = mag_x_data.b[1];
     temp_mag_data[2] = mag_x_data.b[0];
     temp_mag_data[3] = mag_y_data.b[1];
     temp_mag_data[4] = mag_y_data.b[0];
     temp_mag_data[5] = mag_z_data.b[1];
     temp_mag_data[6] = mag_z_data.b[0];

    stop_time = millis();

    int time_dif = (stop_time - start_time) >> 16;
    middle_data[1] = middle_flex;
  } else {
    ble.waitForEvent();
  }
}

