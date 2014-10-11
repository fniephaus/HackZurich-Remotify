#include <Ethernet.h>
#include <SPI.h>

int ledPin = 8;

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

EthernetClient client;

void setup()
{
  pinMode(ledPin, OUTPUT);
  Serial.begin(9600);
  
  while (Ethernet.begin(mac) != 1)
  {
    Serial.println("Error getting IP address via DHCP, trying again...");
    delay(1000);
  } 
  reconnect();
}

void loop()
{
  char c;
  if (client.available()) {
    c = client.read();
    // Serial.print(c);
  }

  if (!client.connected()) {
    if (c == '1') {
      digitalWrite(ledPin, HIGH);
    } else {
      digitalWrite(ledPin, LOW);
    }
    // Serial.println("disconnecting.");
    client.stop();
    delay(100);
    reconnect();
  }
}

void reconnect()
{
  // Serial.println("connecting...");

  if (client.connect("hackzurich.beta.scapp.io", 80)) {
    // Serial.println("connected");
    client.println("GET /arduino/status HTTP/1.0");
    client.println("Host: hackzurich.beta.scapp.io");
    client.println();
  } else {
    Serial.println("connection failed");
  }
}