class Node:
 
      def __init__(self,info): #constructor of class
 
          self.info = info  #information for node
          self.left = None  #left leef
          self.right = None #right leef
          self.level = None #level none defined
 
      def __str__(self):
 
          return str(self.info) #return as string
 
 
class searchtree:
 
      def __init__(self): #constructor of class
 
          self.root = None
 
 
      def create(self,val):  #create binary search tree nodes
 
          if self.root == None:
 
             self.root = Node(val)
 
          else:
 
             current = self.root
 
             while 1:
 
                 if val < current.info:
 
                   if current.left:
                      current = current.left
                   else:
                      current.left = Node(val)
                      break;      
 
                 elif val > current.info:
                 
                    if current.right:
                       current = current.right
                    else:
                       current.right = Node(val)
                       break;      
 
                 else:
                    break 
      
      def delete(self,node, val):
      	   
      	   if node is not None:
      	   	self.delete(node.left, val)
      	   	if( node.info == val ):
      	   		print "deleting..: ", node.info
      	   		del node
      	   		return 
 		self.delete(node.right, val)
 		
      	   
      def inorder(self,node):
            
           if node is not None:   
              self.inorder(node.left)
              print node.info
              self.inorder(node.right)
 
                        
tree = searchtree()     
arr = [8,3,1,6,4,7,10,14,13]
for i in arr:
    tree.create(i)
    
print 'Inorder Traversal before'
tree.inorder(tree.root) 
tree.delete(tree.root, 10)
print 'Inorder Traversal After'
tree.inorder(tree.root)
