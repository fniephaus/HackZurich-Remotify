import os
import controller

from flask import Flask, request
app = Flask(__name__)
arduino = controller.Controller()

@app.route('/hello_world', methods=["GET"])
def hello_world():
    return 'Hello World!'

@app.route('/echo', methods=["GET", "POST"])
def echo():
    if request.method == 'POST':
    	return request.form['test']

@app.route('/toggle', methods=["GET"])
def toggle():
	arduino.toggleLight()
	return "Light is toggled"

@app.route('/light', methods=["GET"])
def light():
	return arduino.getLight()

if __name__ == "__main__":
    port = os.getenv('VCAP_APP_PORT', '5000')
    app.run(host='0.0.0.0', port=int(port), debug=True)

   