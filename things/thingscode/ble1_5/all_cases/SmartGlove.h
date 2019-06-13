#ifndef _H__SMARTGLOVE_
#define _H__SMARTGLOVE_

#ifndef TRUE
#define TRUE 1==1
#endif
#ifndef FALSE
#define FALSE 1==0
#endif

#define DEVICE_NAME           "flexonly"  // Local Device Name
#define TXRX_BUF_LEN          20            // BLE Packet Length
#define DATA_REFRESH_RATE_MS  8           // Delay between data collection (milliseconds)
#define DATA_PER_PACKET       4             // Number of data points sent/packet
#define PACKET_LENGTH         10

typedef union _sg_time {
  uint8_t b[4];
  long value;
} sg_time_t;                                // Container for Timestamp

typedef union _sg_dif {
  uint8_t b[2];
  uint16_t value;
} sg_dif_t;

typedef union _flex_data {
  uint8_t b[2];
  int value;
} flex_data_t;                              // Container for FlexSensor Data

typedef union _imu_data {
  uint8_t b[2];
  int16_t value;
} imu_data_t;                               // Container for IMU Data

#define LSM9DS1_M             0x1E          // SDO_XM Address
#define LSM9DS1_AG            0x6B          // SDO_G Address

#endif
