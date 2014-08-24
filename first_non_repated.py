class first_non_repeated:
	def __init__(self, value):
		self.data = value
		self.repeated = list()
		self.nonrepeated = list()
	def fetch_first_non_repeated(self):
		hash_map = {}
		dat = self.data
		for x in range(0, len(dat)):
			temp = hash_map.get(dat[x])
			if temp == None:
				hash_map[dat[x]] = list(dat[x])
			else:
				hash_map[dat[x]].append(dat[x])
				self.repeated.append(dat[x])
		for x in range(0, len(dat)):
			temp = hash_map.get(dat[x])
			if len(temp) == 1 :
				self.nonrepeated.append(dat[x])
		print self.repeated
		print self.nonrepeated
def main():
	data = raw_input("enter the string: ")
	ff_obj = first_non_repeated(data)
	ff_obj.fetch_first_non_repeated()
if __name__ == '__main__':
	main()
