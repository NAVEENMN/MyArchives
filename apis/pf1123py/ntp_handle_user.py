import sys
import db_functions as df
import numpy as np
import json
from error_id import ERRORS
from req_id import r_id
from numpy import matrix
from numpy import array


#lookups
db_type = ["mongo", "firebase"]
table_ids = ["ADB", "MOV", "EVNT", "food", "restauraunts"]
#(databasetype, tablem reqid, data)

def frame_output(rid, status, reqdes, msg, error):
	out = dict()
	out["request_id"] = rid
	out["request_des"] = reqdes #later
	out["error_des"] = error
	out["status"] = status
	out["response"] = msg
	return out
	
def mongo_db_operations(did, tid, rid, payload):
	response = None
	if did == "mongo": #mongo
		#------------- insert
		if rid == 1000:
        		status, minfo = df.frame_data(tid, payload)
			if status == 1:
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



def main(op, pay):
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

		if int(operation) > 5000: #api
			payload = pay
			result = frame_output(int(operation), "success", payload, "Mak", ERRORS[999999])
		else: #db request
			db_id = db_type[int(operation[0])-1]
			table_id = table_ids[int(operation[1])-1]
			operation_id = int(operation[2:])
			if db_id == "mongo": #mongo
				if operation_id >= 1000 and operation_id < 5000: #db_operation
					result = mongo_db_operations(db_id, table_id, operation_id, payload)
			if db_id == "firebase": #firebase
				if operations > 1000 and operations < 5000: #db_operation
					result = mongo_db_operations(2, table_id, operation, payload)
	return result
if __name__ == "__main__":
        main()







