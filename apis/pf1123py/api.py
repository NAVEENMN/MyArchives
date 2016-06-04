import get_yelp_ranking_copy as yr
import get_yelp_ranking_private as yp
from pymongo import MongoClient
import json
import hashlib
from firebase import Firebase
import populate_res as pp
import datetime
import numpy as np
from scipy.spatial import *
from scipy import spatial

client = MongoClient('localhost', 27017)
db = client.Chishiki

FIREBASE_URL = "https://metsterios.firebaseio.com/"


#--------- sigmoid values
sigdic = dict()
sigdic["loc0"] = 0.73105857863
sigdic["loc1"] = 0.710949502625
sigdic["loc2"] = 0.689974481128
sigdic["loc3"] = 0.668187772168
sigdic["loc4"] = 0.645656306226
sigdic["loc5"] = 0.622459331202
sigdic["loc6"] = 0.598687660112
sigdic["loc7"] = 0.574442516812
sigdic["loc8"] = 0.549833997312
sigdic["loc9"] = 0.524979187479
sigdic["loc10"] = 0.5
sigdic["loc11"] = 0.475020812521
sigdic["loc12"] = 0.450166002688
#----------

def add_public_search(query, email):
	data = dict()
	data["email"] = email
	data["query"] = query
	cursor = db.accounts.find({"email":email})
	lat = 0.0
	lon = 0.0
	for cur in cursor:
		lat = cur["latitude"]
		lon = cur["longitude"]
	data["latitude"] = lat
	data["longitude"] = lon
	payload = json.dumps(data)
	post_seach_public(payload)
	print "add public"

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
		add_public_search(query, email)
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
	print ("pin this place")
	status = 888888
	res = "ok"
	all_good = False
        #print jpayload
	data = json.loads(jpayload) #unpack
	event_id = data["event_id"]
        place_id = data["place_id"]
    	email = data["email"]
        print ("here")
	place_info = data["place_info"]
	print place_info
	is_event_ok = db.events.find({"mid":event_id}).count()
	if is_event_ok:
	    #update this place to firebase
		fb_base_url = FIREBASE_URL+"/"+event_id
		fb_user_url = fb_base_url +"/places/"+place_id
                fb = Firebase(fb_user_url)
		to_fb = dict()
		for keys in place_info:
			to_fb[keys] = place_info[keys]
		to_fb["votes"] = str(email) + "-"
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
		#person = dict([(str(k), str(v)) for k, v in person.items()])
		jdat = json.dumps(person)
		people[person["fb_id"]] = jdat
	people = dict([(str(k), str(v)) for k, v in people.items()])
	res = people
	status = 1
	return status, res

def post_seach_public(jpayload):
	dat = dict()
	res = None
	res = "Ok"
	status = 999999
	dat = dict()
	data = json.loads(jpayload)
	email = str(data["email"])
	lat = str(data["latitude"])
	lon = str(data["longitude"])
	query = str(data["query"])
	hobj = hashlib.md5(email+query)
	pmid = hobj.hexdigest()
	clk = datetime.datetime.now() #current date
	cursor = db.accounts.find({"email" : email})
	name = None
	fb_id = None
	for doc in cursor:
		name = doc["name"]
		fb_id = doc["fb_id"]
	dat["mid"] = pmid
	dat["name"] = name
	dat["fb_id"] = fb_id
	dat["pdate"] = clk
   	dat["query"] = query
	dat["latitude"] = float(lat)
	dat["longitude"] = float(lon)
        if (db.querylookup.find({"mid": pmid}).count > 0):
                db.querylookup.delete_many({"mid": pmid})
                db.querylookup.insert_one(dat)
                res = "del and insert"
                status = 1
        else:
                db.querylookup.insert_one(dat)
                res = "insert new"
                status = 1
	return status, res

def get_distance(a, b):
    return distance.euclidean(a, b)

def get_pref_vec(pref):
    #print("get_pref_vec")
    print (pref)
    features = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm']
    pvec = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

    if (len(pref) != 13):
	for x in range(0, len(pvec)):
		key =  "loc"+str(x)
		val = sigdic[key]
		pvec[x] = val
    else:
	for x in range (0, len(pref)):
		key =  "loc"+str(x)
		val = sigdic[key]
		feature = pref[x]
		idx = features.index(feature)
		pvec[idx] = val

    return np.array(pvec)

def get_matchscore(upref, spref):
	uvec = get_pref_vec(upref)
	svec = get_pref_vec(spref)
	result = (1 - spatial.distance.cosine(uvec, svec)) * 5.0
	result = "{0:.1f}".format(result)
	print "match"
	print uvec
	print svec
	print result
	return result

