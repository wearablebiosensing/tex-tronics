/*****************************************************************
Pulseband_Settings.ino
SFE_LSM9DS1 Library Settings Configuration and PulseSensor controller
Nicholas Constant @ URI Wearables
Original Creation Date: August 19, 2015

//LSM9DS1 info
https://github.com/sparkfun/LSM9DS1_Breakout
//PulseSensor info
https://github.com/WorldFamousElectronics/PulseSensor_Amped_Arduino/blob/master/README.md

Code handles two main components:
1) the LSM9dS1
2) the PulseSensor
--------------------------------------
The breakdown:
1) the LSM9dS1

This Arduino sketch demonstrates how to configure every
possible configuration value in the SparkFunLSM9DS1 library.

It demonstrates how to set the output data rates and scales
for each sensor, along with other settings like LPF cutoff
frequencies and low-power settings.

It also demonstrates how to turn various sensors in the
LSM9DS1 on or off.

Hardware setup: This library supports communicating with the
LSM9DS1 over either I2C or SPI. This example demonstrates how
to use I2C. The pin-out is as follows:
  LSM9DS1 --------- Arduino
   SCL ---------- SCL (A5 on older 'Duinos')
   SDA ---------- SDA (A4 on older 'Duinos')
   VDD ------------- 3.3V
   GND ------------- GND
(CSG, CSXM, SDOG, and SDOXM should all be pulled high.
Jumpers on the breakout board will do this for you.)

The LSM9DS1 has a maximum voltage of 3.6V. Make sure you power it
off the 3.3V rail! I2C pins are open-drain, so you'll be
(mostly) safe connecting the LSM9DS1's SCL and SDA pins
directly to the Arduino.

2) the PulseSensor
    -Blinks an LED to User's Live Heartbeat   PIN 13
    -Fades an LED to User's Live HeartBeat
    -Determines BPM
    -Prints All of the Above to Serial

Development environment specifics:
  IDE: Arduino 1.6.5
  Hardware Platform: BLE Nano
  LSM9DS1 Breakout Version: 1.0
  PulseSensor Version: Amped

This code is beerware. If you see me at the local, and you've found our code helpful,
please buy us a round!

Distributed as-is; no warranty is given.
*****************************************************************/

// Include SparkFunLSM9DS1 library and its dependencies
#include <Wire.h>
#include <SPI.h>
#include <SparkFunLSM9DS1.h>


/*****************
 * Motion
 */
LSM9DS1 imu;  // Create an LSM9DS1 object


// Global variables to print to serial monitor at a steady rate
unsigned long lastPrint = 0;
const unsigned int PRINT_RATE = 500;
Ticker tickerAG, tickerMAG;


//accelerometer
uint16_t fax;// uint8_t *pax = (uint8_t*)&fax;
uint16_t fay;// uint8_t *pay = (uint8_t*)&fay;
uint16_t faz;// uint8_t *paz = (uint8_t*)&faz;
uint8_t abuf[19] = "A";
//float faccr; uint8_t *paccr = (uint8_t*)&faccr;

//gyroscope
uint16_t fgx;// uint8_t *pgx = (uint8_t*)&fgx;
uint16_t fgy;// uint8_t *pgy = (uint8_t*)&fgy;
uint16_t fgz;// uint8_t *pgz = (uint8_t*)&fgz;
//float fgyr; uint8_t *pgyr = (uint8_t*)&fgyr;
//uint8_t gbuf[13] = "G";

//magnometer
uint16_t fmx;// uint8_t *pmx = (uint8_t*)&fmx;
uint16_t fmy;// uint8_t *pmy = (uint8_t*)&fmy;
uint16_t fmz;// uint8_t *pmz = (uint8_t*)&fmz;
//float fmar; uint8_t *pmar = (uint8_t*)&fmar;
//uint8_t mbuf[13] = "M";


/*****************
 * Pulse
 */
//  Variables
int pulsePin = A5;                 // Pulse Sensor purple wire connected to analog pin 0
uint16_t Signal;                // holds the incoming raw data
Ticker tickerPULSE;

uint16_t fsig; uint8_t *psig = (uint8_t*)&fsig;

/*****************
 * BLE
 */
#define TXRX_BUF_LEN                     20

BLE                                      ble;
Ticker                                   ticker2;

static byte queryDone            = false;
static byte buf_len              = 0;
static uint8_t reback_pin        = 2;
static uint8_t status_check_flag = 1;

