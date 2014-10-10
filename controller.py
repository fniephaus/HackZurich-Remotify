

class Arduino:

	def __init__(self):
		self.light = 0

	def toggleLight(self):
		if self.light == 0:
			self.light = 1
		else:
			self.light = 0

	def getLight(self):
		print self.light
		return str(self.light)

class Controller:

	def __init__(self):
		self.devices = {}
		self.nextFreeId = 0
		self.addDevice("Arduino")		# Add an Arduino device as first device (id = 0) by default

	def addDevice(self, type):
		if type == "Arduino":
			id = self.nextFreeId
			self.devices[id] = Arduino()
			self.nextFreeId += 1
			return id

	def getDevice(self, id):
		try:
			return self.devices[id]
		except KeyError:
			return "KeyError"