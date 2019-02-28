/******************************************************************************
Create a voltage divider circuit combining a flex sensor with a 47k resistor.
- The resistor should connect from A0 to GND.
- The flex sensor should connect from A0 to 3.3V
As the resistance of the flex sensor increases (meaning it's being bent), the
voltage at A0 should decrease.

Development environment specifics:
Arduino 1.6.7
******************************************************************************/
#include <CurieBLE.h>

const int FLEX_PIN = A0; // Pin connected to voltage divider output
const int FLEX_PIN2 = A2;

// Measure the voltage at 5V and the actual resistance of your
// 47k resistor, and enter them below:
const float VCC = 4.98; // Measured voltage of Ardunio 5V line
const float R_DIV = 10000.0; // Measured resistance of 3.3k resistor
const float R_DIV2 = 1200000.0; // Measured resistance of 3.3k resistor


// Upload the code, then try to adjust these values to more
// accurately calculate bend degree.
const float STRAIGHT_RESISTANCE = 6000.0; // resistance when straight
const float BEND_RESISTANCE = 20000.0; // resistance at 90 deg

const float STRAIGHT_RESISTANCE2 = 6080.0; 
const float BEND_RESISTANCE2 = 20500.0; 

BLEPeripheral blePeripheral;
BLEService indexService("98d4d182-4ffe-4562-90f0-c1aff5500ed3");
BLEIntCharacteristic 
indexCharacteristic("1a417579-d50f-43ca-86d2-3bbc9eec393f", BLEWrite | BLENotify);
BLEIntCharacteristic 
thumbCharacteristic("381cc05a-6064-4ffb-8146-c71dc21a9b97", BLEWrite | BLENotify);

void setup() 
{
  Serial.begin(9600);
  while(!Serial);
  
  blePeripheral.setLocalName("Smart Glove (Old)");
  blePeripheral.setAdvertisedServiceUuid(indexService.uuid());
  blePeripheral.addAttribute(indexService);
  blePeripheral.addAttribute(indexCharacteristic);
  blePeripheral.addAttribute(thumbCharacteristic);
  
  indexCharacteristic.setValue(0);
  thumbCharacteristic.setValue(0);

  blePeripheral.begin();
  
  pinMode(FLEX_PIN, INPUT);
  pinMode(FLEX_PIN2, INPUT);
}  

void loop() 
{
    // Connects to the android device
  BLECentral cent = blePeripheral.central();

  while(cent.connected())
  {
    sendData();
  }
}

void sendData()
{
  // Read the ADC, and calculate voltage and resistance from it
  int flexADC = analogRead(FLEX_PIN);
  float flexV = flexADC * VCC / 1023.0;
  float flexR = R_DIV * (VCC / flexV - 1.0);
 // Serial.println("Resistance: " + String(flexR) + " ohms");
  thumbCharacteristic.setValue((int)flexR);
  Serial.print(flexR);
  Serial.print("\t");

  int flexADC2 = analogRead(FLEX_PIN2);
  float flexV2 = flexADC2 * VCC / 1023.0;
  float flexR2 = R_DIV * (VCC / flexV2 - 1.0);
  //Serial.println("Resistance: " + String(flexR2) + " ohms");
  indexCharacteristic.setValue((int)flexR2);
  Serial.print(flexR2);
  Serial.print("\t");
  Serial.println("");

  // Use the calculated resistance to estimate the sensor's
  // bend angle:
  //float angle = map(flexR, STRAIGHT_RESISTANCE, BEND_RESISTANCE,
     //              0, 90.0);
                   
  //float angle2 = map(flexR2, STRAIGHT_RESISTANCE2, BEND_RESISTANCE2,
//                   0, 90.0);
  //Serial.println("Bend: " + String(angle2) + " degrees"); //+ "\tBend2: " + String(angle2) + " degrees");
  //Serial.println(String(flexR2)+ ","+ String(angle2));
//Serial.println(angle2);
 

  delay(100);
}

