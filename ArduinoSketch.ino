#include <YunServer.h>
#include <Bridge.h>
#include <YunClient.h>
#include "DHT.h"

#define DHTPIN 12
#define DHTTYPE DHT22 

//pin assignment to power the LED to detect dust in sensor
int powerLED = 8; 
int sensorData = 0;
int coAnalogPin = A2;
int ledPin = 12;

//variables used to store the collected sensor data
float DustData;
float VoltageMap;
float DustLevel;
float CoData;
float Humidity;
float CTemperature;
float FTemperature;

DHT dht(DHTPIN, DHTTYPE);
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
  
   dht.begin();
  
   server.noListenOnLocalhost();
   server.begin();
 }

void loop() {
   
   int pin;
   int value;
   
//delay 2s to allow temperature to initialize
   delay(2000); 
   YunClient client = server.accept();
   
//Sample Optical Dust Sensor after 280us delay
   digitalWrite(powerLED,LOW);
   delayMicroseconds(280);
   
//-------- Dust Sensor Data -------------------//
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


//-------- CO Sensor Data --------------------//
    
   CoData = analogRead(coAnalogPin);
    
//-------- Temperature Sensor Data ----------//
   Humidity = dht.readHumidity();
   CTemperature = dht.readTemperature();
   FTemperature = dht.readTemperature(true);

   Serial.print("Humidity: "); 
   Serial.print(Humidity);
   Serial.print(" %\t");
   Serial.print("Temperature: "); 
   Serial.print(CTemperature);
   Serial.print(" *C ");
   Serial.print(FTemperature);
   Serial.print(" *F\t");
   
//if no values are returned after read, print error message
   if (isnan(Humidity) || isnan(CTemperature) || isnan(FTemperature)) {
     Serial.println("Failed to read from DHT sensor!");
     return;
   }

//process incoming client connection from mobile application 
   if (client) {
     process(client);
     client.stop();
   }
    
   delay(1000);
}

//process the client connection to digital/analog read
void process(YunClient client) {
   String command = client.readStringUntil('/');
  
   if(command == "digital") {
     digitalTime(client);
   } 
   if(command == "analog") {
     analogTime(client);   
   }
}

void digitalTime(YunClient client) {
   int pin;
   pin = client.parseInt();
   
   Serial.print("Digital Pin Number: ");
   Serial.print(Humidity);

   client.print(Humidity);
   client.print(" ");
   client.print(CTemperature);
   client.print(" ");
   client.print(FTemperature);

}

void analogTime(YunClient client) {
   int pin;
   pin = client.parseInt();
    
   Serial.print("Analog Pin Number: ");
   Serial.print(pin);
   
   if(pin == 1){
     client.print(DustData);
   }
   if(pin == 2){
     client.print(CoData);
   }

}
