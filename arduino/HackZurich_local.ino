#include <Adafruit_NeoPixel.h>

int incomingByte = 0;

int ledPin = 6;
#define NUMPIXELS      16
bool ledsOn = 0;
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
          } else if (incomingByte == 49){
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
          }

          // Serial.println(incomingByte, DEC);
  }
}