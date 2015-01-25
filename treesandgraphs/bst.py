#defines a new node
class node:
	def __init__(self, data):
		self.data = data
		self.leftchild = None
		self.rightchild = None

class bst:
	#start from root
	def insert(self, current_node, value):
		if current_node.data is None:
			current_node.data = value
			current_node.rightchild = node(None)
			current_node.leftchild = node(None)
		else:
			if value > current_node.data:#move to right
				self.insert(current_node.rightchild, value)
			else:#move to left
				self.insert(current_node.leftchild, value)
class properties:
	def get_height(self, current_node):
		if current_node.data is None:
			return -1
		else:
			left_height = self.get_height(current_node.leftchild)
			right_child = self.get_height(current_node.rightchild)
			return max(left_height, right_child)+1

class print_tree:
	def inorder(self, current_node):#left->data->right
		if current_node.data is not None:
			self.inorder(current_node.leftchild)
			print current_node.data
			self.inorder(current_node.rightchild)
	def preorder(self, current_node):#data->left->right
		if current_node.data is not None:
			print current_node.data
			self.preorder(current_node.leftchild)
			self.preorder(current_node.rightchild)
	def postorder(self, current_node):#right->left->data
		if current_node.data is not None:
			self.postorder(current_node.rightchild)
			self.postorder(current_node.leftchild)
			print current_node.data
def main():
	#root node
	root = node(3)
	BST = bst()
	pt = print_tree()
	prop = properties()
	root.rightchild = node(None)
	root.leftchild = node(None)
	BST.insert(root, 5)
	BST.insert(root, 7)
	BST.insert(root, 2)
	BST.insert(root, 1)
	BST.insert(root, 4)
	BST.insert(root, 9)
	print "inorder"
	pt.inorder(root)
	print "preorder"
	pt.preorder(root)
	print "postorder"
	pt.postorder(root)
	print "height"
	print prop.get_height(root)
if __name__ == "__main__":
	main()
