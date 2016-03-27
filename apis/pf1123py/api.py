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
	if (db.accounts.find({"mid" : amid}).count() >= 1) and (db.events.find({"mid" : event_id}).count() >= 1):
		# we need to update in accounts, events, firebase
		#accounts
		records = db.accounts.find({"mid" : amid})
		joined = list()
		for doc in records:
			joined = doc["joined"]
		if joined[0] == "none":
			del joined[:]
			joined.append(event_id)
		else:
			joined.append(event_id)
		joined = list(set(joined))
		db.accounts.update_one({"mid": amid},{"$set": {"joined":joined}})
		res = "joined"
		status = 1
	else:
		res = "join fail"
		status = 100014
	return status, res

def main(operid, payload):
	status = 999999
	res = None
	if int(operid) == 9000:#find_food
		res = find_food(payload)
		status = 1
	if int(operid) == 8000:#accept_invite
		status, res = accept_invite(payload)
	return status, res

if __name__ == "__main__":
	main()
