from random import randint
#------------------------------------------------------------------------ Variables
global size
global BstHeights
global AvlHeights
size = []
BstHeights = []
AvlHeights = []
#------------------------------------------------------------------------ BST Section start
class BstNode(): # node for Binary search tree
    def __init__(self, key): # Constructor : intialize the values
        self.key = key
        self.left = None 
        self.right = None 

class BSTTree(): # main class for Binary search tree
    def __init__(self, *args): # Constructor : intialize the values
        self.node = None 
        self.height = -1 # set intial height to -1 and it will be updated when node is created  
        
        if len(args) == 1:# If only one data is sent push it 
            for i in args[0]: 
                self.insert(i)
                
    def height(self):# funtion to retrieve height
        if self.node: 
            return self.node.height 
        else: 
            return 0  
    
    def insert(self, key):# funtion to insert the key into the BST
        
        newnode = BstNode(key) # create a new node and intialize the object to a tree
        nnode = self.node 
        
        
        if nnode == None:# setup the root node 
            self.node = newnode 
            self.node.left = BSTTree() #setup its left and right node
            self.node.right = BSTTree()
        
        elif key <= nnode.key: # if the value asked for insertion is  lesser than key check to insert in left sub tree
            self.node.left.insert(key)
            
        elif key > nnode.key: # if the value asked for insertion is  greater than key check to insert in right sub tree
            self.node.right.insert(key)
       
        self.update_heights(False) #update the height informations 
          
    def update_heights(self, recurse=True):
        if not self.node == None: 
            if recurse: 
                if self.node.left != None: 
                    self.node.left.update_heights()
                if self.node.right != None:
                    self.node.right.update_heights()#node traversing logic
            
            self.height = max(self.node.left.height,
                              self.node.right.height) + 1 #fetch the maximum heights from the child nodes and update the current over by one 
        else: 
            self.height = -1 
            
    

    def delete(self, key):
        if self.node != None: 
            if self.node.key == key: 
                print "Deleting" + str(key)  
                if self.node.left.node == None and self.node.right.node == None: # no left or right child
                    self.node = None # go ahead and delete it 
                elif self.node.left.node == None: #if no left child move to right child
                    self.node = self.node.right.node
                elif self.node.right.node == None: #if no right child move to left
                    self.node = self.node.left.node
                
                else:  # here in this case we have both childs
                    replacement = self.smallonrightsub(self.node) #logic find the smallest key in right sub tree and replace 
                    if replacement != None:  
                        self.node.key = replacement.key 
                        self.node.right.delete(replacement.key)
                return  
            elif key < self.node.key: 
                self.node.left.delete(key)  
            elif key > self.node.key: 
                self.node.right.delete(key)
        else: 
            return 

    
    def smallonrightsub(self, node): #logic to find smallest key on right sub tree
        node = node.right.node  
        if node != None: 
            
            while node.left != None:
                if node.left.node == None: 
                    return node 
                else: 
                    node = node.left.node  
        return node  
        
    def inorder_traverse(self): #inorder traversal implementation : move Left, visit node, move Right
        if self.node == None:
            return [] 
        
        inlist = [] 
        l = self.node.left.inorder_traverse()
        for i in l: 
            inlist.append(i) 

        inlist.append(self.node.key)

        l = self.node.right.inorder_traverse()
        for i in l: 
            inlist.append(i) 
    
        return inlist 

    
    def getheight(self):
    	return(self.height)
#----------------------------------------------------------------------------------------------------------- BST section end
#------------------------------------------------------------------------ AVL Section start
class AvlNode(): # node for AVL tree
    def __init__(self, key): # Constructor : intialize the values
        self.key = key
        self.left = None 
        self.right = None 

