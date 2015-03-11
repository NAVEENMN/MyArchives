# Author : Naveen Mysore
# UNCC ID: 800812071
# project url : https://github.com/NAVEENMN/normalized_edit_distance 
# USAGE: python ned.py [filea.txt] [fileb.txt]

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys

'''
Integer edit_distance(String, String)
@desc : This functions computes the edit distances of two string
	 It uses only two rows and hence we are not storing the entire table
@param1 : String - input A
@param2 : String - input B
return 	: Integer - edit distance
'''
def edit_distance(s1, s2):
    '''
    Execution expects first parameter to be longest string
    hence if len(s1) is less then s2 just flip it
    '''
    if len(s1) < len(s2):
        return edit_distance(s2, s1)
    '''
    The previous line ensured that s2 is smaller.
    If s2 is null then total cost to convert s1 to s2 will be length of s1
    '''
    if len(s2) == 0:
        return len(s1)
 
    '''
    previous_row and current_row are the two rows with keep tacks of edit distances
    the values held in this rows will be used to get values for next cells
    cell1 and cell2 holds the actual characters from the string
    i and j holds the indices of the cells
    '''
    previous_row = range(len(s2) + 1)
    for i, cell1 in enumerate(s1):
        current_row = [i + 1]
        for j, cell2 in enumerate(s2):
            insertions = previous_row[j + 1] + 1 # computing cost of insertion
            deletions = current_row[j] + 1 # computing cost for deletion
            substitutions = previous_row[j] + (cell1 != cell2) # computing cost for substitution
            current_row.append(min(insertions, deletions, substitutions)) # compute min and insert to next cell
        previous_row = current_row # update the previous row for new row computation
 
    return previous_row[-1] # return the last element in the final row

'''
String lcs(String, String)
@desc : This functions finds the longest common subsequence
	 It uses all rows and columns to backtrack and hence the table is stored
@param1 : String - input A
@param2 : String - input B
return 	: String - longest common subsequence
'''
def lcs(a, b):
	Matrix = [[0 for j in range(len(b)+1)]  for i in range(len(a)+1)] # row 0 and column 0 are initialized to 0
	#setting up the costs for empty string insertions and deletions
	for y in range(0, len(b)+1):
		Matrix[0][y] = y
	for x in range(0, len(a)+1):
		Matrix[x][0] = x
	#computing the entire matrix
	for i, x in enumerate(a, 1):
        	for j, y in enumerate(b, 1):
			insert_cost = Matrix[i-1][j]+1
			delete_cost = Matrix[i][j-1]+1
			if x == y:
				substitute_cost = Matrix[i-1][j-1]
			else:
				substitute_cost = Matrix[i-1][j-1]+1
			cell_cost = min(insert_cost, delete_cost, substitute_cost)
                	Matrix[i][j] = cell_cost
	# uncomment the below section to see the table
	'''
	row = list()
	for x in range(0, len(a)+1):
		for y in range(0, len(b)+1):
			row.append(Matrix[x][y])
		print row
		del row[:]
	'''		
	# backtracking the subsequence from the matrix
	result = ""
	x, y = len(a), len(b)
	while x != 0 and y != 0:
		if a[x-1] == b[y-1]: # charcters match
			result = result+a[x-1]
			x = x-1
			y = y-1
		else:
        		top = Matrix[x-1][y]
			left = Matrix[x][y-1]
			if left <= top:
				y = y-1
			else:
				x = x-1
				
	return result[::-1]#reversing the string

