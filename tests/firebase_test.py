from firebase import Firebase
from collections import OrderedDict


fb = Firebase("https://met-ster-event.firebaseio.com/859842507380812/859842507380812-->event-->0")
data = fb.get()
food = dict()
# test for latitude based
people = list()
for key in data.keys(): # key refers to id
	if(data[key]['nodetype'] == "member" or data[key]['nodetype'] == "host"): # this is person
		location = list()
		location.append(data[key]['Latitude'])
		location.append(data[key]['Longitude'])
		people.append(location)
		pref = data[key]['food']
		if(food.has_key(pref)):
			count = food[pref]
			food[pref] = count + 1
		else:
			food[pref] = 1

for person in people:
	print person

food_sorted = OrderedDict(sorted(food.items(), key=lambda kv: kv[1], reverse=True))

for item in food_sorted:
	print item
