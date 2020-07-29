def get_longest_common_sub_sequence(data):
	key = data[0]
	ptr = 0
	longest = 0
	current_max = 0
	current_max_char = None
	for x in range(0, len(data)):
		if data[x] == key:
			current_max = current_max + 1
		else:
			if current_max > longest:
				longest = current_max
				current_max_char = key
			key = data[x]
			current_max = 1
	if current_max == len(data): # when we have only type of char
		print data[0]
	else:
		print current_max_char		

def main():
	data = "aaaaaaabbbbccdddeeeeefgggg"
	get_longest_common_sub_sequence(data)

if __name__ == "__main__":
	main()
