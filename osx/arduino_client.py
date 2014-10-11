import serial
import urllib2
import time 

ser = serial.Serial('/dev/tty.usbmodemfd121', 9600)

while True:
    try:
        state = urllib2.urlopen("http://hackzurich.beta.scapp.io/arduino/status").read()
        ser.write(state)
        print state
        time.sleep(0.5)
    except KeyError:
        print 'down?'
        time.sleep(0.5)
