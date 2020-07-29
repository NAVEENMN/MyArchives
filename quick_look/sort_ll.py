import random
class linked_list:
	def __init__(self):
		self.list1 = list()
		self.list2 = list()
	def print_list(self):
		print "list one: "
		print self.list1
		print "list two: "
		print self.list2
	def add(self,list_id, data):
		if list_id == 1:
			self.list1.append(data)
		if list_id == 2:
			self.list2.append(data)
	def delete(self, list_id, data):
		if list_id == 1:
			self.list1.remove(data)
		if list_id == 2:
			self.list2.remove(data)
	def sort_list(self):
		temp = self.list1.pop()
		if (len(self.list2) == 0 ):
			 self.list2.append(temp)
		else:
			temp_val = self.list2.pop()
			count = 0
			while len(self.list1) > 0 :
				while temp < temp_val:
					self.list1.append(self.list2.pop())
					count = count + 1
				self.list2.append(temp)
				while count == 0:
					self.list2.append(self.list1.pop())
					count = count - 1
		
def main():
	print "hello world!!"
	ll = linked_list();
	for x in range(0, 10):
		ll.add(1, random.randint(1,10))
	ll.sort_list()
	ll.print_list()	

if __name__ == '__main__':
	main()
