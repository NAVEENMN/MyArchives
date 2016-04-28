import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *
from firebase import Firebase
FIREBASE_URL = "https://metsterios.firebaseio.com/"

client = MongoClient('localhost', 27017)
db = client.Chishiki
#----------------------- functions
def host_event_delete(amid, event_id):
	k = 0
	cursor = db.accounts.find({"mid": amid})
	hosted = list()
	status = 888888
	res = None
	for doc in cursor:
		hosted = list(doc["hosted"])
	if event_id in hosted:
		hosted.remove(event_id)
		db.accounts.update_one({"mid": amid},{"$set": {"hosted":hosted}})
		#db.events.delete_many({"mid": event_id})
		#find all members and del in thuer accoun
		cursor = db.events.find({"mid": event_id})
		members = list()
		for doc in cursor:
			members = list(doc["event_members"])
		for member in members:
			cursor = db.accounts.find({"mid": member})
			joined = list()
			for doc in cursor:
				joined = list(doc["joined"])
			if event_id in joined:
				joined.remove(event_id)
			db.accounts.update_one({"mid": member},{"$set": {"joined":joined}})
		fb_base_url = FIREBASE_URL+"/"+event_id
		fb = Firebase(fb_base_url)
		fb.delete()
		#delete the event
		db.events.remove({"mid": event_id})
		status = 1
		res = "event dropped"
	return status, res
	
def member_event_delete(amid, event_id):
	#del member account, events, firebase
	cursor = db.accounts.find({"mid": amid})
	joined = list()
	status = 888888
	res = None
	for doc in cursor:
		joined = list(doc["joined"])
	if event_id in joined:
		#del it
		joined.remove(event_id)
		db.accounts.update_one({"mid": amid},{"$set": {"joined":joined}})
		fb_base_url = FIREBASE_URL+"/"+event_id
		fb_user_url = fb_base_url +"/users/"+amid
		fb = Firebase(fb_user_url)
		fb.delete()
		cursor = db.events.find({"mid":event_id})
		members = list()
		for doc in cursor:
			members = list(doc["event_members"])
		members.remove(amid)
		db.events.update_one({"mid": event_id},{"$set": {"event_members":members}})
		status = 1
		res = "event dropped"

	return status, res

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
		result = "Duplicate"
	else :	
		dat["mid"] = mid
		emp = list()
		n = ["invites", "hosted", "joined"]
		for key in data:
			if key in n:
				dat[key] = emp
			else:
				if key == "food_pref" or key == "movie_pref":
					pref = data[key]
					dat[key] = pref.lower()
				else:
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
	status = 100014
	result = "not"
	if db.accounts.find({"mid" : mid}).count() >= 1:
		#del all events for this user
		cursor = db.accounts.find({"mid": mid})
		hosted = list()
		joined = list()
		for doc in cursor:
			hosted = list(doc["hosted"])
			joined = list(doc["joined"])
		for event_id in hosted:
			status, res = host_event_delete(mid, event_id)
		for event_id in joined:
			status, res = member_event_delete(mid, event_id)

		result = str(db.accounts.delete_many({"mid": mid}))
		if "DeleteResult" in result:
			status = 1
			result = "deleted"
		else:
			status = 100017
			result = "delete failed"
	else:
		status = 100014
		result = "not found"
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
	if db.accounts.find({"mid" : mid}).count() >= 1:
        	out = db.accounts.find({"mid": mid})
                for records in out:
			fp = data["food_pref"]
			mp = data["movie_pref"]
			lat = data["latitude"]
			lon = data["longitude"]
			if fp != 0 and mp != 0:
				db.accounts.update_one({"mid": mid},{"$set": {"food_pref":fp}})	
				db.accounts.update_one({"mid": mid},{"$set": {"movie_pref":mp}})
			db.accounts.update_one({"mid": mid},{"$set": {"latitude":lat}})
			db.accounts.update_one({"mid": mid},{"$set": {"longitude":lon}})
			result = "updated"
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
		dat["event_members"] = mem
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

def delete_event(jpayload):
	# get all event members and remove events from there accounts
	status = 888888
	res = "oper failed"
	data = json.loads(jpayload) #unpack
	email = data["email"]
	event_id = data["event_id"]
	is_user = False
	is_event = False
	is_host = False
	hobj = hashlib.md5(email)
	amid = hobj.hexdigest()
	if (db.accounts.find({"mid" : amid}).count() >= 1):
		is_user = True
	if (db.events.find({"mid" : event_id}).count() >= 1):
		is_event = True
	if is_user and is_event:
		cursor = db.events.find({"mid": event_id})
		for doc in cursor:
			event_host = doc["host"]
		if event_host == amid:
			is_host = True
		if is_host:
			#del host
			status, res = host_event_delete(amid, event_id)
		else:
			#del member account, events, firebase
			status, res = member_event_delete(amid, event_id)
	else:
		status = 888888
		res = "invalid data in"
	
        return status, res

def find_event(jpayload):
	data = json.loads(jpayload) # decode json
	event_id = data["event_id"]
	res = "None"
	name = None
	if db.events.find({"mid":event_id}).count() >= 1:
		cursor  = db.events.find({"mid":event_id})
		for doc in cursor:
			host_email = doc["host_email"]
			acc = db.accounts.find({"email":host_email})
			for cur in acc:
				name = cur["name"]
			print name
			temp = dict(doc)
			temp["host_name"] = name
			STRING_DATA = dict([(str(k), str(v)) for k, v in temp.items()])
			res = STRING_DATA
		status = 1
	else:
		status = 888888
		res = "event not found"
	print status, res
        return status, res
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
