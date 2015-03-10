#Copyright 2015. Naveen Mysore, Inc. All Rights Reserved.
# 
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
	for y in range(0, len(b)+1):
		Matrix[0][y] = y
	for x in range(0, len(a)+1):
		Matrix[x][0] = x
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
	# backtrack the subsequence from the matrix
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
def lcsr(xstr, ystr):
    if not xstr or not ystr:
        return ""
    x, xs, y, ys = xstr[0], xstr[1:], ystr[0], ystr[1:]
    if x == y:
        return x + lcsr(xs, ys)
    else:
        return max(lcsr(xstr, ys), lcsr(xs, ystr), key=len)
'''
def lcs_recursive(a, b, LCS):
	if len(a) == 1:
		for x in range(0, len(b)):
			if a[0] == b[x]:
				LCS.append(a[0])
	elif len(b) == 1:
		for y in range(0, len(a)):
			if b[0] == a[x]:
				LCS.append(b[0])

	else:
		#computing the middle row
		middle = (len(a)/2)-1
		Matrix = [[0 for j in range(len(b)+1)] for i in range(len(a)+1)] # row 0 and column 0 are initialized to 0

    		for i, x in enumerate(a):
        		for j, y in enumerate(b):
				if i<= middle:
            				if x == y:
                				Matrix[i+1][j+1] = Matrix[i][j] + 1
            				else:
                				Matrix[i+1][j+1] = max(Matrix[i+1][j], Matrix[i][j+1])
		

		
def main():
	StringA = str(raw_input("Enter String A: "))
	StringB = str(raw_input("Enter String B: "))
	LCSR = list()
	ed = float( edit_distance(StringA, StringB) )
	total_length = float( len(StringA) + len(StringB) )
	normalized_edit_distance = (total_length - ed)/(total_length)
	print "Edit Distance: ", ed
	print "Normalized Edit Distance: ", normalized_edit_distance
	LCS = lcs(StringA, StringB)
	lcs_recursive(StringA, StringB, LCSR)
	print "Longest Common Subsequence: ", LCS
	print "Longest Common Subsequence recursive: ", LCSR
if __name__ == "__main__":
	main()
