import nltk 
from nltk import word_tokenize
from nltk.util import ngrams

#This function genarates unigrams
def gen_unigrams(review):
	token = nltk.word_tokenize(review)
	unigrams = ngrams(token, 1)
	unigram_list = ""
	for x in range(0, len(unigrams)):
		words = unigrams[x]
		unigram_list = unigram_list + words[0] + "-->"
	return unigram_list
#This function generated bigrams
def gen_bigrams(review):
	token = nltk.word_tokenize(review)
	bigrams = ngrams(token, 2)
	bigram_list = ""
	for x in range(0, len(bigrams)):
		words = bigrams[x]
		bigram_list = bigram_list + words[0]+ " " + words[1]+"-->"
	return bigram_list
#This is the main finction
def main():
	f = open('review.txt', 'r+')
	line =  f.read()
	review = line
	bigram_list = gen_bigrams(review)
	unigram_list = gen_unigrams(review)
	processed_review = bigram_list + unigram_list
	f.close()
	f = open('review.txt','w+')
	f.write(processed_review)
if __name__ == "__main__" :
	main()
