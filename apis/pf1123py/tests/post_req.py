import urllib, urllib2, cookielib
import json

cookie_jar = cookielib.CookieJar()
opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cookie_jar))
urllib2.install_opener(opener)

# acquire cookie
url_1 = 'http://52.8.173.36/metster/apis/pf1123py'
req = urllib2.Request(url_1)
rsp = urllib2.urlopen(req)

# do POST
url_2 = 'http://52.8.173.36/metster/apis/handle_user.php'
data = dict()
data["dev_id"] = "1234"
data["name"] = "naveen"
data["email"] = "navimn1991@gmail.com"
data["fb_id"] = "3445"
jpayload = json.dumps(data)
values = dict(operation='add', key='15828', payload=jpayload)
data = urllib.urlencode(values)
req = urllib2.Request(url_2, data)
rsp = urllib2.urlopen(req)
content = rsp.read()

# print result
import re
pat = re.compile('Title:.*')
print content
