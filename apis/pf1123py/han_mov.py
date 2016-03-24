import json
import hashlib
from pymongo import MongoClient
import urllib2
from lookups import *


#------------------------  MOVIES OPEARTION
def insert_movie(payload):
	return "OK"
def delete_movie(payload):
	return "OK"
def find_movie(payload):
	return "OK"
#------------------------------------------


#------------------------  THEATER OPEARTION
def insert_theater(payload):
        return "OK"
def delete_theater(payload):
        return "OK"
def find_thater(payload):
        return "OK"
#------------------------------------------


#-------------------------  FRAME OUTPUT
def frame_output(rid, status, reqdes, msg, error):
        out = dict()
        out["request_id"] = rid
        out["request_des"] = reqdes #later
        out["error_des"] = error
        out["status"] = status
        out["response"] = msg
        return json.dumps(out)


#----------------------- MAIN
# 11 insert movie
# 12 delete movie
# 13 find movie
# 21 insert theater
# 22 delete theater
# 23 find theater
def main(oper, payload):
 	token = str(oper)
	status = 999999
	res = None
	print oper
	if token[0] == 1: #movies
		if token[1] == 1: # insert
			status, res = insert_movie(payload)
		if token[1] == 2: # delete
			status, res = delete_movie(payload)
		if token[1] == 3: # find
			status, res = find_movie(payload)
	if token[0] == 2: #theater
		if token[1] == 1: # insert
			status, res = insert_theater(payload)
                if token[1] == 2: # delete
			status, res = delete_theater(payload)
                if token[1] == 3: # find
			status, res = find_theater(payload)
	output = frame_output(oper, status, token[1], res, ERRORS[status])
	print output
#---------------------------
if __name__ == "__main__":
	main()
