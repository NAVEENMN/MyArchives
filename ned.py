# Copyright 2015. Naveen Mysore, Inc. All Rights Reserved.
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

def lcs(a, b):
    lengths = [[0 for j in range(len(b)+1)] for i in range(len(a)+1)]
    # row 0 and column 0 are initialized to 0 already
    for i, x in enumerate(a):
        for j, y in enumerate(b):
            if x == y:
                lengths[i+1][j+1] = lengths[i][j] + 1
            else:
                lengths[i+1][j+1] = max(lengths[i+1][j], lengths[i][j+1])
    # read the substring out from the matrix
    result = ""
    x, y = len(a), len(b)
    while x != 0 and y != 0:
        if lengths[x][y] == lengths[x-1][y]:
            x -= 1
        elif lengths[x][y] == lengths[x][y-1]:
            y -= 1
        else:
            assert a[x-1] == b[y-1]
            result = a[x-1] + result
            x -= 1
            y -= 1
    return result

def lcsr(xstr, ystr):
    """
    >>> lcs('thisisatest', 'testing123testing')
    'tsitest'
    """
    if not xstr or not ystr:
        return ""
    x, xs, y, ys = xstr[0], xstr[1:], ystr[0], ystr[1:]
    if x == y:
        return x + lcsr(xs, ys)
    else:
        return max(lcsr(xstr, ys), lcsr(xs, ystr), key=len)

def main():
	StringA = str(raw_input("Enter String A: "))
	StringB = str(raw_input("Enter String B: "))
	ed = float( edit_distance(StringA, StringB) )
	total_length = float( len(StringA) + len(StringB) )
	normalized_edit_distance = (total_length - ed)/(total_length)
	print "Edit Distance: ", ed
	print "Normalized Edit Distance: ", normalized_edit_distance
	LCS = lcs(StringA, StringB)
	LCSR = lcsr(StringA, StringB)
	print "Longest Common Subsequence: ", LCS
	print "Longest Common Subsequence recursive: ", LCSR
if __name__ == "__main__":
	main()
