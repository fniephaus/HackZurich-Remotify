import os
import controller

from flask import Flask, request
app = Flask(__name__)
devices = controller.Controller()

@app.route('/hello_world', methods=["GET"])
def hello_world():
    return 'Hello World!'

@app.route('/echo', methods=["GET", "POST"])
def echo():
    if request.method == 'POST':
    	return request.form['test']

@app.route('/<int:param>/light', methods=["GET"])
def light(param):
	return devices.getDevice(param).getLight()

@app.route('/<int:param>/toggle', methods=["GET"])
def toggle(param):
	device = devices.getDevice(param)
	print device
	if device == "KeyError":
		return "Device is not registered!"
	device.toggleLight()
	return "Light is toggled and now at value " + devices.getDevice(param).getLight()

@app.route('/register', methods=["POST"])
def register():
	if request.form['device'] == 'Arduino':
		return str(devices.addDevice(request.form['device']))
	else:
		return "Device not supported!"

if __name__ == "__main__":
    port = os.getenv('VCAP_APP_PORT', '5000')
    app.run(host='0.0.0.0', port=int(port), debug=True)

   