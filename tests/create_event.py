'''
	name : create_event
	desp : This script creates 3 events of chosen host and random preferences
'''

from firebase import Firebase
import sys
import random
import os

URL = "https://met-ster-event.firebaseio.com/"
# hypothetical members
members = ["123456","78910111213","141516171819","987654321","859844578380812","85985607380812","859842545680812","859842503450812","859834507580812"]
#hypothetical names
nodename = ["naveen mysore","patrik harris","sam plank","alan turing","ada lovelace","elon musk","bill gates","steve woz","eric brown"]
#choices
food = ["asian", "coffee", "american", "indian", "italian", "sushi"]
price = ["1","2","3","4","5"]
travel = ["1","2","3","4","5"]
eventname = ["dinner","lunch","birthday"]

def build_payload(host):
	payload = dict()
	lat = 37.4421975
	lon = -122.1616956
	lat = lat + random.uniform(-0.05, 0.05)
	lon = lon + random.uniform(-0.05, 0.05)
	payload['Latitude']= str(lat)
        payload['Longitude']= str(lon)
        payload['eventname']= random.choice(eventname)
        payload['food']= random.choice(food)
        payload['nodename'] = random.choice(nodename)
        payload['nodetype'] = "host"
        payload['price'] = random.choice(price)
        payload['travel'] = random.choice(travel)
	return payload

def new_event(host):
	eventid = host + "-->event-->0" 
	path = URL + host + "/" + eventid
	payload = build_payload(host)
	fb = Firebase(path)
	fb.put(payload)
	print "event ", eventid, "created"	
	return eventid


def add_event():
	# we will create three events and host will be first three
	event_ids = list()
	for x in range(0, 3):
		event_ids.append(new_event(members[x]))
	# we have created three events
	for event in event_ids:
		print "adding a member to ", event
		for x in range(0, 2):
			cmd = "python add_a_user_to_fb.py "+ event.replace("-->","--")
			os.system(cmd)

def main():
	add_event()

if __name__ == '__main__':
	main()
