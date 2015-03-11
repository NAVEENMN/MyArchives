# Implementation of Longest common subsequence by calculating edit distances

Author : Naveen Mysore
UNCC ID : 800812071

------------------------------------
This the project done as part of my course requirement for Algorithms and data structures class at UNCC
Spring 2015

This is the implementation of finding longest common subsequence by

• Normalized Edit Distance Algorithm
• LCS Algorithm – stores the entire table
• LCS Algorithm – linear memory recursive version

1. Implementation of the algorithm for normalized edit distance.
This function takes two sequences and computes the normalized edit distance between them.
This function uses only two rows so that the table is not stored in memory.

functions used : edit_distance, main

2. Implementation of the LCS algorithm that stores the entire table.
This function computes and returns the longest common subsequence of two sequences. This
function is implemented using the basic dynamic programming algorithm that stores the entire table in memory.

functions used : lcs, main

3. Implementation of the LCS algorithm that uses linear memory.
This function computes and returns the longest common subsequence of two sequences. This
function is implemented using the recursive linear memory algorithm.

functions used : lcs_recursive, main

------------------------------------

Descriptions:

For the normalized edit distances first we need to find the edit distances, for this purpose the edit_distance
takes two string inputs and orders based on lenght of the input. then we use only two rows to compute the edit distance. The algorithm for edit distance can be found here (http://en.wikipedia.org/wiki/Levenshtein_distance). Once the edit distance is found we use the formula

(len(a)+len(b)-edit_distance)/(len(a)+len(b))

to compute the normalized edit distance.

Next for LCS Algorithm we do the same logic as above but instead we store all the rows which we be use to backtrack and get the longest common subsequence.
These are the steps used for backtracking

• Start from the bottom right cell of the table.
• If the symbols are equal, push the symbol on to a stack and move to the diagonal top-left
neighbor.
• If the symbols do not match, compare the top and left neighbor and move to the one with
the smaller value. If there is a tie then you should always choose the left neighbor.
This is to ensure consistency with our convention, so that your algorithm’s solution will match
ours.
• Continue backtracking through the table and always push symbols on to the stack if they are
equal.
• The backtracking will stop when you reach the top or left boundary of the table. After you
are done, pop the symbols off the stack to produce the LCS.


For the last option we do the same logic as above but we calaculate only half of the matrix and other half is reversed and calculated.

These are the steps

• First, we start by computing rows from top to bottom. Each row is computed left to right as
we did in the previous algorithms for computing edit distance. Again, we can use only two
rows, so that the entire table does not need to be stored. However, instead of computing all
the rows, we stop at the middle row, i.e. row ⌊
m
2
⌋ where m is the length of the string aligned
along the left. This row is called the forward middle row because it is computed in the
forward direction.
• Second, we compute the other rows from bottom to top. Each of these rows is computed right
to left (reverse direction). Note that the cells three neighbors are now inverted. Before a
new cell can be computed, its right neighbor, bottom neighbor, and diagonal bottom-right
neighbor must be computed. Again, instead of computing all the rows, we stop at the bottom
middle row, i.e. row ⌊
m
2
⌋ + 1. This row is called the reverse middle row.


Once we find the middile splits we can use this recursive logic to compute LCS

void lcs_recursive(const Sequence & X, const Sequence & Y, Sequence & LCS) {
if (X.size() == 1) {
Compare X[0] to each symbol in Y[0, ..., Y.size()-1]
If there is a symbol match push_back X[0] on to LCS
}
else if (Y.size() == 1) {
Compare Y[0] to each symbol in X[0, ..., X.size()-1]
If there is a symbol match push_back Y[0] on to LCS
}
else {
Compute the middle rows as described above.
Find the horizontal (x) and vertical (y) split indices for the table.
Generate Sequence X_front = X[0, ..., x]
Generate Sequence Y_front = Y[0, ..., y]
Generate Sequence X_back = X[x+1, ..., X.size()-1]
Generate Sequence Y_back = Y[y+1, ..., Y.size()-1]
lcs_recursive(X_front, Y_front, LCS);
lcs_recursive(X_back, Y_back, LCS);
}
}

