import urllib, urllib2, cookielib
import json

def req(rid, payload):
	cookie_jar = cookielib.CookieJar()
	opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cookie_jar))
	urllib2.install_opener(opener)

	# acquire cookie
	url_1 = 'http://104.236.177.93:8080/'
	req = urllib2.Request(url_1)
	rsp = urllib2.urlopen(req)

	# do POST
	url_2 = 'http://104.236.177.93:8080/'
	jpayload = payload
	values = dict(operation=rid, key='15828', payload=jpayload)
	data = urllib.urlencode(values)
	req = urllib2.Request(url_2, data)
	rsp = urllib2.urlopen(req)
	content = rsp.read()
	# print result
	return content
def main():
	print req("test", "test")
if __name__ == "__main__":
	main()
