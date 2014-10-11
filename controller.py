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

class Itunes:

	def __init__(self):
		self.music = -1

	def next(self):
		self.music = 0

	def previous(self):
		self.music = 1

	def reset(self):
		self.music = -1

	def getMusic(self):
		return str(self.music)

class Controller:

	def __init__(self):
		self.devices = {}
		self.nextFreeId = 0
		self.addDevice("Arduino")		# Add an Arduino device as first device (id = 0) by default
		self.addDevice("Itunes")

	def addDevice(self, type):
		if type == "Arduino":
			id = self.nextFreeId
			self.devices[id] = Arduino()
			self.nextFreeId += 1
			return id
		elif type == "Itunes":
			id = self.nextFreeId
			self.devices[id] = Itunes()
			self.nextFreeId += 1
			return id

	def getDevice(self, id):
		try:
			return self.devices[id]
		except KeyError:
			return "KeyError"