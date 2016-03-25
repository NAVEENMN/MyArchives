import get_yelp_ranking_copy as yr
from pymongo import MongoClient
import json

client = MongoClient('localhost', 27017)
db = client.Chishiki


def find_food(jpayload):
	data = json.loads(jpayload) #unpack
	query = data["query"]
	event_id = data["event_id"]
	response = yr.main(db, query, event_id)
	return response

def main(operid, payload):
	status = 999999
	res = None
	if int(operid) == 9000:#find_food
		res = find_food(payload)
		status = 1
	return status, res

if __name__ == "__main__":
	main()
