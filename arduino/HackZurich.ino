#include <Ethernet.h>
#include <SPI.h>
#include <Adafruit_NeoPixel.h>

int ledPin = 6;
#define NUMPIXELS      16

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

EthernetClient client;

bool ledsOn = 0;

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, ledPin, NEO_GRB + NEO_KHZ800);

void setup()
{
  pinMode(ledPin, OUTPUT);
  Serial.begin(9600);
  pixels.begin();
  pixels.show();
    
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
      if (ledsOn != 1) {
        for(int j=0; j<150; j++) {
          for(int i=0;i<NUMPIXELS;i++){
            pixels.setPixelColor(i, pixels.Color(0,j,0));
          }
          pixels.show();
          delay(5);
        }
        ledsOn = 1;
      }
      // Serial.println("on");
    } else if (c == '0') {
      if (ledsOn != 0) {
        for(int j=0; j<150; j++) {
          for(int i=0;i<NUMPIXELS;i++){
            pixels.setPixelColor(i, pixels.Color(0,150-j,0));
          }
          pixels.show();
          delay(5);
        }
        ledsOn = 0;
      }
      // Serial.println("off");
    }
    // Serial.println("disconnecting.");
    client.stop();
    delay(200);
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