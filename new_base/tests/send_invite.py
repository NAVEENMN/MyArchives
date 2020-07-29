import urllib, urllib2
import json
import random

url = 'http://52.8.173.36/metster/send_gcm_message.php'
names = ["allie","david","jinfeng","mike","chris","kamayaki"]
event_name = ["dinner", "birthday", "party tonight"]
param1 = "812345607380812"
param2 = "859842507380812"
event =  "812345607380812-->event-->0"

elon = "865432107380812"
alan = "119979305002439"
event = alan+"-->event-->0" #alan_fb-->event

payload = dict()
payload['host'] = alan # elon invite
payload['to_id'] = "859842507380812" # send to naveen
payload['payload_type'] = "invite_check"
payload['payload_message'] = "dinner?"
payload['sender_name'] = "alan turing"
payload['event_reference'] =  alan + "-->event-->0"
payload['event_name'] = "meeting"

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
