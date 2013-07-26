const int buttonPin = 8;
const int ledPin =    9;
const int ACTIVE_TIME = 10;

int buttonState = 0;
int buttonActive = 0;


void setup() {
  pinMode(ledPin, OUTPUT);      
  pinMode(buttonPin, INPUT);     

  Serial.begin(9600);
}

void loop(){

  //check, if that is available on the serial wire
  readData();

  //check if the native button is pressed
  buttonState = digitalRead(buttonPin);
  if (buttonState == HIGH) {     
    buttonActive = ACTIVE_TIME;
  }

  if(buttonActive > 0){
    digitalWrite(ledPin, HIGH);  
    buttonActive--;
  } 
  else {
    digitalWrite(ledPin, LOW); 
  }

  writeData();

  delay(1000);
}

void readData() {
  if (Serial.available() > 0) {
    String line = "";
    while(Serial.available() > 0){
      char c = Serial.read();
      line += c;
    }

    //check if pump should be started
    Serial.println("Recived: " + line);
    if( line == "Pump=1"){
      buttonActive = ACTIVE_TIME;
    }   
  }
}

void writeData() {
  Serial.print("Pump=");
  if(buttonActive > 0){
    Serial.print("1");
  }
  else{
    Serial.print("0");
  }
  Serial.println("");
}







