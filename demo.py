from os.path import join
from xlutils.copy import copy
from xlrd import open_workbook
import os

def setup_workbook(workbook):
	rb = open_workbook(workbook, formatting_info=True, on_demand=True )
	wb = copy(rb)
	p, q = find_boundries(rb)
	return p-1, q-1

def find_boundries(rb):
	sheet1 = rb.sheet_by_index(0)
	xcount = 0
	ycount = 0
	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(0,xcount).value
		xcount = xcount + 1 
	xcount = xcount - 1
	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(ycount,0).value
		ycount = ycount + 1
	ycount = ycount - 1

	return(xcount, ycount)	

def main():
	x_limit, y_limit = setup_workbook('sample.xls')
	print x_limit, y_limit
if __name__ == "__main__" :
	main()
