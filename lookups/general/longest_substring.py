def is_palindrome(word):
	word = word + "\0"
	ptr1 = word[0]
	ptr2 = word[0]
	ptr3 = 0
	while word[ptr2] == "\0":
		ptr1 = ptr1 + 1
		ptr2 = ptr2 + 2
	while word[ptr1] == "\0":
		if word[ptr3] != word[ptr2]:
			return False
	return True
	
def find_longest_substring(data):
	start = 0
	end = 0
	temp = 0
	while end != len(data):
		while data[end] != " ":
			end = end + 1
		if is_palindrome(data[start:end]):
			print "yes"#heap.insert((end-start), data[start:end])
		else:
			start = end + 1
			end = start

def main():
	data = "This word mam is not as big as madam"
	find_longest_substring(data)

if __name__ == "__main__":
	main()
