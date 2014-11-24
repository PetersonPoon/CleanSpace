#include <YunServer.h>
#include <Bridge.h>
#include <YunClient.h>

// pin assignment to power the LED to detect dust in sensor
int powerLED = 8; 

// pin for reading analog data set up
int sensorData = 0;

// turn on LED for testing
int ledPin = 12;

//float HumidData;
float DustData;
float CoData;
float VoltageMap;
float DustLevel;

int coAnalogPin = A2;

YunServer server(5555);

void setup() {
// initialize serial communication
  Serial.begin(9600);

  Serial.println("Starting bridge...\n");
     pinMode(8,OUTPUT); 
     pinMode(12, OUTPUT);
     pinMode(1,OUTPUT);
     pinMode(13,OUTPUT);  
      
      
// make contact with the linux processor and turn on LED light when ready     
     digitalWrite(13, LOW);  
  Bridge.begin();
     digitalWrite(13, HIGH); 
  
   server.noListenOnLocalhost();
   server.begin();
 }

void loop() {
   int pin;
   int value;
    
   YunClient client = server.accept();
   
//Sample Optical Dust Sensor after 280us delay
   digitalWrite(powerLED,LOW);
   delayMicroseconds(280);
/////// Read data from pins FOR TESTING PURPOSES ONLY////////////
   DustData = analogRead(sensorData);
   digitalWrite(powerLED,HIGH);
   VoltageMap = (DustData * 3.3) / 1024.0;
   DustLevel = 0.17 * VoltageMap - 0.1;
   
   Serial.print("Raw Signal Value (0-1023): ");;
   Serial.print(DustData);
 
   Serial.print(" - Voltage: ");
   Serial.print(VoltageMap);
 
   Serial.print(" - Dust Density: ");
   Serial.println(DustLevel);
/////////////////////////////////////////////////////////////////
    
    CoData = analogRead(coAnalogPin);
    
    if (client) {
      process(client);
      client.stop();
    }
    
    delay(1000);
}

void process(YunClient client) {
   String command = client.readStringUntil('/');
  
   if(command == "digital") {
 //Read from Sensor with Digital pins
 //digitalTime(client);
   } 
   if(command == "analog") {
     analogTime(client);   
   }
}

void DigitalTime(YunClient client) {
   int pin;
   pin = client.parseInt();

//For testing purpose only      
   Serial.print("Pin Number: ");
   Serial.print(pin);
//value = analogRead(pin);
//client.print(____);
}


void analogTime(YunClient client) {
   int pin;
   pin = client.parseInt();

//For testing purpose only      
   Serial.print("Pin Number: ");
   Serial.print(pin);
//value = analogRead(pin);
//Add if statement if multiple sensors use analog pins
if(pin==1){
   client.print(DustData);
}
   if(pin == 2){
   client.print(CoData);
   }
}
