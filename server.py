import os

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


if __name__ == "__main__":
	port = os.getenv('VCAP_APP_PORT', '5000')
	app.run(host='0.0.0.0', port=int(port), debug=True)
