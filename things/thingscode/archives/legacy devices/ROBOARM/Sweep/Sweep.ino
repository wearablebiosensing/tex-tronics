// Sweep
// by BARRAGAN <http://barraganstudio.com> 
// This example code is in the public domain.


#include <Servo.h> 


Servo servothumb; 
Servo servoindex; 
Servo servomajeure; 
Servo servoringfinger;
Servo servopinky;
Servo servowrist;

void setup() {
  // initialize serial communication:
//  Serial.begin(115200); 
   // initialize the LED pins:
       pinMode(13, OUTPUT);
      for (int thisPin = 0; thisPin < 13; thisPin++) {
        pinMode(thisPin, OUTPUT);
      }
servothumb.attach(3); // Set left servo to digital pin 10 
servoindex.attach(5); // Set right servo to digital pin 9 
servomajeure.attach(6);
servoringfinger.attach(9);
//servopinky.attach(9);
//servowrist.attach(13);
       
}

void loop() {
  // read the sensor:
  //if (Serial.available() > 0) {
    //int inByte = Serial.read();
    // do something different depending on the character received.  
    // The switch statement expects single number values for each case;
    // in this exmaple, though, you're using single quotes to tell
    // the controller to get the ASCII value for the character.  For 
    // example 'a' = 97, 'b' = 98, and so forth:

    digitalWrite(13, HIGH);
    //switch (inByte) {
    delay(10000);
    //case 'a':     
      //servowrist.write(0);
      servothumb.write(180); 
      servoindex.write(0); 
      servomajeure.write(0); 
      servoringfinger.write(0);
      //servopinky.write(0);
      Serial.println("a");
      delay(10000);
   //   break;
   // case 'b':    
      //servowrist.write(170);
      servothumb.write(0); 
      servoindex.write(180); 
      servomajeure.write(180); 
      servoringfinger.write(180);
      //servopinky.write(180);
     // Serial.println("b");
      delay(10000);
    // break;
    // case 'c':     
      //servowrist.write(0);
      servothumb.write(40); 
      servoindex.write(40); 
      servomajeure.write(40); 
      servoringfinger.write(40);
     // servopinky.write(40);
   //   Serial.println("c");
      delay(10000);
   //   break;
   //  case 'd':     
      //servowrist.write(0);
      servothumb.write(90); 
      servoindex.write(180); 
      servomajeure.write(0); 
      servoringfinger.write(0);
    //  servopinky.write(0);
    //  Serial.println("d");
      delay(10000);
   //   break;
     //case 'e':     
      //servowrist.write(0);
      servothumb.write(0); 
      servoindex.write(0); 
      servomajeure.write(0); 
      servoringfinger.write(0);
    //  servopinky.write(0);
    //  Serial.println("e");
      delay(10000);
  //    break;
    // case 'f':     
      servothumb.write(0); 
      servoindex.write(30); 
      servomajeure.write(180); 
      servoringfinger.write(180);
  //    servopinky.write(180);
  //    Serial.println("f");
      delay(10000);
  //    break;
//     case 'g':     
      servothumb.write(90); 
      servoindex.write(180); 
      servomajeure.write(0); 
      servoringfinger.write(0);
   //   servopinky.write(0);
  //    Serial.println("g");
      delay(10000);
  //    break;
    // case 'h':     
      servothumb.write(90); 
      servoindex.write(180); 
      servomajeure.write(180); 
      servoringfinger.write(0);
   //   servopinky.write(0);
  //    Serial.println("h");
      delay(10000);
 //     break;
  //   case 'i':     
      servothumb.write(0); 
      servoindex.write(0); 
      servomajeure.write(0); 
      servoringfinger.write(0);
   //   servopinky.write(180);
  //    Serial2.println("i");
      delay(10000);
  //    break;


      
  
//    default:
//      // turn all the LEDs off:
//      for (int thisPin = 2; thisPin < 7; thisPin++) {
//        digitalWrite(thisPin, LOW);
//      }
 //   }
    digitalWrite(13, LOW); 
 // }
}
// 
//Servo myservo;  // create servo object to control a servo 
//Servo my2;
//Servo my3;
//Servo my4;
//                // a maximum of eight servo objects can be created 
// 
//int pos = 0;    // variable to store the servo position 
// 
//void setup() 
//{ 
//  myservo.attach(9);  // attaches the servo on pin 9 to the servo object 
//  my2.attach(3);
//  my3.attach(5);
//  my4.attach(6);
//} 
// 
// 
//void loop() 
//{ 
//  for(pos = 0; pos < 180; pos += 1)  // goes from 0 degrees to 180 degrees 
//  {                                  // in steps of 1 degree 
//    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
//    my2.write(pos);
//    my3.write(pos);
//    my4.write(180);
//    delay(15);                       // waits 15ms for the servo to reach the position 
//  } 
//  for(pos = 180; pos>=1; pos-=1)     // goes from 180 degrees to 0 degrees 
//  {                                
//    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
//    my2.write(pos);
//    my3.write(pos);
//    my4.write(0);
//    delay(15);                       // waits 15ms for the servo to reach the position 
//  } 
//} 
