from os.path import join
from xlutils.copy import copy
from xlrd import open_workbook
import os

look_up = dict()
look_up['Total Unique applicants'] = 0
look_up['total applicants'] = 0
look_up['repeated'] = 0
look_up['Male'] = 0
look_up['Female'] = 0
look_up['unspecified'] = 0
look_up['White'] = 0
look_up['Afro American'] = 0
look_up['Asian'] = 0
look_up['Hispanic/Latino'] = 0
look_up['others'] = 0
look_up['freshman'] = 0
look_up['junior'] = 0
look_up['sophomore'] = 0
look_up['senior'] = 0

# This function takes care of setting up the workbook and returns boundaries
def setup_workbook(workbook):
	rb = open_workbook(workbook, formatting_info=False, on_demand=True )
	wb = copy(rb)
	p, q = find_boundries(rb)
	return p, q

#function used to find the boundries
def find_boundries(rb):
	sheet1 = rb.sheet_by_index(0)
	xcount = 0
	ycount = 0
	val = 0
	# compute y boundry
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(ycount,0).value
		ycount = ycount + 1
	ycount = ycount - 1

	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(0,xcount).value
		if val in look_up.keys():
			look_up[val] = rb.sheet_by_index(0).cell(ycount-1, xcount).value
		xcount = xcount + 1 
	xcount = xcount - 1
	val = 0

	return(xcount, ycount)	

def main():
	x_limit, y_limit = setup_workbook('sample.xls')
	print look_up
if __name__ == "__main__" :
	main()
