import urllib2
url =  raw_input("enter url: ")
data = urllib2.urlopen(url).read()
print data
