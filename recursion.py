class find_fact:
	def __init__(self, value):
		self.data = value
	def get_fact(self, numb):
		if numb > 1:
			return numb * self.get_fact(numb-1)
		else:
			return 1
def main():
	ff = find_fact(3)
	val = int(raw_input("enter the number: "))	
	print ( ff.get_fact(val) )
if __name__=='__main__':
	main()
