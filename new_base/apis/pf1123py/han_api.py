import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *


#------------------------  ACCOUNT OPEARTION
def insert_account(jpayload):
	data = json.loads(jpayload) #unpack
	if "mid" in data.keys():
		status = 100013
	else :
		if db.accounts.find({"mid": data[mid]}).count >= 1:
			status = 100013 # DUPLICATE 
			dat = None
		else:
			hobj = hashlib.md5(data["email"])
			dat["mid"] = hobj.hexdigest()
			for key in data:
				dat[key] = data[key]
			result = str(db.accounts.insert_one(dat))
			if "InsertOneResult" in result:
				status = 1 # M_OK
			else:
				status = 100012
		
	return status, "inserted"

def delete_account(payload):
	return "OK"
def find_account(payload):
	return "OK"
#------------------------------------------


#------------------------  EVENT OPEARTION
def insert_event(payload):
        return "OK"
def delete_event(payload):
        return "OK"
def find_event(payload):
        return "OK"
#------------------------------------------


#-------------------------  FRAME OUTPUT
def frame_output(rid, status, reqdes, msg, error):
        out = dict()
        out["request_id"] = rid
        out["request_des"] = reqdes #later
        out["error_des"] = error
        out["status"] = status
        out["response"] = msg
        return json.dumps(out)


#----------------------- MAIN
# 11 insert movie
# 12 delete movie
# 13 find movie
# 21 insert theater
# 22 delete theater
# 23 find theater
def main(oper, payload):
 	token = str(oper)
	status = 999999
	res = None
	if token[1] == 1: #acounts
		if token[1] == 1: # insert
			status, res = insert_account(payload)
		if token[1] == 2: # delete
			status, res = delete_account(payload)
		if token[1] == 3: # find
			status, res = find_account(payload)
	if token[1] == 2: #events
		if token[1] == 1: # insert
			status, res = insert_event(payload)
                if token[1] == 2: # delete
			status, res = delete_event(payload)
                if token[1] == 3: # find
			status, res = find_event(payload)
	output = frame_output(oper, status, token[1], res, ERRORS[status])
	print output
#---------------------------
if __name__ == "__main__":
	main()
