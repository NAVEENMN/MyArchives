import datetime
import json
import hashlib
from  error_id import ERRORS
from pymongo import MongoClient

client = MongoClient('localhost', 27017)
db = client.Chishiki

add_params_list = ["name", "email", "aid", "id"]

params = dict()
params["add_params"] = add_params_list

def check_add_params(payload):
        status = 0
        add_list = params["add_params"]
        for key in payload:
                if key in add_list:
                        status = 1
                else :
                        return 100011
        return status

def get_data(payload):
        try:
                data = json.loads(payload) # decode json
                dat = dict() # for new payload
                dat["name"] = data["name"]
                dat["email"] = data["email"]
                dat["id"] = data["id"]
                data = dat
        except ValueError as e:
                print e
                data = "Error"
        return data

def insert_to_db(datatodb):
        try:
                result = db.accounts.insert_one(datatodb)
                print result
        except ValueError as e:
                print e

def show_db():
        print "in db"
        cursor = db.inventory.find()
        for document in cursor:
                print(document)
