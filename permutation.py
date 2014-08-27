class permutation:
	def __init__(self, value):
		self.data = list(value)
	def get_permutation(self):
		print ("permutation for: ")
		print self.data
		for x in range (0, len(self.data)):
			new = swap(self.data, 0, x)
			print new

def swap(data, x, y):
	temp = data[x]
	data[x] = data[y]
	data[y] = temp
	return data

def main():
	string_data = raw_input("Enter the string: ")
	per = permutation(string_data)	
	per.get_permutation()

if __name__=='__main__':
	main()
