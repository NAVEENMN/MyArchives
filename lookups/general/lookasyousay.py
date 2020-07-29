def look_as_you_say(data):
	count = 1
	ls = list()
	print data
	key = data[0]
	for x in range(1, len(data)):
		if data[x] is key:
			count = count + 1
		else:
			ls.append(count)
			ls.append(key)
			count = 1
			key = data[x]  
	return list(ls)		
def lays(limit):
	response = str(1)
	for x in range(0, limit):
		 response = look_as_you_say(response)
		 print response
def main():
	print look_as_you_say(str(11112233444) + "a")
if __name__ == "__main__":
	main()