// The Nordic UART Service
static const uint8_t service1_uuid[]                = {0x71, 0x3D, 0, 0, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t service1_tx_uuid[]             = {0x71, 0x3D, 0, 3, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t service1_rx_uuid[]             = {0x71, 0x3D, 0, 2, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t uart_base_uuid_rev[]           = {0x1E, 0x94, 0x8D, 0xF1, 0x48, 0x31, 0x94, 0xBA, 0x75, 0x4C, 0x3E, 0x50, 0, 0, 0x3D, 0x71};

uint8_t tx_value[TXRX_BUF_LEN] = {0,};
uint8_t rx_value[TXRX_BUF_LEN] = {0,};

GattCharacteristic  characteristic1(service1_tx_uuid, tx_value, 1, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_WRITE | GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_WRITE_WITHOUT_RESPONSE );

GattCharacteristic  characteristic2(service1_rx_uuid, rx_value, 1, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);

GattCharacteristic *uartChars[] = {&characteristic1, &characteristic2};

GattService         uartService(service1_uuid, uartChars, sizeof(uartChars) / sizeof(GattCharacteristic *));


static void disconnectionCallBack(Gap::Handle_t handle, Gap::DisconnectionReason_t reason)
{
  //Serial1.println("Disconnected!");
  //Serial1.println("Restarting the advertising process");
  ble.startAdvertising();
}

/********************
 * Motion functions
 */
void setupDevice()
{
  // [commInterface] determines whether we'll use I2C or SPI
  // to communicate with the LSM9DS1.
  // Use either IMU_MODE_I2C or IMU_MODE_SPI
  imu.settings.device.commInterface = IMU_MODE_I2C;
  // [mAddress] sets the I2C address or SPI CS pin of the
  // LSM9DS1's magnetometer.
  imu.settings.device.mAddress = 0x1E; // Use I2C addres 0x1E
  // [agAddress] sets the I2C address or SPI CS pin of the
  // LSM9DS1's accelerometer/gyroscope.
  imu.settings.device.agAddress = 0x6B; // I2C address 0x6B
}

void setupGyro()
{
  // [enabled] turns the gyro on or off.
  imu.settings.gyro.enabled = true;  // Enable the gyro
  // [scale] sets the full-scale range of the gyroscope.
  // scale can be set to either 245, 500, or 2000
  imu.settings.gyro.scale = 245; // Set scale to +/-245dps
  // [sampleRate] sets the output data rate (ODR) of the gyro
  // sampleRate can be set between 1-6
  // 1 = 14.9    4 = 238
  // 2 = 59.5    5 = 476
  // 3 = 119     6 = 952
  imu.settings.gyro.sampleRate = 4; // 238Hz ODR
  // [bandwidth] can set the cutoff frequency of the gyro.
  // Allowed values: 0-3. Actual value of cutoff frequency
  // depends on the sample rate. (Datasheet section 7.12)
  imu.settings.gyro.bandwidth = 0;
  // [lowPowerEnable] turns low-power mode on or off.
  imu.settings.gyro.lowPowerEnable = false; // LP mode off
  // [HPFEnable] enables or disables the high-pass filter
  imu.settings.gyro.HPFEnable = true; // HPF disabled
  // [HPFCutoff] sets the HPF cutoff frequency (if enabled)
  // Allowable values are 0-9. Value depends on ODR.
  // (Datasheet section 7.14)
  imu.settings.gyro.HPFCutoff = 1; // HPF cutoff = 4Hz
  // [flipX], [flipY], and [flipZ] are booleans that can
  // automatically switch the positive/negative orientation
  // of the three gyro axes.
  imu.settings.gyro.flipX = false; // Don't flip X
  imu.settings.gyro.flipY = false; // Don't flip Y
  imu.settings.gyro.flipZ = false; // Don't flip Z
}

void setupAccel()
{
  // [enabled] turns the acclerometer on or off.
  imu.settings.accel.enabled = true; // Enable accelerometer
  // [enableX], [enableY], and [enableZ] can turn on or off
  // select axes of the acclerometer.
  imu.settings.accel.enableX = true; // Enable X
  imu.settings.accel.enableY = true; // Enable Y
  imu.settings.accel.enableZ = true; // Enable Z
  // [scale] sets the full-scale range of the accelerometer.
  // accel scale can be 2, 4, 8, or 16
  imu.settings.accel.scale = 8; // Set accel scale to +/-8g.
  // [sampleRate] sets the output data rate (ODR) of the
  // accelerometer. ONLY APPLICABLE WHEN THE GYROSCOPE IS
  // DISABLED! Otherwise accel sample rate = gyro sample rate.
  // accel sample rate can be 1-6
  // 1 = 10 Hz    4 = 238 Hz
  // 2 = 50 Hz    5 = 476 Hz
  // 3 = 119 Hz   6 = 952 Hz
  imu.settings.accel.sampleRate = 1; // Set accel to 10Hz.
  // [bandwidth] sets the anti-aliasing filter bandwidth.
  // Accel cutoff freqeuncy can be any value between -1 - 3.
  // -1 = bandwidth determined by sample rate
  // 0 = 408 Hz   2 = 105 Hz
  // 1 = 211 Hz   3 = 50 Hz
  imu.settings.accel.bandwidth = 0; // BW = 408Hz
  // [highResEnable] enables or disables high resolution
  // mode for the acclerometer.
  imu.settings.accel.highResEnable = false; // Disable HR
  // [highResBandwidth] sets the LP cutoff frequency of
  // the accelerometer if it's in high-res mode.
  // can be any value between 0-3
  // LP cutoff is set to a factor of sample rate
  // 0 = ODR/50    2 = ODR/9
  // 1 = ODR/100   3 = ODR/400
  imu.settings.accel.highResBandwidth = 0;
}

void setupMag()
{
  // [enabled] turns the magnetometer on or off.
  imu.settings.mag.enabled = true; // Enable magnetometer
  // [scale] sets the full-scale range of the magnetometer
  // mag scale can be 4, 8, 12, or 16
  imu.settings.mag.scale = 12; // Set mag scale to +/-12 Gs
  // [sampleRate] sets the output data rate (ODR) of the
  // magnetometer.
  // mag data rate can be 0-7:
  // 0 = 0.625 Hz  4 = 10 Hz
  // 1 = 1.25 Hz   5 = 20 Hz
  // 2 = 2.5 Hz    6 = 40 Hz
  // 3 = 5 Hz      7 = 80 Hz
  imu.settings.mag.sampleRate = 7; // Set OD rate to 80Hz
  // [tempCompensationEnable] enables or disables
  // temperature compensation of the magnetometer.
  imu.settings.mag.tempCompensationEnable = false;
  // [XYPerformance] sets the x and y-axis performance of the
  // magnetometer to either:
  // 0 = Low power mode      2 = high performance
  // 1 = medium performance  3 = ultra-high performance
  imu.settings.mag.XYPerformance = 3; // Ultra-high perform.
  // [ZPerformance] does the same thing, but only for the z
  imu.settings.mag.ZPerformance = 3; // Ultra-high perform.
  // [lowPowerEnable] enables or disables low power mode in
  // the magnetometer.
  imu.settings.mag.lowPowerEnable = false;
  // [operatingMode] sets the operating mode of the
  // magnetometer. operatingMode can be 0-2:
  // 0 = continuous conversion
  // 1 = single-conversion
  // 2 = power down
  imu.settings.mag.operatingMode = 0; // Continuous mode
}

void setupTemperature()
{
  // [enabled] turns the temperature sensor on or off.
  imu.settings.temp.enabled = false;
}

uint16_t initLSM9DS1()
{
  setupDevice(); // Setup general device parameters
  setupGyro(); // Set up gyroscope parameters
  setupAccel(); // Set up accelerometer parameters
  setupMag(); // Set up magnetometer parameters
  setupTemperature(); // Set up temp sensor parameter

  return imu.begin();
}

void tickeventAG(void)
{ 
  ble.updateCharacteristicValue(characteristic2.getValueAttribute().getHandle(), abuf, 19);
  //ble.updateCharacteristicValue(characteristic2.getValueAttribute().getHandle(), gbuf, 13);
}

//void tickeventMAG(void)
//{
//  ble.updateCharacteristicValue(characteristic2.getValueAttribute().getHandle(), mbuf, 13);
//}
/***********************
 * Pulse Functions
 */
void tickeventPULSE(void)
{
  Signal = analogRead(pulsePin);              // read the Pulse Sensor
  uint8_t buff[3] = "P";
  buff[1] = (Signal>>8);
  buff[2] = (Signal);
  ble.updateCharacteristicValue(characteristic2.getValueAttribute().getHandle(), buff, 3);
}




void writtenHandle(const GattWriteCallbackParams *Handler)
{
  uint8_t buf[TXRX_BUF_LEN];
  uint16_t bytesRead, index;

  if (Handler->handle == characteristic1.getValueAttribute().getHandle()) {
    ble.readCharacteristicValue(characteristic1.getValueAttribute().getHandle(), buf, &bytesRead);
    //Serial1.print("bytesRead: ");
    //Serial1.println(bytesRead, HEX);
    //Serial1.write(buf[0]);
    //Serial1.print(buf[1], DEC);
    //Serial1.print(buf[2], DEC);
    //Serial1.println("");
    
    switch (buf[0])
    {
      case 0x0B://end update
        {
          tickerPULSE.detach();
          tickerAG.detach();
          //tickerMAG.detach();
          break;
        }
        buf_len = 0;
    }
    status_check_flag = 1;
  }
}

void setup()
{
  //Serial1.begin(9600);
  ble.init();
  ble.onDisconnection(disconnectionCallBack);
  ble.onDataWritten(writtenHandle);

  // setup adv_data and srp_data
  ble.accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED);
  ble.accumulateAdvertisingPayload(GapAdvertisingData::SHORTENED_LOCAL_NAME,
                                   (const uint8_t *)"TXRX", sizeof("TXRX") - 1);
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_128BIT_SERVICE_IDS,
                                   (const uint8_t *)uart_base_uuid_rev, sizeof(uart_base_uuid_rev));

  // set adv_type
  ble.setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
  // add service
  ble.addService(uartService);
  // set device name
  ble.setDeviceName((const uint8_t *)"Pulseband");
  // set tx power,valid values are -40, -20, -16, -12, -8, -4, 0, 4
  ble.setTxPower(4);
  // set adv_interval, 100ms in multiples of 0.625ms.
  ble.setAdvertisingInterval(160);
  // set adv_timeout, in seconds
  ble.setAdvertisingTimeout(.002);
  // start advertising
  ble.startAdvertising();

  uint16_t status = initLSM9DS1();
  tickerPULSE.attach_us(tickeventPULSE, 4000);
  tickerAG.attach_us(tickeventAG, 4200);
  //tickerMAG.attach_us(tickeventMAG, 125000);
}

void loop()
{
    // imu.accelAvailable() returns 1 if new accelerometer
  // data is ready to be read. 0 otherwise.
  if (imu.accelAvailable())
  {
    imu.readAccel();
      //accelerometer
  fax = (uint16_t)(imu.calcAccel(imu.ax)+32767);
  abuf[1] = (fax>>8);
  abuf[2] = (fax);
  fay = (uint16_t)(imu.calcAccel(imu.ay)+32767);
  abuf[3] = (fay>>8);
  abuf[4] = (fay);
  faz = (uint16_t)(imu.calcAccel(imu.az)+32767);
  abuf[5] = (faz>>8);
  abuf[6] = (faz);
  }

  // imu.gyroAvailable() returns 1 if new gyroscope
  // data is ready to be read. 0 otherwise.
  if (imu.gyroAvailable())
  {
    imu.readGyro();
      //gyroscope
  fgx = (uint16_t)(imu.calcGyro(imu.gx)+32767);
  abuf[7] = (fgx>>8);
  abuf[8] = (fgx);
  fgy = (uint16_t)(imu.calcGyro(imu.gy)+32767);
  abuf[9] = (fgy>>8);
  abuf[10] = (fgy);
  fgz = (uint16_t)(imu.calcGyro(imu.gz)+32767);
  abuf[11] = (fgz>>8);
  abuf[12] = (fgz);
  }


  // imu.magAvailable() returns 1 if new magnetometer
  // data is ready to be read. 0 otherwise.
  if (imu.magAvailable())
  {
    imu.readMag();
      
  //magnometer
  fmx = (uint16_t)(imu.calcMag(imu.mx)+32767);
  abuf[13] = (fmx>>8);
  abuf[14] = (fmx);
  fmy = (uint16_t)(imu.calcMag(imu.my)+32767);
  abuf[15] = (fmy>>8);
  abuf[16] = (fmy);
  fmz = (uint16_t)(imu.calcMag(imu.mz)+32767);
  abuf[17] = (fmz>>8);
  abuf[18] = (fmz);
  }

}

