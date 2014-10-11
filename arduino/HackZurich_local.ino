#include <Adafruit_NeoPixel.h>

int incomingByte = 0;

int ledPin = 6;
#define NUMPIXELS      16
bool ledsOn = 0;
int colorID = 0;
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, ledPin, NEO_GRB + NEO_KHZ800);

void setup() {
  Serial.begin(9600);
  pixels.begin();
  pixels.show();
}

void loop() {
  if (Serial.available() > 0) {
          incomingByte = Serial.read();

          if (incomingByte == 48){
            // Serial.println("idle");
          } else if (incomingByte == 49){
            ledsOn = !ledsOn;
            if (ledsOn) {
              turnOn();
            } else {
              turnOff();
            }
          } else if (incomingByte == 50){
            colorID++;
            colorID = colorID % 3;
            update();
          }
          // Serial.println(incomingByte, DEC);
  }
}

void turnOn()
{
  for(int j=0; j<150; j++) {
    for(int i=0;i<NUMPIXELS;i++){
      switch(colorID){
        case 1:
          pixels.setPixelColor(i, pixels.Color(j,0,0));
          break;
        case 2:
          pixels.setPixelColor(i, pixels.Color(0,0,j));
          break;
        default: 
          pixels.setPixelColor(i, pixels.Color(0,j,0));
      }
    }
    pixels.show();
    delay(5);
  }
}

void turnOff()
{
  for(int j=0; j<150; j++) {
    for(int i=0;i<NUMPIXELS;i++){
      switch(colorID){
        case 1:
          pixels.setPixelColor(i, pixels.Color(150-j,0,0));
          break;
        case 2:
          pixels.setPixelColor(i, pixels.Color(0,0,150-j));
          break;
        default: 
          pixels.setPixelColor(i, pixels.Color(0,150-j,0));
      }
    }
    pixels.show();
    delay(5);
  }
}

void update()
{
  for(int j=0; j<150; j++) {
    for(int i=0;i<NUMPIXELS;i++){
      switch(colorID){
        case 1:
          pixels.setPixelColor(i, pixels.Color(j,150-j,0));
          break;
        case 2:
          pixels.setPixelColor(i, pixels.Color(150-j,0,j));
          break;
        default: 
          pixels.setPixelColor(i, pixels.Color(0,j,150-j));
      }
    }
    pixels.show();
    delay(5);
  }
  pixels.show();
  ledsOn = 1;
}