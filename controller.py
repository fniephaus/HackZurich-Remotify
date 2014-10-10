

class Controller:

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