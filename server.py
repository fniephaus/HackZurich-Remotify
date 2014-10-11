import os
import json
import time
import urllib2

from flask import Flask, request
app = Flask(__name__)

arduino_status = 0
pc_status = 0


@app.route('/')
def index():
	return 'Remote Control Server'


@app.route('/arduino/status')
def arduino():
	global arduino_status
	return str(arduino_status)


@app.route('/arduino/toggle')
def arduino_toggle():
	global arduino_status
	arduino_status = 1 if arduino_status == 0 else 0
	return "Light now at value %s" % arduino_status


@app.route('/car/fast')
def car_fast():
	car_sendSpeed(100)
	return "fast"


@app.route('/car/slow')
def car_slow():
	car_sendSpeed(30)
	return "slow"


@app.route('/pc/status')
def pc():
	global pc_status
	tmp = pc_status
	pc_status = 0
	return str(tmp)


@app.route('/pc/next')
def itunes_next():
	global pc_status
	pc_status = 1
	return "Next song!"


@app.route('/pc/prev')
def itunes_previous():
	global pc_status
	pc_status = 2
	return "Previous song!"


@app.route('/ping')
def car_ping():
	return "success"


@app.route('/start', methods=['POST'])
def car_start():
	car_sendSpeed(30)
	return "start"


@app.route('/sensor')
def car_sensor():
	return "sensor"


def car_sendSpeed(percentage):
	speed = int(percentage * 2.5)
	data = {
		"teamId": "hpi",
		"accessCode": "1234",
		"power": speed,
		"timeStamp": time.time()
	}
	print data

	req = urllib2.Request('http://carrera-relay.beta.swisscloud.io/ws/rest/relay/speed')
	req.add_header('Content-Type', 'application/json')

	response = urllib2.urlopen(req, json.dumps(data))


if __name__ == "__main__":
	port = os.getenv('VCAP_APP_PORT', '5000')
	app.run(host='0.0.0.0', port=int(port), debug=True)
