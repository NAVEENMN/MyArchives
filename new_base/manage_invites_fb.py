from firebase import Firebase
import os, sys
# this is only a reference file
FIREBASE_URL = "https://met-ster.firebaseio.com/"

def add_event(to, event_id, event_name, sender_name):
	fb = Firebase(FIREBASE_URL+to+"/invited/")
	data = fb.get()
	print data
 	if data == None:
		data = list()
	to_fb = event_id + "//" + event_name + "//" + sender_name
        terminal_id = "ucchapin-->event-->0" + "//" + "ucchapin" + "//" + "ucchapin"
	terminal_id_2 = "ucchapin-->event-->1" + "//" + "ucchapin" + "//" + "ucchapin"
	try:
		data.append(to_fb)
	except:
		fb.delete() 
        data.append(to_fb)
	data.append(terminal_id) #end marker for the firebase parse
	data.append(terminal_id_2) # if the list size drops to 1 it is getting 
				   # dictonary instead of list 
	s = set()
	for x in range(0, len(data)):
		s.add(data[x])
	new_data = list(s)
	print new_data, len(new_data)
	fb.put(new_data)

def main():
	to = sys.argv[1]
	event_id = sys.argv[2].replace("--","-->")
	event_name = sys.argv[3]
	sender_name = sys.argv[4]
	add_event(to, event_id, event_name, sender_name)
	
if __name__ == "__main__":
	main()