def get_people_public_search(jpayload):
	print "get people"
	ther = 0.5
	res = None
	status = 999999
	data = json.loads(jpayload)
	# user details
	query = str(data["query"])
	email = data["email"]
	# get fid of the user
	cur = db.accounts.find({"email":email})
	usr_fid = None
	usr_fpref = None
	usr_mpref = None
	for doc in cur:
		usr_latitude = float(doc["latitude"])
		usr_longitude = float(doc["longitude"])
		usr_fid = str(doc["fb_id"])
		usr_fpref = str(doc["food_pref"])
		usr_mpref = str(doc["movie_pref"])
	a = [usr_latitude, usr_longitude]
	#find all col for query q
	cur = db.querylookup.find({"query":query})
	print "here.."
	print query
	people = list()
	for doc in cur:
		s_latitude = float(doc["latitude"])
		s_longitude = float(doc["longitude"])
		print doc["name"]
		pdate = doc["pdate"]
		print pdate
		clk = datetime.datetime.now() #current date
		post = "somtime this week"
		print pdate.date
		if clk.day - pdate.day == 0 :#posted today
			diffhour = clk.hour - pdate.hour
			post = str(diffhour) + " hours ago"
			if diffhour == 0:#posted within same hour
				diffmin = clk.minute - pdate.minute
				post = str(diffmin) + " minutes ago"
		b = [s_latitude, s_longitude]
		distancer = get_distance(a, b)
		#map
		distance =int( (43.0 * distancer) / (0.5))
		print distance, "miles"
		#if distancer < ther:
		if True:
			print "found a person"
			s_fbid = doc["fb_id"]
			cur = db.accounts.find({"fb_id": s_fbid})
			usr = dict()
			for doc in cur:
				print doc["name"]
				usr["p_name"] = doc["name"]
				usr["p_gender"] = doc["gender"]
				usr["p_aboutme"] = doc["work"]
				usr["p_fbid"] = doc["fb_id"]
				usr["p_gid"] = doc["gid"]
				p_foodpref = doc["food_pref"]
				p_movpref = doc["movie_pref"]
				print ("getting score for food")
				fscore = get_matchscore(usr_fpref, p_foodpref)
				print("getting score for movies")
				mscore = get_matchscore(usr_mpref, p_movpref)
				usr["p_fmatch"] = str(fscore)
				usr["p_mmatch"] = str(mscore)
				usr["p_email"] = doc["email"]
				usr["p_distance"] = str(distance)
				usr["poston"] = post
			if s_fbid == usr_fid:
				k = 0
			else:
				people.append(usr)
	print people
	status = 1
	res = people
	return status, res

def vote_up(jpayload):
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = data["email"]
	place_id = data["place_id"]
	event_id = data["event_id"]
	print data
	is_event_ok = db.events.find({"mid":event_id}).count()
        if is_event_ok:
            #update this place to firebase
                fb_base_url = FIREBASE_URL+"/"+event_id
                fb_user_url = fb_base_url +"/places/"+place_id+"/votes"
                fb = Firebase(fb_user_url)
                dat = str(fb.get())
		dat = dat +str(email)+ "-"
		fb.put(dat)
                status = 1
                res = "voted"
        else:
                status = 888888
                res = "invalid input"
	status = 1
	res = "ok"
        return status, res


def group_chat(jpayload):
	print ("group chat")
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	event_id = str(data["chat_id"])
	operate = str(data["operate"])
	if (db.events.find({"mid": event_id}).count >= 1):
		event_members = list()
		cur = db.events.find({"mid": event_id})
		for doc in cur:
			event_members = list(doc["event_members"])
		if operate == "add":
			print("add")
			for member in event_members:
				print member
				fid = None
				tcur = db.accounts.find({"mid": member})
				for doc in tcur:
					fid = doc["fb_id"]
				cur = db.chatlist.find({"fbid": fid})
                                chatids = list()
                                for doc in cur:
                                        chatids = list(doc["clist"])
                                chatids.append(event_id)
                                chatids = list(set(chatids))
                                db.chatlist.update_one({"fbid": fid},{"$set": {"clist":chatids}})
				status = 1
				res = "updated"
		if operate == "delete":
			print("delete")
	return status, res	


