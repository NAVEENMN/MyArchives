import datetime
import json
import hashlib
from  error_id import ERRORS
from pymongo import MongoClient

client = MongoClient('localhost', 27017)
db = client.Chishiki

add_params_list = ["mid", "name", "email", "fb_id", "dev_id"]

params = dict()
params["add_params"] = add_params_list

def m_log(sev, func, msg):
	st = str(datetime.datetime.now())
	sev_tp = ["notice: ", "warning: ", "critical: "]
	to_log = st + " "+ sev_tp[sev] + " "+ func +": " + msg
	print "log", to_log
	fh = open("/var/www/html/metster/apis/pf1123py/tests/pylog.txt", "w")
	print "opened"
	fh.write("test")
	fh.close()
	with open("/var/www/html/metster/apis/pf1123py/tests/pylog.txt", 'rb') as f:
		f.write("test")

#this checks if data key-val is ok
def check_payload(db_name, payload):
        status = 1 
	if db_name == "ADB":
        	add_list = params["add_params"]
		data = json.loads(payload) # decode json
		for key in data:
                	if key in add_list:
                        	status = 1
                	else :
                        	return 100011
        return status

#this converts data to mongo compatible
def frame_data(db_name, payload):
	dat = dict()
	if db_name == "ADB":
		status = check_payload(db_name, payload)
		if ERRORS[status] == "M_OK":
        		try:
                		data = json.loads(payload) # decode json
				hobj = hashlib.md5(data["email"])
                		dat["mid"] = hobj.hexdigest()
                		dat["name"] = data["name"]
               			dat["email"] = data["email"]
                		dat["fb_id"] = data["fb_id"]
				dat["dev_id"] = data["dev_id"]
                		data = dat
        		except ValueError as e:
                		print e
                		data = "Error"
		else :
			return status, dat #payload check error
	status = 1
        return status, dat

def insert_to_db(db_name, datatodb):
	status = 1 # M_OK 
	if db_name == "ADB":
		mid = datatodb["mid"]
		#check if duplicate
		cursor = db.accounts.find({"mid": mid})
		i = 0
		for document in cursor:
    			i = i + 1
		if i == 0: # ok to insert
        		try:
                		result = str(db.accounts.insert_one(datatodb))
				if "InsertOneResult" in result:
					status = 1 # M_OK
				else:
					status = 100012
        		except ValueError as e:
				status = 100012 # INSERT_FAILED
		else:
			status =  100013 # DUPLICATE_ENTRY
	return status

def show_db(collections):
        print "in col: " + collections
	if collections == "accounts":	
        	cursor = db.accounts.find()
        	for document in cursor:
                	print(document)














