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
#include <BLE_API.h>
// The SFE_LSM9DS1 library requires both Wire and SPI be
// included BEFORE including the 9DS1 library.
#include <Wire.h>
#include <SPI.h>
#include <SparkFunLSM9DS1.h>

//////////////////////////
// LSM9DS1 Library Init //
//////////////////////////
// Use the LSM9DS1 class to create an object. [imu] can be
// named anything, we'll refer to that throught the sketch.
LSM9DS1 imu;

///////////////////////
// Example I2C Setup //
///////////////////////
// SDO_XM and SDO_G are both pulled high, so our addresses are:
#define LSM9DS1_M  0x1E // Would be 0x1C if SDO_M is LOW
#define LSM9DS1_AG  0x6B // Would be 0x6A if SDO_AG is LOW

////////////////////////////
// Sketch Output Settings //
////////////////////////////
#define PRINT_CALCULATED
//#define PRINT_RAW
#define PRINT_SPEED 250 // 250 ms between prints

// Earth's magnetic field varies by location. Add or subtract 
// a declination to get a more accurate heading. Calculate 
// your's here:
// http://www.ngdc.noaa.gov/geomag-web/#declination
#define DECLINATION -8.58 // Declination (degrees) in Boulder, CO.

#define DEVICE_NAME       "BLE_Peripheral"
#define TXRX_BUF_LEN      20
// Create ble instance
BLE                       ble;
// Create a timer task
Ticker                    tickerUpdate;

