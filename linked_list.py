class node:
	def __init__(self):
		self.Data = None # holds the data
		self.Next = None # refrence to next node
	
class linked_list:
	def __init__(self):
		self.head = node()
		self.head.Data = None
		self.head.Next = None
		self.cur_node = self.head
	def add_node(self, data):
		new_node = node() # create a new node
		new_node.Data = data
		new_node.Next = self.cur_node 
		self.cur_node = new_node # save current node refrence
	def del_node(self, node, data):
		while node.Data != data:
			node = node.Next
		print node.Data	
	def list_print(self):
		node = self.cur_node
		while node:
			print node.Data
			node = node.Next
def main():
	ll = linked_list()
	ll.add_node(1)
	ll.add_node(3)
	ll.add_node(9)
	ll.list_print()
	#ll.del_node(head, 3)
	#print head.Data

if __name__ == '__main__':
	main()
