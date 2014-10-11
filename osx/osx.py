import time
import urllib2
from appscript import *

iTunes = app('iTunes')

while True:
    try:
        state = urllib2.urlopen("http://hackzurich.beta.scapp.io/pc/status").read()
        if state == '1':
            iTunes.next_track()
        elif state == '2':
            iTunes.back_track()
            iTunes.back_track()
        else:
            print 'sleep'
            time.sleep(0.5)
    except:
        print 'down?'
        time.sleep(0.5)
