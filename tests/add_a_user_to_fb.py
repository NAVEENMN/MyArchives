from firebase import Firebase
import sys 
import random
'''
name : add_a_user_to_fb
@desp : this script adds a user to firebase

'''


def main():
	id = "119979305002439"
	name = "alan turing"
	food = ["asian", "coffee", "american", "indian", "italian", "latin"]
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
	payload['nodename'] = name
	payload['nodetype'] = "host"
	payload['price'] = "3.6"
	payload['travel'] = "5.0"
	url = url + "/" + id +"/"+id + "-->event-->0"
	url = url + "/" + id
	fb = Firebase(url)
	fb.put( payload)

if __name__ == "__main__":
	main()
