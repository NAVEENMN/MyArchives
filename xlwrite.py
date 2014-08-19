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
	for x in range(1,p):
		val = rb.sheet_by_index(0).cell(0,x).value
		print "Input data for " + val
		for y in range(1,q):
			val = rb.sheet_by_index(0).cell(y,0).value
			print val
			answer = raw_input("enter: ")
			wb.get_sheet(0).write(y,x,answer)
		os.system("rm sample.xls")
		print "Saving records"
		wb.save('sample.xls')

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

if __name__ == "__main__" :
	setup_workbook()
