class node:
	def __init__(self):
		self.Data = None # holds the data
		self.Next = None # refrence to next node
	
class linked_list:
	def __init__(self):
		self.cur_node = None # holds previous node refrence

	def add_node(self, data):
		new_node = node() # create a new node
		new_node.Data = data
		new_node.Next = self.cur_node 
		self.cur_node = new_node # save current node refrence
	def list_print(self):
		node = self.cur_node
		while node:
			print node.Data
			node = node.Next
def main():
	ll = linked_list()
	ll.add_node(1)
	ll.add_node(3)
	ll.list_print()

if __name__ == '__main__':
	main()