// The uuid of service and characteristics
static const uint8_t service1_uuid[]         = {0x71, 0x3D, 0, 0, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t service1_chars1_uuid[]  = {0x71, 0x3D, 0, 2, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t service1_chars2_uuid[]  = {0x71, 0x3D, 0, 3, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
static const uint8_t service1_chars3_uuid[]  = {0x71, 0x3D, 0, 4, 0x50, 0x3E, 0x4C, 0x75, 0xBA, 0x94, 0x31, 0x48, 0xF1, 0x8D, 0x94, 0x1E};
// Used in advertisement
static const uint8_t uart_base_uuid_rev[]    = {0x1E, 0x94, 0x8D, 0xF1, 0x48, 0x31, 0x94, 0xBA, 0x75, 0x4C, 0x3E, 0x50, 0, 0, 0x3D, 0x71};

// Initialize value of chars
uint8_t chars1_value[TXRX_BUF_LEN] = {0};
uint8_t chars2_value[TXRX_BUF_LEN] = {1,2,3};
uint8_t chars3_value[TXRX_BUF_LEN] = {0};
static uint8_t glovedata[50]         = {0x0A};
// Create characteristic
GattCharacteristic  characteristic1(service1_chars1_uuid, chars1_value, 1, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_WRITE | GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_WRITE_WITHOUT_RESPONSE );
GattCharacteristic  characteristic2(service1_chars2_uuid, chars2_value, 3, TXRX_BUF_LEN, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_READ);
GattCharacteristic  characteristic3(service1_chars3_uuid, chars3_value, 1, 50, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY);
GattCharacteristic *uartChars[] = {&characteristic1, &characteristic2, &characteristic3};
//Create service
GattService         uartService(service1_uuid, uartChars, sizeof(uartChars) / sizeof(GattCharacteristic *));

DeviceInformationService *deviceInfo;


/*****************
 * Pulse
 */
//  Variables
int ResThumb   = A3;  //Pin values to flex sensors
int Resprimary   = A4;  
int ResMiddle  = D7;
int ResAnnular = D6;
int ResPinky   = A5;

uint16_t OpenedThumb   =0; // Variables for values when the hand is completely opened
uint16_t Openedprimary   =0; // This is needed for a continuous calibration
uint16_t OpenedMiddle  =0; 
uint16_t OpenedAnnular =0;
uint16_t OpenedPinky   =0;

uint16_t ClosedThumb;      // Variables of the values when the hand is completely closed
uint16_t Closedprimary;      // We can't set it to zero since that the minimum value reached
uint16_t ClosedMiddle;     // in the analog read never reach zero. We'll assign the value of
uint16_t ClosedAnnular;    // a first analog read, then the program in the loop will
uint16_t ClosedPinky;      // automatically  assing lower values

uint16_t thumb   =0;       // Variables of the raw values to send
uint16_t primary   =0;
uint16_t middle  =0;
uint16_t annular =0;
uint16_t pinky   =0;
uint16_t check = 123;
//int pulsePin = A5;                 // Pulse Sensor purple wire connected to analog pin 0
//uint16_t Signal;                // holds the incoming raw data
Ticker tickerUpdateSENSE;

/** @brief  Disconnect callback handle
 *
 *  @param[in] *params   params->handle : connect handle
 *                       params->reason : CONNECTION_TIMEOUT                          = 0x08,
 *                                        REMOTE_USER_TERMINATED_CONNECTION           = 0x13,
 *                                        REMOTE_DEV_TERMINATION_DUE_TO_LOW_RESOURCES = 0x14,  // Remote device terminated connection due to low resources.
 *                                        REMOTE_DEV_TERMINATION_DUE_TO_POWER_OFF     = 0x15,  // Remote device terminated connection due to power off.
 *                                        LOCAL_HOST_TERMINATED_CONNECTION            = 0x16,
 *                                        CONN_INTERVAL_UNACCEPTABLE                  = 0x3B,
 */
void disconnectionCallBack(const Gap::DisconnectionCallbackParams_t *params) {
  Serial.print("Disconnected hande : ");
  Serial.println(params->handle, HEX);
  Serial.print("Disconnected reason : ");
  Serial.println(params->reason, HEX);
  Serial.println("Restart advertising ");
  ble.startAdvertising();
}

/** @brief  Connection callback handle
 *
 *  @param[in] *params   params->handle : The ID for this connection
 *                       params->role : PERIPHERAL  = 0x1, // Peripheral Role
 *                                      CENTRAL     = 0x2, // Central Role.
 *                       params->peerAddrType : The peer's BLE address type
 *                       params->peerAddr : The peer's BLE address
 *                       params->ownAddrType : This device's BLE address type
 *                       params->ownAddr : This devices's BLE address
 *                       params->connectionParams->minConnectionInterval
 *                       params->connectionParams->maxConnectionInterval
 *                       params->connectionParams->slaveLatency
 *                       params->connectionParams->connectionSupervisionTimeout
 */
void connectionCallBack( const Gap::ConnectionCallbackParams_t *params ) {
  uint8_t index;
  if(params->role == Gap::PERIPHERAL) {
    Serial.println("Peripheral ");
  }

  Serial.print("The conn handle : ");
  Serial.println(params->handle, HEX);
  Serial.print("The conn role : ");
  Serial.println(params->role, HEX);

  Serial.print("The peerAddr type : ");
  Serial.println(params->peerAddrType, HEX);
  Serial.print("  The peerAddr : ");
  for(index=0; index<6; index++) {
    Serial.print(params->peerAddr[index], HEX);
    Serial.print(" ");
  }
  Serial.println(" ");

  Serial.print("The ownAddr type : ");
  Serial.println(params->ownAddrType, HEX);
  Serial.print("  The ownAddr : ");
  for(index=0; index<6; index++) {
    Serial.print(params->ownAddr[index], HEX);
    Serial.print(" ");
  }
  Serial.println(" ");

  Serial.print("The min connection interval : ");
  Serial.println(params->connectionParams->minConnectionInterval, HEX);
  Serial.print("The max connection interval : ");
  Serial.println(params->connectionParams->maxConnectionInterval, HEX);
  Serial.print("The slaveLatency : ");
  Serial.println(params->connectionParams->slaveLatency, HEX);
  Serial.print("The connectionSupervisionTimeout : ");
  Serial.println(params->connectionParams->connectionSupervisionTimeout, HEX);
}

/** @brief  write callback handle of Gatt server
 *
 *  @param[in] *Handler   Handler->connHandle : The handle of the connection that triggered the event
 *                        Handler->handle : Attribute Handle to which the write operation applies
 *                        Handler->writeOp : OP_INVALID               = 0x00,  // Invalid operation.
 *                                           OP_WRITE_REQ             = 0x01,  // Write request.
 *                                           OP_WRITE_CMD             = 0x02,  // Write command.
 *                                           OP_SIGN_WRITE_CMD        = 0x03,  // Signed write command.
 *                                           OP_PREP_WRITE_REQ        = 0x04,  // Prepare write request.
 *                                           OP_EXEC_WRITE_REQ_CANCEL = 0x05,  // Execute write request: cancel all prepared writes.
 *                                           OP_EXEC_WRITE_REQ_NOW    = 0x06,  // Execute write request: immediately execute all prepared writes.
 *                        Handler->offset : Offset for the write operation
 *                        Handler->len : Length (in bytes) of the data to write
 *                        Handler->data : Pointer to the data to write
 */
void gattServerWriteCallBack(const GattWriteCallbackParams *Handler) {
  uint8_t index;

  Serial.print("Handler->connHandle : ");
  Serial.println(Handler->connHandle, HEX);
  Serial.print("Handler->handle : ");
  Serial.println(Handler->handle, HEX);
  Serial.print("Handler->writeOp : ");
  Serial.println(Handler->writeOp, HEX);
  Serial.print("Handler->offset : ");
  Serial.println(Handler->offset, HEX);
  Serial.print("Handler->len : ");
  Serial.println(Handler->len, HEX);
  for(index=0; index<Handler->len; index++) {
    Serial.print(Handler->data[index], HEX);
  }
  Serial.println(" ");

  uint8_t buf[TXRX_BUF_LEN];
  uint16_t bytesRead;

  Serial.println("Write Handle : ");
  // Check the attribute belong to which characteristic
  if (Handler->handle == characteristic1.getValueAttribute().getHandle()) {
    // Read the value of characteristic
    ble.readCharacteristicValue(characteristic1.getValueAttribute().getHandle(), buf, &bytesRead);
    for(index=0; index<bytesRead; index++) {
      Serial.print(buf[index], HEX);
    }
    Serial.println(" ");
  }
}

/**
 * @brief  Timer task callback handle
 */
void task_handle(void) {
  Serial.println("Task handle ");
  uint8_t buffe[20];

  buffe[0] = {0x0A};
  for( int i = 1; i<20; i++){
    buffe[i] = glovedata[i];
  }
  // if true, saved to attribute, no notification or indication is generated.
  //ble.updateCharacteristicValue(characteristic3.getValueAttribute().getHandle(), (uint8_t *)&value, 2, true);
  // if false or ignore, notification or indication is generated if permit.
  ble.updateCharacteristicValue(characteristic3.getValueAttribute().getHandle(), (uint8_t *)&buffe, sizeof(buffe));

    buffe[0] = {0x0B};
  for( int i = 1; i<20; i++){
    buffe[i] = glovedata[i+19];
  }
  ble.updateCharacteristicValue(characteristic3.getValueAttribute().getHandle(), (uint8_t *)&buffe, sizeof(buffe));
  
}

/***********************
 * Sense Functions
 */
void tickerSENSE(void)
{
  //Signal = analogRead(pulsePin);              // read the Pulse Sensor
  thumb   = (uint16_t)(analogRead(ResThumb));  
  primary   = (uint16_t)(analogRead(Resprimary));  
  middle  = (uint16_t)(analogRead(ResMiddle)); 
  annular = (uint16_t)(analogRead(ResAnnular));
  pinky   = (uint16_t)(analogRead(ResPinky));  
//  ble.updateCharacteristicValue(characteristic3.getValueAttribute().getHandle(), (uint8_t *)&glovedata, sizeof(glovedata));
}

/**
 * @brief  Set advertisement
 */
void setAdvertisement(void) {
  // A list of Advertising Data types commonly used by peripherals.
  //   FLAGS                              = 0x01, // Flags, refer to GapAdvertisingData::Flags_t.
  //   INCOMPLETE_LIST_16BIT_SERVICE_IDS  = 0x02, // Incomplete list of 16-bit Service IDs.
  //   COMPLETE_LIST_16BIT_SERVICE_IDS    = 0x03, // Complete list of 16-bit Service IDs.
  //   INCOMPLETE_LIST_32BIT_SERVICE_IDS  = 0x04, // Incomplete list of 32-bit Service IDs (not relevant for Bluetooth 4.0).
  //   COMPLETE_LIST_32BIT_SERVICE_IDS    = 0x05, // Complete list of 32-bit Service IDs (not relevant for Bluetooth 4.0).
  //   INCOMPLETE_LIST_128BIT_SERVICE_IDS = 0x06, // Incomplete list of 128-bit Service IDs.
  //   COMPLETE_LIST_128BIT_SERVICE_IDS   = 0x07, // Complete list of 128-bit Service IDs.
  //   SHORTENED_LOCAL_NAME               = 0x08, // Shortened Local Name.
  //   COMPLETE_LOCAL_NAME                = 0x09, // Complete Local Name.
  //   TX_POWER_LEVEL                     = 0x0A, // TX Power Level (in dBm).
  //   DEVICE_ID                          = 0x10, // Device ID.
  //   SLAVE_CONNECTION_INTERVAL_RANGE    = 0x12, // Slave Connection Interval Range.
  //   LIST_128BIT_SOLICITATION_IDS       = 0x15, // List of 128 bit service UUIDs the device is looking for.
  //   SERVICE_DATA                       = 0x16, // Service Data.
  //   APPEARANCE                         = 0x19, // Appearance, refer to GapAdvertisingData::Appearance_t.
  //   ADVERTISING_INTERVAL               = 0x1A, // Advertising Interval.
  //   MANUFACTURER_SPECIFIC_DATA         = 0xFF  // Manufacturer Specific Data.

  // AD_Type_Flag : LE_LIMITED_DISCOVERABLE = 0x01, Peripheral device is discoverable for a limited period of time
  //                LE_GENERAL_DISCOVERABLE = 0x02, Peripheral device is discoverable at any moment
  //                BREDR_NOT_SUPPORTED     = 0x03, Peripheral device is LE only
  //                SIMULTANEOUS_LE_BREDR_C = 0x04, Not relevant - central mode only
  //                SIMULTANEOUS_LE_BREDR_H = 0x05, Not relevant - central mode only
  ble.accumulateAdvertisingPayload(GapAdvertisingData::BREDR_NOT_SUPPORTED | GapAdvertisingData::LE_GENERAL_DISCOVERABLE);
  // Add short name to advertisement
  ble.accumulateAdvertisingPayload(GapAdvertisingData::SHORTENED_LOCAL_NAME,(const uint8_t *)"BLEP", 4);
  // Add complete 128bit_uuid to advertisement
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_128BIT_SERVICE_IDS,(const uint8_t *)uart_base_uuid_rev, sizeof(uart_base_uuid_rev));
  // Add complete device name to scan response data
  ble.accumulateScanResponse(GapAdvertisingData::COMPLETE_LOCAL_NAME,(const uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME));
  ble.accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t*)service1_uuid, sizeof(service1_uuid));

}

