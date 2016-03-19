import datetime
import json
import hashlib
from  error_id import ERRORS
from pymongo import MongoClient

client = MongoClient('localhost', 27017)
db = client.Chishiki

add_params_list = ["mid", "name", "email", "fb_id", "dev_id"]
mov_prams_list = ["mov_id", "mov_name", "release_date", "language", "genre", "year"]

params = dict()
params["add_params"] = add_params_list
params["mov_params"] = mov_prams_list

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
def check_payload(tb_name, payload):
        status = 1 
	if tb_name == "ADB":
        	add_list = params["add_params"]
	if tb_name == "MOV":
		add_list = params["mov_params"]
		
        data = json.loads(payload) # decode json
        for key in data:
        	if key in add_list:
                	status = 1
               	else :
                        return 100011
        return status

#this converts data to mongo compatible
def frame_data(tb_name, payload):
	status = 100015
	dat = dict()
	if tb_name == "ADB":
		status = check_payload(tb_name, payload)
		if ERRORS[status] == "M_OK":
        		try:
                		data = json.loads(payload) # decode json
				hobj = hashlib.md5(data["email"])
                		dat["mid"] = hobj.hexdigest()
                		for key in data:
					dat[key] = data[key]
				status = 1
        		except ValueError as e:
                		print e
				status = 100011 # INVALID_INPUT
                		dat = "Error"
		else :
			return status, dat #payload check error
	if tb_name == "MOV":
		status = check_payload(tb_name, payload)
		if ERRORS[status] == "M_OK":
			try:
				data = json.loads(payload) # decode json
				uq = str(data["mov_name"])+str(data["year"])
				hobj = hashlib.md5(uq)
				dat["mid"] = hobj.hexdigest()
				for key in data:
					dat[key] = data[key]
				status = 1
			except ValueError as e:
				print e
				status = 100011 # INVALID_INPUT
				dat = "Error"
		else:
			return status, dat
	return status, dat

def insert_to_db(db_name, datatodb):
	tbls = ["ADB", "MOV"]
	if db_name not in tbls:
		status = 100015 # INVALID_TABLE 
		return status
	
	mid = datatodb["mid"]
	cursor = None
	#check if duplicate
	if db_name == "ADB": 
		cursor = db.accounts.find({"mid": mid})
	if db_name == "MOV":
		cursor = db.movies.find({"mid": mid})
	i = 0
	for document in cursor:
    		i = i + 1
	if i == 0: # ok to insert
        	try:
			if db_name == "ADB":
                		result = str(db.accounts.insert_one(datatodb))
			if db_name == "MOV":
				result = str(db.movies.insert_one(datatodb))
			if "InsertOneResult" in result:
				status = 1 # M_OK
			else:
				status = 100012
        	except ValueError as e:
			status = 100012 # INSERT_FAILED
	else:
		status =  100013 # DUPLICATE_ENTRY
	return status

def show_db(db_name):
        print "in col: " + collections
	if db_name == "ADB":	
        	cursor = db.accounts.find()
        	for document in cursor:
                	print(document)

def find_db(tb_name, query):
	tbls = ["ADB", "MOV"]
	if tb_name not in tbls:
		status = 100015 # INVALID_TABLE
		return status, None
	
	hobj = hashlib.md5(query)
	mid = hobj.hexdigest()
	cursor = None
	if tb_name == "ADB":
		cursor = db.accounts.find({"mid": mid})
	if tb_name == "MOV":
		cursor = db.movies.find({"mid": mid})
	i = 0
	data = list()
	for document in cursor:
		data.append(document)
		i = i+1
	if i == 0:
		status = 100014
		data = None
	else:
		status = 1
	return status, data 












