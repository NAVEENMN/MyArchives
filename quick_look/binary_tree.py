class node:
	def __init__(self, data):
		self.data = data
		self.left = None
		self.right = None
	def insert(self, data):
		if data < self.data: # we have to move to left
			if self.left is None:
				self.left = node(data)
			else:
				self.left.insert(data)
		else: # move to right node
			if self.right is None:
				self.right = node(data)
			else:
				self.right.insert(data)
	def peek(self, data, parent = None):
		if data < self.left is None:
			return None, None
		return self.left.peek(data, self)
		elif data > self.right is None:
			return None, None
		return self.right.peek(data, self)
		else:
			return self, parent

def main():
	n = node(None)
	n.insert(2)
	
if __name__ == '__main__':
	main()