void setup() {
  // put your setup code here, to run once
  Serial.begin(115200);
  Serial.println("Start ");
  pinMode(D13, OUTPUT);
  // Init timer task
  tickerUpdate.attach_us(task_handle, 40000);
  tickerUpdateSENSE.attach_us(tickerSENSE, 33333); 
  
  // Init ble
  ble.init();
  ble.onConnection(connectionCallBack);
  ble.onDisconnection(disconnectionCallBack);
  ble.onDataWritten(gattServerWriteCallBack);

  // set advertisement
  setAdvertisement();
  // set adv_type(enum from 0)
  //    ADV_CONNECTABLE_UNDIRECTED
  //    ADV_CONNECTABLE_DIRECTED
  //    ADV_SCANNABLE_UNDIRECTED
  //    ADV_NON_CONNECTABLE_UNDIRECTED
  ble.setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
  // add service
  deviceInfo = new DeviceInformationService(ble, "mobiGlove", "S2F1", "SN1", "hw-rev1", "fw-rev1", "soft-rev1");
  ble.addService(uartService);
  // set device name
  ble.setDeviceName((const uint8_t *)DEVICE_NAME);
  // set tx power,valid values are -40, -20, -16, -12, -8, -4, 0, 4
  ble.setTxPower(4);
  // set adv_interval, 100ms in multiples of 0.625ms.
  ble.setAdvertisingInterval(160);
  // set adv_timeout, in seconds
  ble.setAdvertisingTimeout(0);
  // ger BLE stack version
  Serial.print("BLE stack verison is : ");
  Serial.println(ble.getVersion());
  // start advertising
  ble.startAdvertising();
  Serial.println("start advertising ");
  // Before initializing the IMU, there are a few settings
  // we may need to adjust. Use the settings struct to set
  // the device's communication mode and addresses:
  imu.settings.device.commInterface = IMU_MODE_I2C;
  imu.settings.device.mAddress = LSM9DS1_M;
  imu.settings.device.agAddress = LSM9DS1_AG;
  // The above lines will only take effect AFTER calling
  // imu.begin(), which verifies communication with the IMU
  // and turns it on.
  if (!imu.begin())
  {
    Serial.println("Failed to communicate with LSM9DS1.");
    Serial.println("Double-check wiring.");
    Serial.println("Default settings in this sketch will " \
                  "work for an out of the box LSM9DS1 " \
                  "Breakout, but may need to be modified " \
                  "if the board jumpers are.");
    while (1)
      ;
  }
}

