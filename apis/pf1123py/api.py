import get_yelp_ranking_copy as yr
from pymongo import MongoClient
import json
import hashlib

client = MongoClient('localhost', 27017)
db = client.Chishiki


def find_food(jpayload):
	data = json.loads(jpayload) #unpack
	query = data["query"]
	event_id = data["event_id"]
	response = yr.main(db, query, event_id)
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
		records = db.accounts.find({"mid" : amid})
		joined = list()
		invites = list()
		for doc in records:
			joined = doc["joined"]
			invites = doc["invites"] # remove from invites
		if joined[0] == "none":
			del joined[:]
			joined.append(event_id)
		else:
			joined.append(event_id)
		joined = list(set(joined))
		db.accounts.update_one({"mid": amid},{"$set": {"joined":joined}})
		invites.remove(event_id)
		db.accounts.update_one({"mid": amid},{"$set": {"invites":invites}})
		#events
		records = db.events.find({"mid" : event_id})
		members = list()
		for doc in records:
			members = doc["event_members"]
		if members[0] == "none":
			del members[:]
			members.append(amid)
		else:
			members.append(amid)
		members = list(set(members))
		db.events.update_one({"mid": event_id},{"$set": {"event_members":members}})
		#firebase
		res = "joined"
		status = 1
	else:
		res = "join fail"
		status = 100014
	return status, res

def reject_invite(jayload):
	status = 888888
	res = "ok"

def send_invite(jpayload):
	status = 999999
	res = "ok"
	all_good = False
	data = json.loads(jpayload) #unpack
	hobj = hashlib.md5(data["from_email"])
	from_mid = hobj.hexdigest()
	hobj = hashlib.md5(data["to_email"])
	to_mid = hobj.hexdigest()
	event_id = data["event_id"]
	is_from_ok = db.accounts.find({"mid":from_mid}).count()
	is_to_ok = db.accounts.find({"mid":to_mid}).count()
	is_event_ok = db.events.find({"mid":event_id}).count()
	if is_from_ok >= 1 and is_to_ok>=1 and is_event_ok:
		all_good = True
	if all_good:
		invites = list()
		hosted = list()
		joined = list()
		cursor = db.accounts.find({"mid":to_mid})
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
			invites.remove("none")
			invites.append(event_id)
			db.accounts.update_one({"mid": to_mid},{"$set": {"invites":invites}})
			status = 1
			res = "invited"
		else:
			status = 1
			res = "already invited"
	else:
		status = 999999
		res = "inputed data not found"
	return status, res

def main(operid, payload):
	status = 999999
	res = None
	if int(operid) == 9000:#find_food
		res = find_food(payload)
		status = 1
	if int(operid) == 8000:#accept_invite
		status, res = accept_invite(payload)
	if int(operid) == 8001:#send invite
		status, res = send_invite(payload)
	return status, res

if __name__ == "__main__":
	main()
