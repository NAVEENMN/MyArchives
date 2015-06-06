import os
import get_location
import sys
import multiprocessing
from firebase import Firebase
from collections import OrderedDict
from time import sleep

def worker(foodtype, event):
	response = get_location.main(event, foodtype)#os.system("python get_location.py " + event +" "+str(foodtype))
	return response	

def main():
	fb_base = "https://met-ster-event.firebaseio.com/"
	event_id = sys.argv[1]
	user = event_id.rpartition('--')[0]
	user = user.rpartition('--')[0]
	nevent_id = event_id.replace("--","-->")
	fb_ref = fb_base + user + "/" + nevent_id
	fb  = Firebase(fb_ref)
	data = fb.get()
	food = dict()
	for key in data.keys():
		if(data[key]['nodetype']=="member" or  data[key]['nodetype'] == "host"):
			pref = data[key]['food']
			if(food.has_key(pref)):
				count = food[pref]
				food[pref] = count + 1
			else:
				food[pref] = 1

	food_ordered = OrderedDict(sorted(food.items(), 
                                  key=lambda kv: kv[1], reverse=True))	
	jobs = []
    	for foodchosen in food_ordered:
		#print foodchosen + "<-->"
		res  = worker(foodchosen, event_id)
		sleep(0.05)
		print res
        	#p = multiprocessing.Process(target=worker, args=(foodchosen,event_id))
        	#jobs.append(p)
        	#p.start()
		#p.join()

if __name__ == "__main__":
	main()

