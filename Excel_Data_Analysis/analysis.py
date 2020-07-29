from os.path import join
from xlutils.copy import copy
from xlrd import open_workbook
import os

def setup_workbook():
	rb = open_workbook('sample.xls', formatting_info=True, on_demand=True )
	wb = copy(rb)
	p, q = find_boundries(rb)
	position = find_position(rb, q)
	position = position - 1
	print p
	print q
	print position
	for x in range(position,p):
		val = rb.sheet_by_index(0).cell(0,x).value
		print "Input data for " + val
		response = raw_input("enter the response: ")
		number_of_response = len(response)
		if number_of_response != q-1 :
			print number_of_response
			print "missing responses"
			exit()
		else:	
			for y in range(1,q):
				if y >= q-6 :
					resp = int(raw_input("enter range"))
					resp = (resp/20.0)*7.0
					wb.get_sheet(0).write(y,x,resp)
				else:
					wb.get_sheet(0).write(y,x,int(response[y-1]))
			wb.get_sheet(0).write(y+1,x+1,"done")
			wb.get_sheet(0).write(y+1,x,"")
			os.system("rm sample.xls")
			print "Saving records"
			wb.save('sample.xls')
	
def find_position(rb, maxy):
	sheet1 = rb.sheet_by_index(0)
	position = 0
	value = 0
	while(value != "done"):
		value = rb.sheet_by_index(0).cell(maxy, position).value
		position = position + 1
	return(position)

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
