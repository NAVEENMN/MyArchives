from firebase import Firebase
import sys 
import random
'''
name : add_a_user_to_fb
@desp : this script adds a user to firebase

'''


def main():
	raw_event = sys.argv[1]
	eventid = raw_event.replace("--","-->")
	ids = ["85984457680812","859842506788012","859845708380812","859844567780812","859844578380812","85985607380812","859842545680812","859842503450812","859834507580812"]
	names = ["Sam p","Mike m","Kevin b","Sid t","allie l","john m"," peter g"]
	food = ["asian", "coffee", "american", "indian", "italian", "latin"]
	print(eventid)
	data = eventid.replace("-->", " ").split()
	root = data[0]
	url = "https://met-ster-event.firebaseio.com/"
	url = url + root +"/"+ eventid # path
	payload = dict()
	lat = 37.4421975
	lon = -122.1616956
	lat = lat + random.uniform(0.01, 0.09)
	lon = lon + random.uniform(0.01, 0.09)
	payload['Latitude']= str(lat)
	payload['Longitude']= str(lon)
	payload['eventname']= "dinner"
	payload['food']= random.choice(food)
	chosen_id = random.choice(ids)
	payload['nodename'] = random.choice(names)
	payload['nodetype'] = "member"
	payload['price'] = "3.6"
	payload['travel'] = "5.0"
	url = url + "/" + chosen_id
	fb = Firebase(url)
	fb.put( payload)

if __name__ == "__main__":
	main()
