#include <OneWire.h>

// base constants
const int INT_MAX = 65535;
const float INVALID_TEMPERATURE = -1000;

// pin definition for pump
const int pinPumpButton = 11;
const int pinPumpOut =    9;

// pin definition for test
const int pinSelfTestButton = 12;
const int pinSelfTestLed =    10;

// pin definition for temperature sensors
const int pinTemperatureInput = 8;
OneWire ds(pinTemperatureInput); 

byte temperatureSensors[3][8];
int temperatureSensorCount = 0;

const int ACTIVE_TIME = 10;

int pumpStateFlag = LOW;
int selfTestStateFlag = 0;
int tick = 0;

void setup() {
  Serial.begin(9600);

  pinMode(pinPumpOut, OUTPUT);      
  pinMode(pinPumpButton, INPUT);     

  pinMode(pinSelfTestLed, OUTPUT);      
  pinMode(pinSelfTestButton, INPUT);     

  for(int i = 0; i<3;i++){
    Serial.println("Search temperature device " + String(i));
    if(ds.search(temperatureSensors[i])){
      Serial.print("Temperature Device " + String(i) +" found: ");
      serialWriteArray(temperatureSensors[i],8);
      Serial.println();
      temperatureSensorCount++;
    }
  }

}

void loop(){

  if(tick == INT_MAX){
    tick = 0;
  }

  //read data from the serial wire
  serialReadData();

  //check if the native button is pressed
  checkPumpButton();
  checkSelfTestButton();

  //activate the pump, if the flag is set
  if(doTick(2)){
    doPumpOutput();
    doSelfTestLed(); 

    // write data to the serial wire
    serialWriteMeasures();
  }

  // update loop variables
  updateLoop();

}

void checkPumpButton(){
  if (digitalRead(pinPumpButton) == HIGH) {     
    pumpStateFlag = ACTIVE_TIME;
  }
}

void doPumpOutput(){
  if(pumpStateFlag > 0){
    digitalWrite(pinPumpOut, HIGH);  
  } 
  else {
    digitalWrite(pinPumpOut, LOW); 
  }
}

void checkSelfTestButton(){
  if (digitalRead(pinSelfTestButton) == HIGH) {     
    selfTestStateFlag = 4;
  }
}

void doSelfTestLed(){
  if(selfTestStateFlag > 0){
    digitalWrite(pinSelfTestLed, selfTestStateFlag % 2 == 0 ? HIGH : LOW);  
  } 
  else {
    digitalWrite(pinSelfTestLed, LOW); 
  }
}

void updateLoop(){

  if(doTick(2)){
    if(pumpStateFlag > 0){
      pumpStateFlag--;
    }

    if(selfTestStateFlag > 0){
      selfTestStateFlag--;
    }
  }

  tick++;
  delay(500);
}

boolean doTick(int count){
  return tick % count == 0;
}

//------------------------------------------------------------
// Temperature functions
//------------------------------------------------------------
float getTemperature(byte* addr){
  //returns the temperature from one DS18S20 in DEG Celsius

  byte data[12];

  if ( OneWire::crc8( addr, 7) != addr[7]) {
    //Serial.println("CRC is not valid!");
    return INVALID_TEMPERATURE;
  }

  if ( addr[0] != 0x10 && addr[0] != 0x28) {
    //Serial.println("Device is not recognized");
    return INVALID_TEMPERATURE;
  }

  ds.reset();
  ds.select(addr);
  ds.write(0x44,1); // start conversion, with parasite power on at the end

  byte present = ds.reset();
  ds.select(addr);    
  ds.write(0xBE); // Read Scratchpad


  for (int i = 0; i < 9; i++) { // we need 9 bytes
    data[i] = ds.read();
  }

  ds.reset_search();

  byte LSB = data[0];
  byte MSB = data[1];
  byte COUNT_REMAIN = data[6];

  int16_t TemperatureSum = ((((MSB << 8) | LSB) & 0xFFFE) << 3) + 12 - COUNT_REMAIN;
  return TemperatureSum * 0.0625;

}

//------------------------------------------------------------
// Serial functions
//------------------------------------------------------------

void serialWriteArray(byte* data, int len){
  for(byte i = 0; i < len; i++) {
    Serial.print("0x");
    if (data[i] < 16) {
      Serial.print('0');
    }
    Serial.print(data[i], HEX);
    if (i < len - 1) {
      Serial.print(", ");
    }
  }
}

void serialReadData() {
  if (Serial.available() > 0) {
    String line = "";
    while(Serial.available() > 0){
      char c = Serial.read();
      line += c;
    }
    //check if pump should be started
    Serial.println("Recived: " + line);

    if( line == "action=pump"){
      pumpStateFlag = ACTIVE_TIME;
    }

    if( line == "action=selftest"){
      selfTestStateFlag = 10;
    }
  }
}

void serialWriteMeasures() {
  Serial.print("[start]");

  // write pump state
  serialWriteMeasure("Pump", pumpStateFlag > 0 ? "1" : "0");

  //write self test state
  serialWriteMeasure("SelfTest", selfTestStateFlag > 0 ? "1" : "0");

  //write temperature of all connected sensors
  for(int i = 0; i < temperatureSensorCount;i++){
    float t = getTemperature(temperatureSensors[i]);
    if(t != INVALID_TEMPERATURE){
      serialWriteMeasure("Temperature" + String(i), t);
    }
  }

  Serial.println("[end]");
}

void serialWriteMeasure(String name, String value){
  Serial.print(name + "=" + value + ";");
}

void serialWriteMeasure(String name, float value){
  Serial.print(name + "=");
  Serial.print(value);
  Serial.print(";");
}