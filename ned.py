class edit_distance:
	def __init__(self, stringa, stringb):
		self.A = stringa
		self.B = stringb

def main():
	ed = edit_distance("Hello","World")

if __name__ == "__main__":
	main()
