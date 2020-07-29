def remove_the_vowels(data):
	look_up = {'a','e','i','o','u'}
	for x in range(0, len(data)):
		if data[x] in look_up:
			data[x] = ''
def  main():
	data = list(raw_input("Enter the sentence "))
	remove_the_vowels(data)
	print ''.join(data)

if __name__ == "__main__":
	main()