void loop() {
    // We don't know if accelerometer or gyroscope data is
    // available.
    // Use accelAvailable and gyroAvailable to check, then
    // read from those sensors if it's new data.
    if (imu.accelAvailable())
    {
      imu.readAccel();
      glovedata[1] = (imu.ax >> 8);
      glovedata[2] = (imu.ax);
      glovedata[3] = (imu.ay >> 8);
      glovedata[4] = (imu.ay);
      glovedata[5] = (imu.az >> 8);
      glovedata[6] = (imu.az);   
    }   
    if (imu.gyroAvailable()){
      imu.readGyro();
      glovedata[7] = (imu.gx >> 8);
      glovedata[8] = (imu.gx);
      glovedata[9] = (imu.gy >> 8);
      glovedata[10] = (imu.gy);
      glovedata[11] = (imu.gz >> 8);
      glovedata[12] = (imu.gz);  
    }     
    if (imu.magAvailable())
    {
      imu.readMag();
      glovedata[13] = (imu.mx >> 8);
      glovedata[14] = (imu.mx);
      glovedata[15] = (imu.my >> 8);
      glovedata[16] = (imu.my);
      glovedata[17] = (imu.mz >> 8);
      glovedata[18] = (imu.mz);        
    }
    if (imu.tempAvailable())
    {
      imu.readTemp();
      glovedata[19] = (imu.temperature >> 8);
      glovedata[20] = (imu.temperature);      
    }


  thumb   = analogRead(ResThumb);  
  glovedata[21] = (thumb>>8);
  glovedata[22] = (thumb);
  primary   = analogRead(Resprimary);  
  glovedata[23] = (primary>>8);
  glovedata[24] = (primary);
  middle  = analogRead(ResMiddle); 
  glovedata[25] = (middle>>8);
  glovedata[26] = (middle);
  annular = analogRead(ResAnnular);
  glovedata[27] = (annular>>8);
  glovedata[28] = (annular);
  pinky   = analogRead(ResPinky);  
  glovedata[29] = (pinky>>8);
  glovedata[30] = (pinky);
    
  if(thumb   > OpenedThumb)   // Calibration reading and setting the maximum values. This needs you to completely open your hand a few times
  OpenedThumb   = thumb; 
  if(primary   > Openedprimary)
  Openedprimary   = primary;
  if(middle  >  OpenedMiddle)
  OpenedMiddle  = middle;
  if(annular > OpenedAnnular)
  OpenedAnnular = annular;
  if(pinky   > OpenedPinky)
  OpenedPinky   = pinky;
  
  if(thumb   < ClosedThumb)  // Calibration reading and setting the minimum values. This needs you to completely close your hand a few times
  ClosedThumb   = thumb;
  if(primary   < Closedprimary)
  Closedprimary   = primary;
  if(middle  < ClosedMiddle)
  ClosedMiddle  = middle;
  if(annular < ClosedAnnular)
  ClosedAnnular = annular;
  if(pinky   < ClosedPinky)
  ClosedPinky   = pinky;

  glovedata[31] = (OpenedThumb>>8);
  glovedata[32] = (OpenedThumb);
  glovedata[33] = (ClosedThumb>>8);
  glovedata[34] = (ClosedThumb);
  
  thumb   = (uint16_t)(map(thumb  ,ClosedThumb  ,OpenedThumb  ,0,180));  // The analog read has to be readapted in values between 0 and 180 to be used by the servomotors.
  glovedata[35] = (thumb>>8); //since we know thumb is capped at 180 lets only send 8-bits
  primary   = (uint16_t)(map(primary  ,Closedprimary  ,Openedprimary  ,0,180));  // The minimum and maximum values from the calibrations are used to correctly set the analog reads.
  glovedata[36] = (primary>>8);
  middle  = (uint16_t)(map(middle ,ClosedMiddle ,OpenedMiddle ,0,180));
  glovedata[37] = (middle>>8);
  annular = (uint16_t)(map(annular,ClosedAnnular,OpenedAnnular,0,180));  
  glovedata[38] = (annular>>8);
  pinky   = (uint16_t)(map(pinky  ,ClosedPinky  ,OpenedPinky  ,0,180));
  glovedata[39] = (pinky>>8);



  glovedata[48] = (check>>8);
  glovedata[49] = (check);
  
  ble.waitForEvent();
}


