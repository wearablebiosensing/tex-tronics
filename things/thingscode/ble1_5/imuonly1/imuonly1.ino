/*
 * Copyright (c) 2016 RedBear
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

/*
 * This program collects data from the LSM9DS1 IMU IC as well as from FlexSensors. This data is then transmitted via BLE to any connected Central Device.
 * The data is sent in UART form every 550 ms. Each packet is 19 bytes (see below) and two packets are sent per data collection event.
 * 
 *    analogRead() returns int (0 to 1023) -> 10-bit ADC
 *        each flex sensor = 2 bytes (2 * 5 = 10 bytes)
 *    imu returns signed 16 bit readings (-32768 to 32767)
 *        each imu reading = 2 bytes (2 * 9 = 18 bytes)
 *    long data type is 4 bytes (for the timestamp)
 *    
 *    Total Bytes to Transmit per Sample = (10 + 19 + 4) = 33 bytes
 *    
 *    Android Max Packets Sent/Received (Reliably) = 20 bytes
 *    First Packet (19 Bytes) - 0x01 (1); Timestamp (4); flex sensor readings (10); null bytes (4)
 *    Second Packet (19 Bytes) - 0x02 (1); IMU readings (18)
 */
 
 
#include <BLE_API.h>
#include "Wire_SG.h"
#include <SPI.h>
#include "SparkFunLSM9DS1_SG.h"
#include "SmartGlove.h"

BLE                                       ble;                  // BLE Module
LSM9DS1                                   imu;                  // IMU Module
Ticker                                    ticker_task1;         // Timer for Periodic Callback (used instead of delay in loop)

static uint8_t packet1[TXRX_BUF_LEN];                           // Container for data sent in first packet
static uint8_t packet2[TXRX_BUF_LEN];                           // Container for data sent in second packet
static int looper;

sg_dif_t ticks; 
sg_time_t prev_ticks;                                                // Timestamp sent along with data
uint8_t time_since = 0;
flex_data_t thumb_data, index_data, middle_data, ring_data, pinky_data;  // FlexSensor Values
imu_data_t acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, mag_x, mag_y, mag_z; // IMU Values


// The uuid of service and characteristics
static const uint8_t uart_service_uuid[]        = {0x6E, 0x40, 0X00, 0X01, 0xB5, 0xA3, 0xF3, 0x93, 0xE0, 0xA9, 0xE5, 0x0E, 0x24, 0xDC, 0xCA, 0x9E}; // 6E400001-B5A3-F393-E0A9-E50E24DCCA9E
static const uint8_t tx_characteristic_uuid[]   = {0x6E, 0x40, 0X00, 0X02, 0xB5, 0xA3, 0xF3, 0x93, 0xE0, 0xA9, 0xE5, 0x0E, 0x24, 0xDC, 0xCA, 0x9E}; // 6E400002-B5A3-F393-E0A9-E50E24DCCA9E
static const uint8_t rx_characteristic_uuid[]   = {0x6E, 0x40, 0X00, 0X03, 0xB5, 0xA3, 0xF3, 0x93, 0xE0, 0xA9, 0xE5, 0x0E, 0x24, 0xDC, 0xCA, 0x9E}; // 6E400003-B5A3-F393-E0A9-E50E24DCCA9E

uint8_t tx_value[TXRX_BUF_LEN] = {0,};
uint8_t rx_value[TXRX_BUF_LEN] = {0,};

// Create characteristic and service
GattCharacteristic  tx_characteristic(tx_characteristic_uuid, tx_value, 1, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_WRITE );
GattCharacteristic  rx_characteristic(rx_characteristic_uuid, rx_value, 1, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);
GattCharacteristic *uart_chars[] = {&tx_characteristic, &rx_characteristic};
GattService         uart_service(uart_service_uuid, uart_chars, sizeof(uart_chars) / sizeof(GattCharacteristic *));

void disconnectionCallBack(const Gap::DisconnectionCallbackParams_t *params) {
  ble.startAdvertising();
}

void gattServerWriteCallBack(const GattWriteCallbackParams *Handler) {
  uint8_t buf[TXRX_BUF_LEN];
  uint16_t bytesRead, index;

  if (Handler->handle == tx_characteristic.getValueAttribute().getHandle()) {
    ble.readCharacteristicValue(tx_characteristic.getValueAttribute().getHandle(), buf, &bytesRead);
    for(index=0; index<bytesRead; index++) {
      // Handle Incoming Data
    }
  }
}

