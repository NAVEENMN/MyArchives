
class is_all_unique_char:
	def __init__(self, vala):
		self.data = vala
		self.look_up = []
	def check_is_unique(self, Test):
		value = Test
		for x in range(0, 26):
			self.look_up.insert(x, 0)
		for x in range(0, len(Test)):
			ref = ord(value[x])- ord('a')
			val = self.look_up[ref]
			if val+1 >= 2:
				print "Has duplicates!!"
				exit()
			self.look_up.insert(ref, val+1)
class compress_the_string:
	def __init__(self, value):
		self.data = value
		self.new = []
	def string_compression(self):
		string_data = self.data
		count = 0
		i = 0
		for x in range(0, len(string_data)):
			temp = string_data[x]
			i = x
			while temp == string_data[i]:
				count = count + 1
				i = i+1
		self.new.append(val)
		self.new.append(count)
				 
def check_char_unique():
	user_test = raw_input("Enter the test string:").lower()
	if len( user_test ) > 26  :
		print "This string is has repeated charcaters"
	else:
		test_string = is_all_unique_char(user_test)
		test_string.check_is_unique(user_test)
		print test_string.look_up

def string_compress():
	user_test = raw_input("Enter the test string").lower()
	test_string = compress_the_string(user_test)
	test_string.string_compression()	

	
def main():
	print "enter 1 for - Check if all characters are unique:"
	print "	     2 for - string compression"
	choice = int(raw_input("enter the choice"))
	if choice == 1 :
		check_char_unique()
	if choice == 2:
		string_compress()

if __name__ == '__main__':
	main()
