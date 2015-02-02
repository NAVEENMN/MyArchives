def permutation(data, i, n):
	if i == n:
		print data
	else:
		for x in range(i, n):
			temp = data[i]
			data[i] = data[x]
			data[x] = temp
			permutation(data, i+1, n)
			temp = data[i]
			data[i] = data[x]
			data[x] = temp

def main():
	data = "ABCD"
	data = list(data)
	permutation(data, 0,len(data))

if __name__ == "__main__":
	main()
