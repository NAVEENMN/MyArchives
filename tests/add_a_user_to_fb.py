from firebase import Firebase
import sys 
import random
'''
name : add_a_user_to_fb
@desp : this script adds a user to firebase

'''
FIREBASE_URL = "https://met-ster-event.firebaseio.com/"
'''
name : check_avail
desp : this function checks available users to add members
'''
def check_avail():
	fb = Firebase(FIREBASE_URL+"/859842507380812")
	data = fb.get()
	print data

def main():
        host = raw_input("enter user id")
        event_id = raw_input("enter event id")
	name = ["natalie", "mark", "fry", "peter", "donald"]
	food = ["asian", "coffee", "american", "indian", "italian", "latin"]
        new_user_id = ["12345","67890","1112131415","16171819","19203212","3141313"]
	url = "https://met-ster-event.firebaseio.com/"
	#url = url + root +"/"+ eventid # path
	payload = dict()
	lat = 37.4421975
	lon = -122.1616956
	lat = lat + random.uniform(0.01, 0.09)
	lon = lon + random.uniform(0.01, 0.09)
	payload['Latitude']= str(lat)
	payload['Longitude']= str(lon)
	payload['eventname']= "meeting"
	payload['food']= random.choice(food)
	payload['nodename'] = random.choice(name)
	payload['nodetype'] = "member"
	payload['price'] = "3.6"
	payload['travel'] = "5.0"
 	new_usr_id = random.choice(new_user_id)
	url = url + "/" + host +"/"+event_id
        url = url + "/" + new_usr_id
	fb = Firebase(url)
	fb.put( payload)

if __name__ == "__main__":
	main()
