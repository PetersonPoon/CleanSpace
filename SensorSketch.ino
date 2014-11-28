
int datapin = 12;

void setup()
{ 
  pinMode(datapin, INPUT);
  digitalWrite(datapin, HIGH);
  delay(6000);
  Serial.begin(9600);
}

void loop()
{
  int retryCount;
  
  delay(2000);    
  
  digitalWrite(datapin, LOW);
  pinMode(datapin, OUTPUT); 
  Serial.println("Pull Down value: ");
  Serial.println(digitalRead(datapin)); 
  delayMicroseconds(5000); 
  
  digitalWrite(datapin, HIGH); 
  delay(20);
  
  pinMode(datapin, INPUT);
  Serial.println("Pull Up value: ");
  Serial.println(digitalRead(datapin));
  
  
  Serial.println("Signal Start"); 
  retryCount = 0;
do
  {
    if (retryCount > 20) //(Spec is 20 to 40 us, 25*2 == 50 us)
    {
      Serial.println("DHT_ERROR_NOT_PRESENT");
      break;  
    }
    retryCount++;
    delayMicroseconds(2);
  } while(!digitalRead(datapin));


//  retryCount=0;
//  do
//  {
//    if (retryCount > 100) //(Spec is 20 to 40 us, 25*2 == 50 us)
//    {
//      Serial.println("DHT_ACK_TIME_OUT");
//      break;
//    }
//    retryCount++;
//    Serial.println(retryCount);
//    delayMicroseconds(2);
//  } while(digitalRead(datapin));
  
  int pulseLength = pulseIn(datapin, LOW, 200);
  if (pulseLength == 0) {
    Serial.println("Sensor read failed: Sensor never pulled line LOW after initial request");
  }
  
   delay(3000);
}
