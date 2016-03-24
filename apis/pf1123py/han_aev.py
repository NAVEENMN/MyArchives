import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *

client = MongoClient('localhost', 27017)
db = client.Chishiki

#------------------------  ACCOUNT OPEARTION
def insert_account(jpayload):
	data = json.loads(jpayload) #unpack
	keys = data.keys()
	status = 100013
	dat = dict()
	hobj = hashlib.md5(data["email"])
	mid = hobj.hexdigest()
	if db.accounts.find({"mid" : mid}).count() >= 1:
		status = 100013
		dat = None
	else :	
		dat["mid"] = mid
		for key in data:
			dat[key] = data[key]
		result = str(db.accounts.insert_one(dat))
		print "inserted to accounts.."
		if "InsertOneResult" in result:
			status = 1 # M_OK
			result = "inserted"
		else:
			status = 100012
			result = "insert failed"
		
	return status, result
def delete_account(jpayload):
	data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["email"])
	mid = hobj.hexdigest()
	if db.accounts.find({"mid" : mid}).count() >= 1:
		result = str(db.accounts.delete_many({"mid": mid}))
		if "DeleteResult" in result:
			status = 1
			result = "deleted"
		else:
			status = 100017
			result = "delete failed"
	else:
		status = 100014
	return status, result
def find_account(jpayload):
	data = json.loads(jpayload) #unpack
        hobj = hashlib.md5(data["email"])
        mid = hobj.hexdigest()
        if db.accounts.find({"mid" : mid}).count() >= 1:
                out = db.accounts.find({"mid": mid})
		for records in out:
			result = str(records)
		status = 1
        else:
                status = 100014
		result = "couldn`t find"
        return status, result
#------------------------------------------


#------------------------  EVENT OPEARTION
def insert_event(payload):
        return "OK"
def delete_event(payload):
        return "OK"
def find_event(payload):
        return "OK"
#------------------------------------------

#----------------------- MAIN
# 11 insert movie
# 12 delete movie
# 13 find movie
# 21 insert theater
# 22 delete theater
# 23 find theater
def main(table_id, operid, payload):
	status = 999999
	res = None
	if table_id == "ADB": #acounts
		if operid == 1000: # insert
			status, res = insert_account(payload)
		if operid == 1001: # delete
			status, res = delete_account(payload)
		if operid == 1002: # find
			status, res = find_account(payload)
	if table_id == "EVNT": #events
		if operid == 1000: # insert
			status, res = insert_event(payload)
                if operid == 1001: # delete
			status, res = delete_event(payload)
                if operid == 1002: # find
			status, res = find_event(payload)
	return status, res
#---------------------------
if __name__ == "__main__":
	main()
