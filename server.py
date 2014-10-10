import os

from flask import Flask, request
app = Flask(__name__)


@app.route('/hello_world', methods=["GET"])
def hello_world():
    return 'Hello World!'

@app.route('/echo', methods=["GET", "POST"])
def echo():
    if request.method == 'POST':
    	return request.form['test']

if __name__ == "__main__":
    port = os.getenv('VCAP_APP_PORT', '5000')
    app.run(host='0.0.0.0', port=int(port), debug=True)