class AVLTree():
    def __init__(self, *args):
        self.node = None 
        self.height = -1  
        self.balance = 0; 
        
        if len(args) == 1: 
            for i in args[0]: 
                self.insert(i)
                
    def height(self):
        if self.node: 
            return self.node.height 
        else: 
            return 0 
    
    def is_leaf(self):
        return (self.height == 0) 
    
    def insert(self, key):
        
        nnode = self.node 
        newnode = AvlNode(key)
        
        if nnode == None:
            self.node = newnode 
            self.node.left = AVLTree() 
            self.node.right = AVLTree()
        
        elif key <= nnode.key: 
            self.node.left.insert(key)
            
        elif key > nnode.key: 
            self.node.right.insert(key)
        
            
        self.rebalance() 
        
    def rebalance(self):
        self.update_heights(False)
        self.update_balances(False)
        while self.balance < -1 or self.balance > 1: 
            if self.balance > 1:
                if self.node.left.balance < 0:  
                    self.node.left.lrotate() # we're in case II
                    self.update_heights()
                    self.update_balances()
                self.rrotate()
                self.update_heights()
                self.update_balances()
                
            if self.balance < -1:
                if self.node.right.balance > 0:  
                    self.node.right.rrotate() # we're in case III
                    self.update_heights()
                    self.update_balances()
                self.lrotate()
                self.update_heights()
                self.update_balances()


            
    def rrotate(self):
        A = self.node 
        B = self.node.left.node 
        C = B.right.node 
        self.node = B 
        B.right.node = A 
        A.left.node = C 

    
    def lrotate(self):
        A = self.node 
        B = self.node.right.node 
        C = B.left.node 
        self.node = B 
        B.left.node = A 
        A.right.node = C 
        
            
    def update_heights(self, recurse=True):
        if not self.node == None: 
            if recurse: 
                if self.node.left != None: 
                    self.node.left.update_heights()
                if self.node.right != None:
                    self.node.right.update_heights()
            
            self.height = max(self.node.left.height,
                              self.node.right.height) + 1 
        else: 
            self.height = -1 
            
    def update_balances(self, recurse=True):
        if not self.node == None: 
            if recurse: 
                if self.node.left != None: 
                    self.node.left.update_balances()
                if self.node.right != None:
                    self.node.right.update_balances()

            self.balance = self.node.left.height - self.node.right.height 
        else: 
            self.balance = 0 

    def delete(self, key):
        if self.node != None: 
            if self.node.key == key:   
                if self.node.left.node == None and self.node.right.node == None:
                    self.node = None 
                elif self.node.left.node == None: 
                    self.node = self.node.right.node
                elif self.node.right.node == None: 
                    self.node = self.node.left.node
               
                else:  
                    replacement = self.smallonrightsub(self.node)
                    if replacement != None:    
                        self.node.key = replacement.key 
                        self.node.right.delete(replacement.key)
                    
                self.rebalance()
                return  
            elif key < self.node.key: 
                self.node.left.delete(key)  
            elif key > self.node.key: 
                self.node.right.delete(key)
                        
            self.rebalance()
        else: 
            return 

    
    def smallonrightsub(self, node): 
        node = node.right.node  
        if node != None:
            
            while node.left != None:
                if node.left.node == None: 
                    return node 
                else: 
                    node = node.left.node  
        return node 

    def check_balanced(self):
        if self == None or self.node == None: 
            return True
         
        self.update_heights()
        self.update_balances()
        return ((abs(self.balance) < 2) and self.node.left.check_balanced() and self.node.right.check_balanced())  
        
    def inorder_traverse(self):
        if self.node == None:
            return [] 
        
        inlist = [] 
        l = self.node.left.inorder_traverse()
        for i in l: 
            inlist.append(i) 

        inlist.append(self.node.key)

        l = self.node.right.inorder_traverse()
        for i in l: 
            inlist.append(i) 
    
        return inlist 

    
    def getheight(self):
    	return(self.height)
#----------------------------------------------------------------------------------------------------------- AVL section end    	      
if __name__ == "__main__":
	print "This code automatically takes random inputs of the lengths mentioned in the reslut below"
	print "It feeds the same inputs to both Binary search tree and AVL tree."
	print "In the total length insertion is done at probablity of 0.3 and deletion at 0.2 and remaining is spent on searching."
	print "Running..."
	for L in range(100, 10000, 1000):
		length = L
		size.append(L)
		minvalue = 0
		maxvalue = 100 
		avl = AVLTree()
		bst = BSTTree()
		for i in range(0,length):
			data = randint(minvalue,maxvalue)
			if length >= 0.3 * L :
				avl.insert(data)
				bst.insert(data)
			if length == 0.2 * L :
				data = randint(minvalue,maxvalue)
				avl.delete(data)
				bst.delete(data)
				      
		#print "AVL Inorder traversal:", avl.inorder_traverse() 
		AvlHeights.append(avl.getheight())
		#print "BST Inorder traversal:", bst.inorder_traverse() 
		#print "BST height of tree: ", bst.getheight()
		BstHeights.append(bst.getheight())
	print "--Result--"
	print "Input data sizes: ", size
	print "BST heights list: ", BstHeights
	print "AVL heights list: ", AvlHeights
