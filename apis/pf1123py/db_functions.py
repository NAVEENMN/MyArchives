import datetime
import json
import hashlib
from  error_id import ERRORS
from pymongo import MongoClient
import get_yelp_ranking_copy as yr
import urllib2
from lookups import *

client = MongoClient('localhost', 27017)
db = client.Chishiki

#this checks if data key-val is ok
def check_payload(tb_name, jpayload):
        status = 1 
	if tb_name == "ADB":
        	add_list = params["add_params"]
	if tb_name == "MOV":
		add_list = params["mov_params"]
        if tb_name == "EVNT":
                add_list = params["evnt_params"]
		
        data = jpayload
        for key in data:
        	if key in add_list:
                	status = 1
               	else :
                        return 100011
        return status

#this converts data to mongo compatible
def frame_data(tb_name, payload):
	status = 100015 #"INVALID_TABLE"
	dat = dict()
	jpayload = json.loads(payload)
#-------- INSERT TO ADB B
	if tb_name == "ADB":
		# check if data exists if yes... dont frame return error.
		status = check_payload(tb_name, jpayload)
		if db.accounts.find({"mid": mid}).count >= 1:
			return 100013, dat
		else: 
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
#----------- INSERT TO ADB E
	if tb_name == "MOV":
		status = 1#check_payload(tb_name, payload) #new for url
		if ERRORS[status] == "M_OK":
			try:
				keys = ["Title", "Year", "Rated", "Released", "Runtime", "Genre", "Poster", "imdbRating"]
				data = json.loads(payload) # decode json
				url = data['url']
				jdata = urllib2.urlopen(url).read()
				data = json.loads(jdata)
 				hobj = hashlib.md5(data["Title"]+data["Year"])
				dat["mid"] = hobj.hexdigest()
				for key in data:
					if key in keys:
						dat[key] = data[key]
				status = 1
			except ValueError as e:
				print e
				status = 100011 # INVALID_INPUT
				dat = "Error"
		else:
			return status, dat

	if tb_name == "THR":
		data = json.loads(payload)
		status = 1
		hobj = hashlib.md5(data['key'])
		dat["mid"] = hobj.hexdigest()
		for key in data:
			dat[key] = data[key]

	if tb_name == "EVNT":
		status = check_payload(tb_name, payload)
		if ERRORS[status] == "M_OK":
			data = json.loads(payload) # decode json
			email = data["host_email"]
			hobj = hashlib.md5(email)
			umid = hobj.hexdigest()
			i = 0
			cursor = db.accounts.find({"mid": umid})
			for document in cursor:
				i = i + 1
			if i == 0: # user not exist
				status = 100016
			else :
				cursor = db.events.find({"host": umid}) #number of events hosted by host
				i = 0
				evid = 0
				evid_list = list()
				for document in cursor:
					eventid = document['mid']
					eid = eventid.split("--")
					evid_list.append(int(eid[2]))
				evid_list.sort()
				evid = len(evid_list) # number of event gives new event id
				for x in range (0, len(evid_list)): # any evid missing
					if x not in evid_list:
						evid = x
				#this evid doesnt exist so we can use it
				cursor = db.accounts.find({"mid": umid})
				for document in cursor:
					fb_id =  document["fb_id"]
					event_mid = str(fb_id)+"--event--"+str(evid)
					dat["mid"] = event_mid
				dat["host"] = umid
				for key in data:
					dat[key] = data[key]
				status = 1
		else:
			return status, dat
	
	return status, dat

def insert_to_db(db_name, datatodb):
	tbls = ["ADB", "MOV", "EVNT", "THR"]
	res = None
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
	if db_name == "EVNT":
		cursor = db.events.find({"mid": mid})
	if db_name == "THR":
		cursor = db.theater.find({"mid": mid})
	i = 0
	for document in cursor:
    		i = i + 1
	if i == 0: # ok to insert
        	try:
			if db_name == "ADB":
                		result = str(db.accounts.insert_one(datatodb))
			if db_name == "MOV":
				result = str(db.movies.insert_one(datatodb))
			if db_name == "EVNT":
				result = str(db.events.insert_one(datatodb))
				# we also need to update this in accounts table
				res = mid
			if db_name == "THR":
				result = str(db.theater.insert_one(datatodb))
				res = mid
			if "InsertOneResult" in result:
				status = 1 # M_OK
			else:
				status = 100012

			# additonal work for some tables
			if status == 1 and db_name == "EVNT":
				#update this event in accounts table
				events = list()
				cursor = db.events.find({"host": datatodb['host']})
				for document in cursor:
					events.append(document['mid'])
				db.accounts.update_one({"mid": datatodb['host']},{"$set": {"hosted":events}})
					
        	except ValueError as e:
			status = 100012 # INSERT_FAILED
	else:
		status =  100013 # DUPLICATE_ENTRY
	return status, res

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

def find_food(payload):
	status = 100014
	data = None
	j_data = json.loads(payload)
	event_id = j_data["event_id"] #check if this event exist
	query = j_data["query"]
	if db.events.find({"mid":event_id}).count() > 0:
		status = 1
		data = yr.main(db, query, event_id)
	else :
		status = 100014
		data = None
	return status, data










