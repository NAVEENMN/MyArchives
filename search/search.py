def linear_search(data, key):
	for x in range(0, len(data)):
		if data[x] is key:
			return x
	return -1

def binary_search(data, key):
	data.sort()
	print data
	left_pos = 0
	right_pos = len(data) - 1
	middle = len(data) / 2
	while  right_pos - left_pos >= 1:
		if data[middle] == key:
			return middle
		else:
			middle = ( left_pos + right_pos ) / 2
			if key > data[middle]:
				left_pos = middle + 1
			else:
				right_pos = middle - 1
				print middle
	return -1
	

def main():
	data = ['7', '4', '3', '1', '9', '5', '2', '10']
	key = raw_input("Enter the key you wanna search: ")
	pos = linear_search(data, key)
	if pos is not -1:
		print "key " + str(key) + "is at position " + str(pos)
	else:
		print "key not found"
	pos = binary_search(list(data), key)
	if pos is not -1:
		print "key " + str(key) + "is at position " + str(pos)
	else:
		print "key not found"  

if __name__=="__main__":
	main()
