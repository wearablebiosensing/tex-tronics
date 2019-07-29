//LOWPASS

//Global Variables
    int sensorPin = 0;      //pin number to use the ADC
    int sensorValue = 0;    //initialization of sensor variable, equivalent to EMA Y
    float EMA_a = 0.672623801820824;//0.030925070056828;//0.6;      //initialization of EMA alpha
    int EMA_S = 0;          //initialization of EMA S

    int vals[] = {0,0,0,0,0,0,0};
    float b[] = {0.0412535372417206,  0, -0.0825070744834413, 0, 0.0412535372417206, 0, 0};
    float a[] = {1, -2.61893647538050, 3.09978858717532,  -1.85879147444538, 0.513981894219676, 0, 0};
    float wei[] = {.6, .3, .3};
    int peak = 0;
    int old = 0;
    boolean win = false;
    boolean pfound = false;
    
void setup(){
    Serial.begin(115200);           //setup of Serial module, 115200 bits/second
    EMA_S = analogRead(sensorPin);  //set EMA S for t=1
}
 
void loop(){
    sensorValue = analogRead(sensorPin);                //read the sensor value using ADC
    EMA_S = (EMA_a*sensorValue) + ((1-EMA_a)*EMA_S);    //run the EMA
    Serial.print(EMA_S);                              //print digital value to serial

    int deriv = EMA_S - wei[0]*vals[0] - wei[1]*vals[1] - wei[2]*vals[2];

    int secderv = deriv-((b[0]*vals[0] - b[1]*vals[1] - b[2]*vals[2] - b[3]*vals[3] - b[4]*vals[4] - b[5]*vals[5] - b[6]*vals[6])/(a[0]*vals[0] - a[1]*vals[1] - a[2]*vals[2] - a[3]*vals[3] - a[4]*vals[4] - a[5]*vals[5] - a[6]*vals[6]));
    Serial.print("\t");
    Serial.print(secderv);

    //start looking
    if (secderv < -25 && win == false){
      win = true;
    }

    if (secderv < -160 && win == true){
      win = false;
      pfound  = false;
    }

    if(win){
      if (secderv < -105 && secderv > -115 && secderv < old && pfound == false){
        peak = 100;
        pfound = true;
      } else {
        peak = 0;
        pfound = false;
      }
    }
    Serial.print("\t");
    Serial.println(peak);
    
    vals[1] = vals[0];
    vals[2] = vals[1];
    vals[3] = vals[2];
    vals[4] = vals[3];
    vals[5] = vals[4];
    vals[6] = vals[5];
    vals[0] = EMA_S;

    old = secderv;
    
    delay(20);                                          //20ms delay
}
