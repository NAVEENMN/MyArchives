from os.path import join
from xlutils.copy import copy
from xlrd import open_workbook
import os

def setup_workbook():
	rb = open_workbook('sample.xls', formatting_info=True, on_demand=True )
	wb = copy(rb)
	p, q = find_boundries(rb)
	print p
	print q
	'''
	for x in range(0,10):
		val = rb.sheet_by_index(0).cell(x,0).value
		print val
		answer = raw_input("enter: ")
		wb.get_sheet(0).write(x+1,0,answer)
	'''
	os.system("rm sample.xls")
	wb.save('sample.xls')
def find_boundries(rb):
	sheet1 = rb.sheet_by_index(0)
	xcount = 0
	ycount = 0
	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(0,xcount).value
		xcount = xcount + 1 
		print xcount
	val = 0
	while(val != "end"):
		val = rb.sheet_by_index(0).cell(ycount,0).value
		ycount = ycount + 1
		print ycount
	return(xcount, ycount)	

if __name__ == "__main__" :
	setup_workbook()
