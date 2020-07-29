
sequence = list()
sequence_tail = list()

def tail_fib(n, a, b):
	if n == 0:
		return a
	elif n == 1:
		return b
	else:
		temp = tail_fib(n-1, b, a+b)
		sequence_tail.append(a+b)
		return temp

def fib(n):
	if n == 1:
		return 1
	elif n == 0:
		return 0
	else:
		temp = fib(n-1) + fib(n-2)
		sequence.append(temp)
		return temp
def main():
	value = int(raw_input("enter a number: "))
	fib(value)
	tail_fib(value, 0, 1)
	print "fib is : "
	print  sequence
	print  sequence_tail

if __name__ == "__main__":
	main()
