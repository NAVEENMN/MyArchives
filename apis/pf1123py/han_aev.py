import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *
from firebase import Firebase
FIREBASE_URL = "https://metsterios.firebaseio.com/"

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
def update_account(jpayload):
	data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["email"])
	mid = hobj.hexdigest()
	attribute = data["what"]
	if db.accounts.find({"mid" : mid}).count() >= 1:
        	out = db.accounts.find({"mid": mid})
                for records in out:
			if attribute == "food_pref":
				fp = data["food_pref"]
			        db.accounts.update_one({"mid": mid},{"$set": {"food_pref":fp}})	
				result = "updated"
			else:
				status = 100014
				result = "update failed"
                status = 1
        else:
                status = 100014
                result = "couldn`t find"
        return status, result
#------------------------------------------


#------------------------  EVENT OPEARTION
def insert_event(jpayload):
        data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["host_email"])
	mid = hobj.hexdigest()
	dat = dict()
	if db.accounts.find({"mid" : mid}).count() >= 1:
		#host exist
		out = db.accounts.find({"mid": mid})
		for records in out:
			result = records
		user_info = result
		facebook_id = result["fb_id"]
		# find how many events this host 
		cursor = db.events.find({"host": mid})
		# creating a event name
		i = 0
		evid = 0
		evid_list = list()
		for document in cursor:
			eventid = document['mid'] #event main id
			eid = eventid.split("--")
			evid_list.append(int(eid[2])) # event numbers
		evid_list.sort()
		evid = len(evid_list)
		for x in range (0, len(evid_list)):
			if x not in evid_list:
				evid = x
		#this evid doesnt exist so we can use it
		event_mid = str(facebook_id) + "--event--"+str(evid)
		dat["mid"] = event_mid
		dat["host"] = result["mid"]
		for key in data:
			dat[key] = data[key]
		mem = list()
		mem.append(dat["host"])
		dat["members"] = mem
		result = str(db.events.insert_one(dat))
		if "InsertOneResult" in result:
			status = 1
			result = event_mid
			#update this event in accounts table
			events = list()
			cursor = db.events.find({"host": dat['host']})
			for document in cursor:
				events.append(document['mid'])
			db.accounts.update_one({"mid": dat['host']},{"$set": {"hosted":events}})
			#update this event to firebase
			fb_base_url = FIREBASE_URL+"/"+event_mid
			fb_user_url = fb_base_url +"/users/"+dat['host']
			fb = Firebase(fb_user_url)
			to_fb = dict()
			to_fb["node_name"] = user_info["name"]
			to_fb["node_type"] = "host"
			to_fb["latitude"] = user_info["latitude"]
			to_fb["longitude"] = user_info["longitude"]
			to_fb["node_visible"] = True
			fb.put(to_fb)
			
	else:
		status = 100016
		result = "invalid user"
	return status, result
def delete_event(payload):
	# get all event members and remove events from there accounts
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
		if operid == 1003: # find
			status, res = update_account(payload)
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