// This function is called every DATA_REFRESH_MS (SmartGlove.h)
void periodic_callback() {
  if (ble.getGapState().connected) {

    prev_ticks.value = (uint16_t)millis() - prev_ticks.value;
    time_since = byte(prev_ticks.value);
    
    // Update Data on IMU
    imu.readAccel();
    // Collect Data from IMU
    acc_x.value = imu.ax;
    acc_y.value = imu.ay;
    acc_z.value = imu.az;
    //included the Gyro value as well
    imu.readGyro();
    gyr_x.value = imu.gx;
    gyr_y.value = imu.gy;
    gyr_z.value = imu.gz;
    //imu.readMag();

    if (looper <= (DATA_PER_PACKET/2))
    {
      //packet1[1 + looper * PACKET_LENGTH] = prev_ticks.b[0];
      //packet1[2 + looper * PACKET_LENGTH] = prev_ticks.b[0];
      packet1[1 + looper * PACKET_LENGTH] = acc_x.b[0];
      packet1[2 + looper * PACKET_LENGTH] = acc_x.b[1];
      packet1[3 + looper * PACKET_LENGTH] = acc_y.b[0];
      packet1[4 + looper * PACKET_LENGTH] = acc_y.b[1];
      packet1[5 + looper * PACKET_LENGTH] = acc_z.b[0];
      packet1[6 + looper * PACKET_LENGTH] = acc_z.b[1];
      packet1[7 + looper * PACKET_LENGTH] = gyr_x.b[0];
      packet1[8 + looper * PACKET_LENGTH] = gyr_x.b[1];
      packet1[9 + looper * PACKET_LENGTH] = gyr_y.b[0];
      packet1[10 + looper * PACKET_LENGTH] = gyr_y.b[1];
      packet1[11 + looper * PACKET_LENGTH] = gyr_z.b[0];
      packet1[12 + looper * PACKET_LENGTH] = gyr_z.b[1];
    }
    else
    {
      //packet2[1 + (looper - 2) * PACKET_LENGTH] = prev_ticks.b[0];
      //packet2[2 + (looper - 2) * PACKET_LENGTH] = prev_ticks.b[1];
      packet2[1 + (looper - 2) * PACKET_LENGTH] = acc_x.b[0];
      packet2[2 + (looper - 2) * PACKET_LENGTH] = acc_x.b[1];
      packet2[3 + (looper - 2) * PACKET_LENGTH] = acc_y.b[0];
      packet2[4 + (looper - 2) * PACKET_LENGTH] = acc_y.b[1];
      packet2[5 + (looper - 2) * PACKET_LENGTH] = acc_z.b[0];
      packet2[6 + (looper - 2) * PACKET_LENGTH] = acc_z.b[1];
      packet2[7 + (looper - 2) * PACKET_LENGTH] = gyr_x.b[0];
      packet2[8 + (looper - 2) * PACKET_LENGTH] = gyr_x.b[1];
      packet2[9 + (looper - 2) * PACKET_LENGTH] = gyr_y.b[0];
      packet2[10 + (looper - 2) * PACKET_LENGTH] = gyr_y.b[1];
      packet2[11 + (looper - 2) * PACKET_LENGTH] = gyr_z.b[0];
      packet2[12 + (looper - 2) * PACKET_LENGTH] = gyr_z.b[1];
    }


    if (looper == DATA_PER_PACKET-1)
    {
      // Transmit every 4 DATA_REFRESH_MS milliseconds
      ble.updateCharacteristicValue(rx_characteristic.getValueAttribute().getHandle(), packet1, TXRX_BUF_LEN);
      ble.updateCharacteristicValue(rx_characteristic.getValueAttribute().getHandle(), packet2, TXRX_BUF_LEN);
    }

    looper = (looper + 1) % (DATA_PER_PACKET);
  }

}

/*
 * Initialize the IMU Module. More options available (see SparkFunLSM9DS1_SG.h)
 */
void init_imu() {
  imu.settings.device.commInterface = IMU_MODE_I2C;   // Use I2C to Collect Data from IMU
  imu.settings.device.mAddress = LSM9DS1_M;           // Use default I2C Addresses
  imu.settings.device.agAddress = LSM9DS1_AG;
  imu.begin();                                        // Start IMU
}

void init_ble() {
  ble.init();
  ble.onDisconnection(disconnectionCallBack);
  ble.onDataWritten(gattServerWriteCallBack);

  // setup adv_data and srp_data
  ble.accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE);
  ble.accumulateAdvertisingPayload(GapAdvertisingData::SHORTENED_LOCAL_NAME,
                                   (const uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME) - 1);
  // set adv_type
  ble.setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
  // add service
  ble.addService(uart_service);
  // set device name
  ble.setDeviceName((const uint8_t *)DEVICE_NAME);
  // set tx power,valid values are -40, -20, -16, -12, -8, -4, 0, 4
  ble.setTxPower(0);
  // set adv_interval, 100ms in multiples of 0.625ms.
  ble.setAdvertisingInterval(160);
  // set adv_timeout, in seconds
  ble.setAdvertisingTimeout(0);
  // Set Connection Parameters
  //ble.setPreferredConnectionParams(&conn_params);
  // start advertising
  ble.startAdvertising();
}

void setup() {
  ticker_task1.attach_us(&periodic_callback, 10000);//DATA_REFRESH_RATE_MS * 1000); // Initialize Timer (calls periodic_callback)
  // put your setup code here, to run once
  //Serial.begin(115200);                             // Setup Serial Monitor
  pinMode(D13, OUTPUT);
  init_ble();                                       // Configure BLE Module and Start Advertising
  //init_imu();                                       // Configure IMU Module and Start IMU
  if (!imu.begin())
  {
    //Serial.println("Failed to communicate with LSM9DS1.");
    //Serial.println("Double-check wiring.");
    //Serial.println("Default settings in this sketch will " \
                  "work for an out of the box LSM9DS1 " \
                  "Breakout, but may need to be modified " \
                  "if the board jumpers are.");
    
    while (1) {
      digitalWrite(D13,HIGH);
      delay(500);
      digitalWrite(D13, LOW);
      delay(500);
    }
  }
  //Serial.println("Success");
  digitalWrite(D13,HIGH);
  looper = 0;                                       // Initialize Looper
  prev_ticks.value = (uint16_t)millis();
  packet1[0] = 0x01;
  packet2[0] = 0x01;
  ticker_task1.attach_us(periodic_callback, DATA_REFRESH_RATE_MS * 1000); // Initialize Timer (calls periodic_callback)
}

void loop() {
  // put your main code here, to run repeatedly:
  if (ble.getGapState().connected) {
    // Collect Data from FlexSensors
    //ticks.value = (uint16_t)(millis() - prev_ticks.value);
  } else {
    ble.waitForEvent(); 
  }
}
