import sys
import db_functions as df
import numpy as np
import json
from error_id import ERRORS
from req_id import r_id
from numpy import matrix
from numpy import array
from lookups import *
import han_aev as ae
import han_mov as mt
import api as api

# This funtions frames output in json format
def frame_output(rid, status, reqdes, msg, error):
	out = dict()
	if status == 1:
		res = "success"
	else :
		res = "fail"
	out["request_id"] = rid
	out["request_des"] = reqdes #later
	out["error_des"] = error
	out["status"] = res
	out["response"] = msg
	return json.dumps(out)

#-------------------------- INPUT TESTS #perform all tests here
def test_in(op, pay):
	status = 1
        api_oper = [999000, 998000]
	if int(op) in api_oper:
		return 1
	if op is None or pay is None:
		return 999999 # null in
	if len(str(op)) < 3: 
		return  999999 # invalid in
	if int(op[2:]) not in r_id:
		return  999999 # invalid op
	
	try:
		data = json.loads(pay)
		tb_name = table_ids[int(op[1])-1]
		print data, tb_name
		if tb_name == "ADB":
			add_list = params["add_params"]
		if tb_name == "MOV":
                	add_list = params["mov_params"]
        	if tb_name == "EVNT":
                	add_list = params["evnt_params"]
		if tb_name == "THR":
			add_list = params["thr_params"]
		for key in data:
			if key in add_list:
				status = 1
			else :
				print "fail at", key
				return  999999	
	except ValueError as e:
		return 999999

	return status

#--------------------------- MAIN
def main(op, pay):
	result = None
	status = test_in(op, pay)
	print status, pay
	if status == 1:
		operation = str(op)
		operid = int(operation[2:])
		payload = pay
		# do ope
		if operid > 5000: #api
			status, result = api.main(operid, payload)
		else : #mongodb operation
			# check if its movies or not then move to that
			table_id = table_ids[int(operation[1])-1]
			if table_id == "MOV" or table_id == "THR":
				# send to han_mov
				status, result = mt.main(table_id, operid, payload)
			if table_id == "EVNT" or table_id == "ADB":
				# send to db_fun
				status, result = ae.main(table_id, operid, payload)
	else:
		result = None
	output = frame_output(op, status, op[1], result, ERRORS[status])
	return output


if __name__ == "__main__":
        main()







