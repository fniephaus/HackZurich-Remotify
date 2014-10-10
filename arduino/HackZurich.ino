// (c) Copyright 2010-2012 MCQN Ltd.
// Released under Apache License, version 2.0
//
// Simple example to show how to use the HttpClient library
// Get's the web page given at http://<serverName><serverPath> and
// outputs the content to the serial port

#include <SPI.h>
#include <HttpClient.h>
#include <Ethernet.h>
#include <EthernetClient.h>

int ledPin = 8;

const char serverName[] = "hackzurich.beta.scapp.io";
const char serverPath[] = "/0/light";

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xCA };  


void setup()
{
  // initialize serial communications at 9600 bps:
  Serial.begin(9600); 
  pinMode(ledPin, OUTPUT);

  while (Ethernet.begin(mac) != 1)
  {
    Serial.println("Error getting IP address via DHCP, trying again...");
    delay(15000);
  }  
}

void loop()
{
  int err =0;
  
  EthernetClient c;
  HttpClient http(c);
  
  err = http.get(serverName, serverPath);
  if (err == 0)
  {
    // Serial.println("startedRequest ok");
    err = http.responseStatusCode();
    if (err >= 0)
    {
      // Serial.print("Got status code: ");
      // Serial.println(err);

      err = http.skipResponseHeaders();
      if (err >= 0)
      {
        // int bodyLen = http.contentLength();
        // Serial.print("Content length is: ");
        // Serial.println(bodyLen);
        // Serial.println();
        // Serial.println("Body returned follows:");
      
        char c;
        // Whilst we haven't timed out & haven't reached the end of the body
        while ( (http.connected() || http.available()) )
        {
            if (http.available())
            {
                c = http.read();
                if (c == '1'){
                    digitalWrite(ledPin, HIGH);
                }else{
                    digitalWrite(ledPin, LOW);
                }
                break;
            }
            else
            {
                // delay(100);
            }
        }
      }
      else
      {
        Serial.print("Failed to skip response headers: ");
        Serial.println(err);
      }
    }
    else
    {    
      Serial.print("Getting response failed: ");
      Serial.println(err);
    }
  }
  else
  {
    Serial.print("Connect failed: ");
    Serial.println(err);
  }
  http.stop();

}