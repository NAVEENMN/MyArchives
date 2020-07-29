#if string has all unique character

def is_unique(str):
	dictlookup = {}
	for w in str:
		key = w
		if w in dictlookup:
			print "duplicate exists"
			break
			#dictlookup[key] = dictlookup[key] + 1
		else:
			dictlookup[key] = 1
	print dictlookup

def main():
	data = str(raw_input("enter your string: "))
	is_unique(data)

if __name__ == "__main__":
	main()
