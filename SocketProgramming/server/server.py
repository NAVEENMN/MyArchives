#!/usr/bin/python

"""server.py - A Python code for simulating a server which can handle GET and PUT requests."""
'''
__name__ = "Server"
__author__ = "Naveen Mysore <nmysore@uncc.edu>"
'''
import socket
import sys

par = str(sys.argv)
#-------------------------------- Fetching the host name/address
arguments = par.split()
#------------------------ Server
end = len(arguments[1])
start = 1
end =  end -2
port = int(arguments[1][start:end])
#------------------------------- Connection establishment
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('', port))
while 1: #---------------------- The server keeps listening to incoming connections
	s.listen(1)
	print "Server is listening to incoming connection requests..."
	(client, (ip, port))= s.accept() # client is the connection socket
	print 'Server is now connected to: ', ip, 'at port:', port
	#--------------------------------
	data = client.recv(1024)
	info = data.split(" ")
	com = info[0]
	filead = info[1]
	print filead
	#-------------------------------- If GET is requested the method is implemented
	if(com == "GET"):
		try:
	    		f = open(filead, "rb")
	    		permission = 1 # if file is opened the permission is set
		except IOError:
	    		print '404 page not found'
	    		status = "404 page not found"
	    		client.sendall(status)
	    		permission = 0
	    	if(permission):
			print "200 OK " + filead
			status = "200 OK " + filead
			client.sendall(status)
			data = f.read()
			size = str(len(data))
			f.close()
			client.sendall(data)
	#--------------------------------- If PUT method is requested the method is implemented		
	if com == "PUT":
		try:
	    		f = file("local/newfile.html", "w")
	    		permission = 1
		except IOError:
	    		print '404 page not created'
	    		status = "404 page not created"
	    		client.sendall(status)
	    		permission = 0
		if(permission):
			if (filead == "400"):
				status = "400 Error " + "local/newfile.html"
				client.sendall(status)
				print "Bad Request"
			else:
				print "200 OK " + "local/newfile.html"
				status = "200 OK " + "local/newfile.html"
				client.sendall(status)
				data = client.recv(9024)
				f.write(data)
				print "File created!!"
				f.close
	if com != "PUT" and com != "GET":
		print "Bad Request"
s.close()
