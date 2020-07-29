class node:
	def __init__(self, data):
		self.data = data
		self.Next = None

class linked_list:

	def insert(self, tail, data):
		new_node = node(data)
		tail.Next = new_node
		return new_node
	
	def delete_node(self, node, data):
		while node.Next is not None:
			
			if node.Next.data is data:
				temp = node.Next
				node.Next = node.Next.Next
				del temp	
				return
			node = node.Next
			

	def printll(self, node):
		while node is not None:
			print node.data
			node = node.Next

def main():
	 ll = linked_list()
	 head = node(None)
	 tail = head
	 tail = ll.insert(tail, '3')
	 tail = ll.insert(tail, '5')
	 tail = ll.insert(tail, '7')
	 tail = ll.insert(tail, '8')
	 ll.printll(head)
	 ll.delete_node(head, '7')
	 ll.printll(head)
if __name__ == "__main__":
	main()
