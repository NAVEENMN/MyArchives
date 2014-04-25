import random, math
#------------------------------------------------------------------------ BST Section start
class BstNode():
    def __init__(self, key):
        self.key = key
        self.left = None 
        self.right = None 

class BSTTree():
    def __init__(self, *args):
        self.node = None 
        self.height = -1   
        
        if len(args) == 1: 
            for i in args[0]: 
                self.insert(i)
                
    def height(self):
        if self.node: 
            return self.node.height 
        else: 
            return 0  
    
    def insert(self, key):
        
        tree = self.node 
        newnode = BstNode(key)
        
        if tree == None:
            self.node = newnode 
            self.node.left = BSTTree() 
            self.node.right = BSTTree()
            print "Inserted key [" + str(key) + "]"
        
        elif key < tree.key: 
            self.node.left.insert(key)
            
        elif key > tree.key: 
            self.node.right.insert(key)
        
        else: 
            print "Key [" + str(key) + "] already in tree."
            
        self.update_heights(False) 
          
            
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
            
    

    def delete(self, key):
        if self.node != None: 
            if self.node.key == key: 
                print "Deleting ... " + str(key)  
                if self.node.left.node == None and self.node.right.node == None:
                    self.node = None # leaves can be killed at will 
                # if only one subtree, take that 
                elif self.node.left.node == None: 
                    self.node = self.node.right.node
                elif self.node.right.node == None: 
                    self.node = self.node.left.node
                
                # worst-case: both children present. Find logical successor
                else:  
                    replacement = self.smallonrightsub(self.node)
                    if replacement != None: # sanity check 
                        print "Found replacement for " + str(key) + " -> " + str(replacement.key)  
                        self.node.key = replacement.key 
                        
                        # replaced. Now delete the key from right child 
                        self.node.right.delete(replacement.key)
                return  
            elif key < self.node.key: 
                self.node.left.delete(key)  
            elif key > self.node.key: 
                self.node.right.delete(key)
        else: 
            return 

    
    def smallonrightsub(self, node):
        ''' 
        Find the smallese valued node in RIGHT child
        ''' 
        node = node.right.node  
        if node != None: # just a sanity check  
            
            while node.left != None:
                print "LS: traversing: " + str(node.key)
                if node.left.node == None: 
                    return node 
                else: 
                    node = node.left.node  
        return node  
        
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
#----------------------------------------------------------------------------------------------------------- BST section end    	      
# Usage example
if __name__ == "__main__": 
    a = BSTTree()
    print "----- Inserting -------"
    inlist = [0, 1, 2, 3, 4, 5, 6, 7]
    for i in inlist: 
        a.insert(i)
    a.delete(4)
    print "Input            :", inlist      
    print "Inorder traversal:", a.inorder_traverse() 
    print "height of tree: ", a.getheight()
