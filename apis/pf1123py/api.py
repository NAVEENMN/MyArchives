import get_yelp_ranking_copy as yr
import get_yelp_ranking_private as yp
from pymongo import MongoClient
import json
import hashlib
from firebase import Firebase
import populate_res as pp
import datetime

client = MongoClient('localhost', 27017)
db = client.Chishiki

FIREBASE_URL = "https://metsterios.firebaseio.com/"

def find_food(jpayload):
	data = json.loads(jpayload) #unpack
        mode = data["search_mode"]
	print "search mode", mode
        if mode == "private":
		query = data["query"]
		email = data["email"]
		response = yp.main(query, email)
        if mode == "public":
		query = data["query"]
		email = data["email"]
		response = yp.main(query, email)
	if mode == "group":
		query = data["query"]
		event_id = data["event_id"]
		if (db.events.find({"mid" : event_id}).count() >= 1):
			response = yr.main(db, query, event_id)
		else:
			response = "invalid event"
	return response

def accept_invite(jpayload):
	data = json.loads(jpayload) #unpack
	email = data["email"]
	event_id = data["event_id"]
   	hobj = hashlib.md5(email)
	amid = hobj.hexdigest()
	is_account = False
	is_event = False
	if (db.accounts.find({"mid" : amid}).count() >= 1):
		is_account = True
	if (db.events.find({"mid" : event_id}).count() >= 1):
		is_event = True
	if is_account and is_event:
		# we need to update in accounts, events, firebase
		#accounts
		name = None
		lat = None
		lon = None
		records = db.accounts.find({"mid" : amid})
		joined = list()
		invites = list()
		for doc in records:
			name = doc["name"]
			lat = doc["latitude"]
			lon = doc["longitude"]
			joined = doc["joined"]
			invites = list(doc["invites"]) # remove from invites
		joined.append(event_id)
		joined = list(set(joined))
		db.accounts.update_one({"mid": amid},{"$set": {"joined":joined}})
		if event_id in invites:
			invites.remove(event_id)
			db.accounts.update_one({"mid": amid},{"$set": {"invites":invites}})
			#events
			records = db.events.find({"mid" : event_id})
			members = list()
			for doc in records:
				members = list(doc["event_members"])
			members.append(amid)
			members = list(set(members))
			db.events.update_one({"mid": event_id},{"$set": {"event_members":members}})
			#firebase
			fb_base_url = FIREBASE_URL+"/"+event_id
			fb_user_url = fb_base_url +"/users/"+amid
			fb = Firebase(fb_user_url)
			to_fb = dict()
			to_fb["node_name"] = name
			to_fb["node_type"] = "member"
			to_fb["latitude"] = lat
			to_fb["longitude"] = lon
			to_fb["node_visible"] = True
			fb.put(to_fb)
			res = "joined"
			status = 1
		else:
			res = "not invited"
			status = 888888
	else:
		if is_account:
			invites = list()
			cursor = db.accounts.find({"mid":amid})
			for doc in cursor:
				invites = list(doc["invites"])
			invites.remove(event_id)
			db.accounts.update_one({"mid":amid},{"$set": {"invites":invites}})
		res = "join fail"
		status = 100014
	return status, res

def reject_invite(jpayload):
	status = 888888
	res = "ok"
	data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["email"])
	mid = hobj.hexdigest()
	event_id = data["event_id"]
	is_user = False
	is_event = False
	if db.accounts.find({"mid":mid}).count() >= 1:
		is_user = True
	if db.events.find({"mid":event_id}).count() >= 1:
		is_event = True
	if is_user and is_event:
		invites = list()
		cursor = db.accounts.find({"mid":mid})
		for doc in cursor:
			invites = list(doc["invites"])
		if event_id in invites:
			invites.remove(event_id)
		db.accounts.update_one({"mid": mid},{"$set": {"invites":invites}})
		status = 1
		res = "invite rejected"
	else:
		status = 888888
		res = "invalid input data"
	return status, res

def add_place(jpayload):
	status = 888888
	res = "ok"
	all_good = False
	data = json.loads(jpayload) #unpack
	event_id = data["event_id"]
        place_id = data["place_id"]
    	email = data["email"]
	place_info = json.loads(data["place_info"])
	is_event_ok = db.events.find({"mid":event_id}).count()
	if is_event_ok:
	    #update this place to firebase
		fb_base_url = FIREBASE_URL+"/"+event_id
		fb_user_url = fb_base_url +"/places/"+place_id
                fb = Firebase(fb_user_url)
		to_fb = dict()
		for keys in place_info:
			to_fb[keys] = place_info[keys]
 		usr = list()
		usr.append(email)
		usr = set(usr)
		to_fb["votes"] = usr
		fb.put(to_fb)
		status = 1
		res = "inserted"	
	else:
		status = 888888
		res = "invalid input"
	return 1, "ok"