'''
    Void lcs_recursive(String, String, List)
    @desc : This function takes string inputs a and b and a list which holds the result
    @param1 : String - input A
    @param2 : String - input B
    @param3 : List - longest common subsequence in form of a list
    return 	: void
'''
def lcs_recursive(a, b, LCSR):
    # If either of the input is a character then compare it
    # against all characters of other string
	if len(a) == 1:
		for x in range(0, len(b)):
			if a[0] == b[x]:
				LCSR.append(a[0])
	elif len(b) == 1:
		for y in range(0, len(a)):
			if b[0] == a[y]:
				LCSR.append(b[0])

	else:
		#computing the middle row
		middle = (len(a)/2)-1
		if middle == 0:
            		middle = 1 # if middle become zero then pick next row
		#Top Matrix and bottom Matrix setting up the matrix
		TopMatrix = [[0 for j in range(len(b)+1)] for i in range(0,middle+1)] # row 0 and column 0 are initialized to 0
		BottomMatrix = [[0 for j in range(len(b)+1)] for i in range(0,len(a)-middle+1)] # row 0 and column 0 are initialized to 0
		#Top Matrix and Bottom Matrix setting up the costs for empty string insertions and deletions
		for y in range(0, len(b)+1):
			TopMatrix[0][y] = y
		for x in range(0, middle+1):
			TopMatrix[x][0] = x
		for y in range(0, len(b)+1):
			BottomMatrix[0][y] = y
		for x in range(0, len(a)-middle+1):
			BottomMatrix[x][0] = x
		#Computing the TopMatrix
		for i, x in enumerate(a, 1):
        		for j, y in enumerate(b, 1):
				if i <= middle: #first half of matrix
					insert_cost = TopMatrix[i-1][j]+1
					delete_cost = TopMatrix[i][j-1]+1
					if x == y:
						substitute_cost = TopMatrix[i-1][j-1]
					else:
						substitute_cost = TopMatrix[i-1][j-1]+1
					cell_cost = min(insert_cost, delete_cost, substitute_cost)
                			TopMatrix[i][j] = cell_cost
	
		temp = a[middle:]
		c = temp[::-1]
		d = b[::-1]
		#Computing the BottomMatrix
		for i, x in enumerate(c, 1):
        		for j, y in enumerate(d, 1):
				insert_cost = BottomMatrix[i-1][j]+1
				delete_cost = BottomMatrix[i][j-1]+1
				if x == y:
					substitute_cost = BottomMatrix[i-1][j-1]
				else:
					substitute_cost = BottomMatrix[i-1][j-1]+1
				cell_cost = min(insert_cost, delete_cost, substitute_cost)
                		BottomMatrix[i][j] = cell_cost

		# uncomment the below section to see the Top Matrix
		'''
		row = list()
		for x in range(0, middle+1):
			for y in range(0, len(b)+1):
				row.append(TopMatrix[x][y])
			print row
			del row[:]
		print " "
		'''
		# uncomment the below section to see the Bottom Matrix
		'''
		row = list()
		for x in range(0, len(c)+1):
			for y in range(0, len(b)+1):
				row.append(BottomMatrix[x][y])
			print row
			del row[:]
		'''
		f_row = TopMatrix[middle] # This is the forward middle row
		t = BottomMatrix[len(BottomMatrix) - 1] 
		b_row = t[::-1] # This is the Reverse middle row

		#finding mimimum edit distance
		pool = list()
		for x in range(0, len(f_row)):
			pool.append( f_row[x] + b_row[x] )
		end =  pool.index(min(pool)) 
		x_front = ''.join(b[0:end]) # 0 to end 
		y_front = ''.join(a[0:middle]) # 0 to middle
		x_back = ''.join(b[end:])
		y_back = ''.join(a[middle:])
		#x_front, y_front, x_back, y_back split the Whole matrix to two sub matrices
		#They also hold the strings which we will use inside recursive call
		lcs_recursive(x_front, y_front, LCSR)
		lcs_recursive(x_back, y_back, LCSR)	
'''
    Void main(String, String)
    @desc : This is the main function takes file names as string inputs 
    @param1 : String - file a
    @param2 : String - file b
    return 	: void
'''	
def main(filea, fileb):
	#reading input files
	fa = open(filea)
	StringA = str(fa.read())
	fa.close()
	fb = open(fileb)
	StringB = str(fb.read())
	fb.close()
	print "String A : ", StringA
	print "String B : ", StringB
	
	LCSR = list()
	ed = float( edit_distance(StringA, StringB) ) # computing edit distance
	total_length = float( len(StringA) + len(StringB) )
	normalized_edit_distance = (total_length - ed)/(total_length) # computing normalized edit distance
	print "Normalized Edit Distance: - uses only two rows: ", normalized_edit_distance
	LCS = lcs(StringA, StringB)
	lcs_recursive(StringA, StringB, LCSR)
	print "LCS - Stores the entire table: ", LCS
	print "LCS - linear memory recursive version: ", ''.join(LCSR)

if __name__ == "__main__":
	if(len(sys.argv) != 3):
		print "Usage: python ned.py [filea.txt] [fileb.txt]"
	else:
		arguments = sys.argv[1:]
		main(arguments[0], arguments[1])
