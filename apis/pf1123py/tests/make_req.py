import post_req as p
import json
import sys

def create_payload(rid):
	data = dict()
	if rid == 111000: #insert to account
		data["dev_id"] = "12er34"
		data["email"] = "pramn1991@gmail.com"
		data["fb_id"] = "3445ee"
		data["name"] = "paveen"
	if rid == 121000:# insert to movies
                data["mov_id"] = "1234"
                data["mov_name"] = "Iron Man"
                data["release_date"] = "2-May"
                data["language"] = "English"
		data["genre"] = "action, adventure, sci-fi"
		data["year"] = "2008"
	if rid == 111002:
		data["email"] = "pra1991@gmail.com"
	return data

def main():
	oper = sys.argv[1]
	tid = sys.argv[2]
	if oper == "insert":
		rid = 0
		if tid == "1":
			rid = 111000
		if tid == "2":
			rid = 121000
		data = create_payload(rid)
	if oper == "find":
		rid = 111002
		data = create_payload(rid)
	#-----
	#rid = 111000 # insert mongo account 
	#rid2 = 111002 # find by id
	#rid3 = 111003 # edita
	#rid4 = 111004 #print all
	#----
	jpayload = json.dumps(data)
	response = p.req(rid, jpayload)
	print response

if __name__ == "__main__":
	main()
