#ifndef _H__SMARTGLOVE_

#define BLE_DEVICE_NAME       "SmartGlove"
#define DATA_REFRESH_RATE_MS  500

#define UUID_IMU_SERVICE      0x4000
#define UUID_ACCEL_CHAR       0x4001
#define UUID_GYRO_CHAR        0x4002
#define UUID_MAG_CHAR         0x4003
#define UUID_FLEX_SERVICE     0x4004
#define UUID_THUMB_CHAR       0x4005
#define UUID_INDEX_CHAR       0x4006
#define UUID_MIDDLE_CHAR      0x4007
#define UUID_RING_CHAR        0x4008
#define UUID_PINKY_CHAR       0x4009
#define UUID_SMART_SERVICE    0x400A
#define UUID_DATA_READY_CHAR  0x400B

typedef union imu_data {
  float f;
  uint8_t b[4];  // Float is 4 Bytes
} imu_data_t;

#define IMU_CHECK_KEY         0x49D4

#endif
