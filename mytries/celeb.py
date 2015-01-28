#This problem is to find a celebrity from the list
#A celebrity is a person who doesnt know everyone but everyone knows him
#knows(x,y) will return true if x knows y and false if does`nt
#find a celebrity in o(n)
person = dict()
person[0] = [0, 1, 3]
person[1] = [0, 1, 2]
person[2] = [0, 1, 2, 3, 4]
person[3] = [3]
person[4] = [1, 3]

def knows(x, y):
	links = person[x]
	if y in links:
		return True
	else:
		return False

def find_celeb(number_of_people):
	np = number_of_people
	possible = list()
	ptr1 = 0
	ptr2 = 1
	while (ptr1 != np):
		if knows(ptr1, ptr2) and knows(ptr2, ptr1):#they both know each other they cant be celebrity
			ptr1 = ptr1 + 2
			ptr2 = ptr2 + 3
		elif knows(ptr1, ptr2):
			ptr1 = ptr2
			ptr2 = ptr2 + 1
		elif knows(ptr2, ptr1):
			ptr2 = ptr2 + 1
		else: #both dont know each other
			possible.append(ptr2)
			ptr2 = ptr2 + 1

def main():
	find_celeb(4)

if __name__ == "__main__":
	main()
