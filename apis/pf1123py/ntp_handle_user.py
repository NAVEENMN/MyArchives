import sys
import db_functions as df
import numpy as np
import json
from error_id import ERRORS
from req_id import r_id
from numpy import matrix
from numpy import array
from lookups import *


# This funtions frames output in json format
def frame_output(rid, status, reqdes, msg, error):
	out = dict()
	out["request_id"] = rid
	out["request_des"] = reqdes #later
	out["error_des"] = error
	out["status"] = status
	out["response"] = msg
	return json.dumps(out)

# Handle all mongo operations here
# 1000 insert
# 1001 delete
# 1002 find
# 1003 update	
def mongo_db_operations(did, tid, rid, payload):
	response = None
	#------------- insert
	if rid == 1000:
        	status, minfo = df.frame_data(tid, payload)
		if status == 1:
			print minfo
               		status, res = df.insert_to_db(tid, minfo)
			if status == 1:
				response = frame_output(rid, "success", rid, res, "none")
			else :
				response = frame_output(rid, "fail", rid, "none", ERRORS[status])
		else :  # data framing error
			response = frame_output(rid, "fail", rid, "none", ERRORS[status])
	#-------------  find
	if rid == 1002:
		response = frame_output(rid, "success", rid, str(payload), "none")
		data = json.loads(payload)
		q = data['query']
		status, data = df.find_db(tid,q)
		if status == 1:
			response = frame_output(rid, "success", r_id[rid], data, ERRORS[status])
		else:
			response = frame_output(rid, "fail", r_id[rid], data, ERRORS[status])
	
	if response == None: # not a valid request
		response = frame_output(rid, "fail", rid, "none", ERRORS[999999])
		return response
	else:
		return response

def api_req_operations(reqid, payload):
	res = None
	if reqid == 9000:
		status, response = df.find_food(payload)
		if status == 1:
			res = frame_output(reqid, "success", "", response, "none")
		else :
			res = frame_output(reqid, "fail", "", "none", ERRORS[status])
	return res
	

def main(op, pay):
	result = None
	if op is None or pay is None:
		result = frame_output(int(op), "fail", "", "NULL INPUT", ERRORS[999999])
		return result
	else:
		if len(str(op)) < 3:
			result = frame_output(int(op), "fail", "", "BAD REQUEST", ERRORS[999999])
			return result

		operation = str(op)
		operid = int(operation[2:])
		payload = pay

		if operid not in r_id:
			result = frame_output(int(operation), "fail", r_id[operid], "BAD REQUEST", ERRORS[999999])
			return result
		if int(operid) > 5000: #api
			payload = pay
			result = api_req_operations(int(operation[2:]), payload)
		else: #db request
			db_id = db_type[int(operation[0])-1]
			table_id = table_ids[int(operation[1])-1]
			operation_id = int(operation[2:])
			if db_id == "mongo": #mongo
				print payload
				if operation_id >= 1000 and operation_id < 5000: #db_operation
					result = mongo_db_operations(db_id, table_id, operation_id, payload)
			if db_id == "firebase": #firebase
				if operations > 1000 and operations < 5000: #db_operation
					result = mongo_db_operations(2, table_id, operation, payload)
	return result


if __name__ == "__main__":
        main()







