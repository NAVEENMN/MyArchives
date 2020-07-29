class sorting:

	def bubble_sort(self, data):
		boundry = 1
		for y in range(0, len(data)-boundry):
			for x in range(0, len(data)-1):
				if data[x] > data[x+1]:
					temp = data[x+1]
					data[x+1] = data[x]
					data[x] = temp
		boundry = boundry + 1
		print data

	def selection_sort(self, data):
		bigger = None
		pos = 0
		end = len(data) - 1
		boundry = 0
		for y in range(0, len(data)):
			#By the end of this loop we will have big number and its
			#position
			for x in range(0, len(data)-boundry):
				if data[x] > bigger:
					bigger = data[x]
					pos = x
			#swaping
			temp = data[end]
			data[end] = data[pos]
			data[pos] = temp
			end = end - 1
			boundry = boundry + 1 
			bigger = None
		print data
	
	def insertion_sort(self, data):
		for x in range(1,len(data)-1):
			key = data.pop(x) #this is the key look for a position to insert
			pos = x - 1 
			while pos > 0 and  data[pos] > key:
				pos = pos -1
				data.insert(pos, key)
			data.insert(pos, key)
			print data  	

	def merge_step(self, lista, listb):
		A = lista
		B = listb
		length = max(len(A), len(B)) - 1
		posa = 0
		posb = 0
		C = list()
		while posa < len(A) and posb < len(B):
			if A[posa] < B[posb]:
				C.append(A[posa])
				posa = posa + 1
			else:
				C.append(B[posb])
				posb = posb + 1
		if length - posa > 0:
			C.extend(A[posa:])
		if length - posb > 0:
			C.extend(B[posb:])
		return C
	def merge_sort(self, data):
		if len(data) <= 1:
			return data
		middle = len(data)/2
		left = data[0:middle]
		right = data[middle+1:]
		left = self.merge_sort(left)
		right = self.merge_sort(right)
		return list(self.merge_step(left, right))
			
def main():
	data = ['2','4','0','5','9','6','8','7','3','1']
	sort = sorting()
	print data
	#sort.bubble_sort(list(data))#send copy of data not the refrence
	#sort.selection_sort(list(data))
	#sort.insertion_sort(list(data))
	res = sort.merge_sort(data)
	print res

if __name__ == "__main__":
	main()
