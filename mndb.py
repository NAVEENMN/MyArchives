# mndb.py 
# Author : Naveen Mysore
# Date : may 29th 2015
# @desp : handle mongodb connection

from pymongo import MongoClient
import json
import sys
import pprint
from bson.json_util import dumps
'''
	name : insert
	@params : database_ref, id, payload
	@return : status
	@desp : This function checks if data exists
		if id doesn`t exists then it will insert
'''
def insert(db, id, payload):
	data = json.loads(payload)
	return true

'''
	name : print_all_from_db
	@param : database_ref
	@return : void
	@desp : This funtction prints all the data stored in the database 	

'''
def print_all(db):
	records = db.find({});
	for record in records:
		print record

'''
	name : delete_all_from_db
	@param : database_ref
	@return : void
	@desp : This funtion deletes all value in database
'''
def delete_all(db):
	db.remove({})

'''
	name : main
	@params :
	@return :
	@desp : This is the main function
'''
def main():
	conn = MongoClient()
	user_record = json.loads(sys.argv[1] )# this data comes from php
	print sys.argv[1]
	db = conn.metster.accounts
	#db.insert(user_record)
	print_all(db)
if __name__ == "__main__":
	main()