def send_invite(jpayload):
	status = 999999
	res = "ok"
	all_good = False
	data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["from_email"])
	from_mid = hobj.hexdigest()
	to_mid = data["to_email"] #its facebook id
	event_id = data["event_id"]
        print("send invite to : ")
        print(to_mid)
	is_from_ok = db.accounts.find({"mid":from_mid}).count()
	is_to_ok = db.accounts.find({"fb_id":to_mid}).count()
	is_event_ok = db.events.find({"mid":event_id}).count()
	if is_from_ok >= 1 and is_to_ok>=1 and is_event_ok:
		all_good = True
	if all_good:
		invites = list()
		hosted = list()
		joined = list()
		cursor = db.accounts.find({"fb_id":to_mid})
		for doc in cursor:
			hosted = list(doc["hosted"])
			invites = list(doc["invites"])
			joined = list(doc["joined"])
		if event_id in hosted:
			status = 888888
			res = "cannot self invite"
			return status, res
		if event_id in joined:
			status = 888888
			res = "already joined"
			return status, res
		if event_id not in invites:
			invites = list(set(invites))
			invites.append(event_id)
			db.accounts.update_one({"fb_id": to_mid},{"$set": {"invites":invites}})
			status = 1
			res = "invited"
		else:
			status = 1
			res = "already invited"
	else:
		status = 999999
		res = "inputed data not found"
	return status, res

def get_peeps_try_place(jpayload):
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	place_id = str(data["place_id"])
	cursor  = db.tryout.find({"place_id": place_id})
	people = dict()
	for doc in cursor:
		person = dict()
		person["name"] = doc["name"]
		person["fb_id"] = doc["fb_id"]
		person["message"] = doc["message"]
		person = dict([(str(k), str(v)) for k, v in person.items()])
		jdat = json.dumps(person)
		people[person["fb_id"]] = jdat
	people = dict([(str(k), str(v)) for k, v in people.items()])
	res = people
	status = 1
	return status, res

def try_place(jpayload):
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	place_id = str(data["place_id"])
	message = data["place_id"]
	hobj = hashlib.md5(email+place_id)
        pmid = hobj.hexdigest()
	clk = datetime.datetime.now() #current date
	tdate = clk + datetime.timedelta(2,30) #after two days
	cursor = db.accounts.find({"email" : email})
	name = None
	fb_id = None
	for doc in cursor:
		name = doc["name"]
		fb_id = doc["fb_id"]
	dat["mid"] = pmid
	dat["name"] = name
	dat["fb_id"] = fb_id
	dat["email"] = email
	dat["message"] = message
	dat["pdate"] = clk
	dat["tdate"] = tdate
	dat["place_id"] = place_id
	if (db.tryout.find({"mid": pmid}).count > 0):
		db.tryout.delete_many({"mid": pmid})
		db.tryout.insert_one(dat)
		res = "del and insert"
		status = 1
	else:
		db.tryout.insert_one(dat)	
		res = "insert new"
		status = 1
	return status, res

def populate(jpayload):
	data = json.loads(jpayload)
	query = data["query"]
	latitude = data["latitude"]
	longitude = data["longitude"]
	pp.main(query, latitude, longitude) 
	return 1, "ok"
def main(operid, payload):
	status = 999999
	res = None
	print int(operid)
	if int(operid) == 9000:#find_food
		res = find_food(payload)
		status = 1
	if int(operid) == 8000:#accept_invite
		status, res = accept_invite(payload)
	if int(operid) == 8001:#send invite
		status, res = send_invite(payload)
	if int(operid) == 8002:#reject invite
		status, res = reject_invite(payload)
	if int(operid) == 7000:#add place
		status, res = add_place(payload)
	if int(operid) == 7555:#populate
		print "here.."
		status, res = populate(payload)
  	if int(operid) == 7666:#try place
		status, res = try_place(payload)
	if int(operid) == 7667:#get people for place
		status, res = get_peeps_try_place(payload)
	return status, res

if __name__ == "__main__":
	main()