def insert_chat_id(jpayload):
	print "insert chat id"
	dat = dict()
	res = None
	update_from = 0
	update_to = 0
	status = 999999
	data = json.loads(jpayload)
	fromid = str(data["from_id"])
	toid = str(data["to_id"])
	chat_id = str(data["chat_id"])
	operate = str(data["operate"])
	chat_type = str(data["chat_type"])
	if chat_type == "group":
		return group_chat(jpayload)
	#-------> to id
	if (db.chatlist.find({"fbid": toid}).count() >= 1):
		if operate == "add":
			if chat_type == "private":
				#update for to
				cur = db.chatlist.find({"fbid": toid})
				chatids = list()
				for doc in cur:
					chatids = list(doc["clist"])
				chatids.append(chat_id)
				chatids = list(set(chatids))
				db.chatlist.update_one({"fbid": toid},{"$set": {"clist":chatids}})
				#update for from
				cur = db.chatlist.find({"fbid": fromid})
				chatids = list()
				for doc in cur:
					chatids = list(doc["clist"])
				chatids.append(chat_id)
				chatids = list(set(chatids))
				db.chatlist.update_one({"fbid": fromid},{"$set": {"clist":chatids}})
			status = 1
			res = "updated"
		else:
			print ("del chat")
			cur = db.chatlist.find({"fbid": toid})
			chatids = list()
			for doc in cur:
				chatids = list(doc["clist"])
			chatids.remove(chat_id)
			chatids = list(set(chatids))
			db.chatlist.update_one({"fbid": toid},{"$set": {"clist":chatids}})
			status = 1
			res = "removed"
	else:
		print ("possible eventid")
		dat = dict()
		dat["fbid"] = toid
		cids = list()
		cids.append(chat_id)
		dat["clist"] = cids
		db.chatlist.insert_one(dat)
		res = "inserted"
		status = 999999
	

	#-------> from id
	if (db.chatlist.find({"fbid": fromid}).count() >= 1):
		if operate == "add":
			if chat_type == "private":
				#update for to
				cur = db.chatlist.find({"fbid": fromid})
				chatids = list()
				for doc in cur:
					chatids = list(doc["clist"])
				chatids.append(chat_id)
				chatids = list(set(chatids))
				db.chatlist.update_one({"fbid": fromid},{"$set": {"clist":chatids}})
				#update for from
				cur = db.chatlist.find({"fbid": toid})
				chatids = list()
				for doc in cur:
					chatids = list(doc["clist"])
				chatids.append(chat_id)
				chatids = list(set(chatids))
				db.chatlist.update_one({"fbid": toid},{"$set": {"clist":chatids}})
			status = 1
			res = "updated"
		else:
			cur = db.chatlist.find({"fbid": fromid})
			chatids = list()
			for doc in cur:
				chatids = list(doc["clist"])
			chatids.remove(chat_id)
			chatids = list(set(chatids))
			db.chatlist.update_one({"fbid": fromid},{"$set": {"clist":chatids}})
			status = 1
			res = "removed"
	else:
		dat = dict()
		dat["fbid"] = fromid
		cids = list()
		cids.append(chat_id)
		dat["clist"] = cids
		db.chatlist.insert_one(dat)
		res = "inserted"
		status = 1

	print data
	return status, res

def get_chat_history(jpayload):
	print "get chat history"
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	chatids = list()
	if (db.accounts.find({"email": email}).count() >= 1):
		cur = db.accounts.find({"email": email})
		fid = None
		for doc in cur:
			fid = doc["fb_id"]
		cur = db.chatlist.find({"fbid": fid})
		for doc in cur:
			chatids = list(doc["clist"])
		res = chatids
		status = 1
	else:
		status = 1
		res = chatids
	print chatids
	return status, res

def get_next_event(jpayload):
	dat = dict()
	print "get next event"
	print jpayload
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	cur  = db.accounts.find({"email": email})
	joined = list()
	hosted = list()
	invites = list()
	for doc in cur:
		joined = list(doc["joined"])
		hosted = list(doc["hosted"])
		invites = list(doc["invites"])
	# get next joined event
	if len(joined) > 0 :
		for event in joined:
			evnt = db.events.find({"mid": event})
			for doc in evnt:
				print doc["event_date"]
		for event in hosted:
			evnt = db.events.find({"mid": event})
			for doc in evnt:
				print doc["event_date"]
		for event in invites:
			evnt = db.events.find({"mid": event})
			for doc in evnt:
				print doc["event_date"]
	res = "ok"
	status = 1
	return status, res


def upload_image(jpayload):	
	print "upload image"
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	url = str(data["url"])
	pos = int(str(data["position"]))
	print pos
	cur = db.accounts.find({"email":email})
	images = list()
	for doc in cur:
		images = list(doc["images"])
	if len(images) > 5:
		#put image to last location
		images[5] = url
	else:
		images[pos] = url
	print images
	db.accounts.update_one({"email": email},{"$set": {"images":images}})
	res = "ok"
	status = 1
	return status, res

def get_people_for_event(jpayload):
	print "get people for event"
	dat = dict()
	res = None
	status = 999999
	data = json.loads(jpayload)
	email = str(data["email"])
	event_id = str(data["event_id"])
	cur = db.events.find({"mid":event_id})
	members = list()
	people = list()
	for doc in cur:
		members = doc["event_members"]
	for person in members:
		print person
		cur = db.accounts.find({"mid": person})
		usr = dict()
		for doc in cur:
			usr["name"] = doc["name"]
			usr["email"] = doc["email"]
			usr["gid"] = doc["gid"]
			usr["ame"] = doc["work"]
			usr["fb_id"] = doc["fb_id"]
		people.append(usr)
	print people
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
	if int(operid) == 7668:#post public
		status, res = post_seach_public(payload)
	if int(operid) == 7670:#vote up
		status, res = vote_up(payload)
	if int(operid) == 8100:#public search
		status, res = get_people_public_search(payload)
	if int(operid) == 8101:#find people for event
		status, res = get_people_for_event(payload)
	if int(operid) == 6000:# add chat id
		status, res = insert_chat_id(payload)
	if int(operid) == 6001:# get chat history
		status, res = get_chat_history(payload)
	if int(operid) == 7006:# get next event
		status, res = get_next_event(payload)
	if int(operid) == 7008:# upload image
		status, res = upload_image(payload)
	return status, res

if __name__ == "__main__":
	main()
