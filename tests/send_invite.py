import urllib, urllib2
import json
import random

url = 'http://52.8.173.36/metster/send_gcm_message.php'
names = ["allie","david","jinfeng","mike","chris","kamayaki"]
event_name = ["dinner", "birthday", "party tonight"]
param1 = "859842507380812"
param2 = "859842507380812"
event = "859842507380812-->event-->0"

payload = dict()
payload['host'] = param1
payload['to_id'] = param2
payload['payload_type'] = "invite_check"
payload['payload_message'] = "dinner?"
payload['sender_name'] = random.choice(names)
payload['event_reference'] =  event
payload['event_name'] = random.choice(event_name)

jdata = json.dumps(payload)

values = {'param1' : param1,
          'param2' : param2,
          'param3' : jdata }


data = urllib.urlencode(values)
req = urllib2.Request(url, data)
response = urllib2.urlopen(req)
print response.geturl()
print response.info()
the_page = response.read()
print the_page
