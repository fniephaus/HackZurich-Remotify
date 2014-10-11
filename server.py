import os
import json
import time
import urllib2

from flask import Flask, request
app = Flask(__name__)

arduino_status = 0
pc_status = 0

car_acceleration = False
car_speed = 100
min_speed = 100
max_speed = 200


@app.route('/')
def index():
	return 'Remote Control Server'


@app.route('/arduino/status')
def arduino():
	global arduino_status
	tmp = arduino_status
	arduino_status = 0
	return str(tmp)


@app.route('/arduino/toggle')
def arduino_toggle():
	global arduino_status
	arduino_status = 1
	return "Light toggled"


@app.route('/arduino/next')
def arduino_next():
	global arduino_status
	arduino_status = 2
	return "Next color"


@app.route('/car/fast')
def car_fast():
	global car_acceleration
	car_acceleration = True
	return "fast"


@app.route('/car/slow')
def car_slow():
	global car_acceleration
	car_acceleration = False
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
	return "100"


@app.route('/sensor', methods=['POST'])
def car_sensor():
	global car_speed
	data = request.get_json()
	print data
	if car_acceleration == True:
		car_speed = car_speed + 5
		if car_speed > max_speed:
			car_speed = max_speed
	else:
		car_speed = car_speed - 5
		if car_speed < min_speed:
			car_speed = min_speed
	return str(car_speed)


if __name__ == "__main__":
	port = os.getenv('VCAP_APP_PORT', '5000')
	app.run(host='0.0.0.0', port=int(port), debug=True)
