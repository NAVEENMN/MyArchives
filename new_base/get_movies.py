import numpy as np
from pymongo import MongoClient
import string
from scipy.spatial import *
import operator

client = MongoClient('localhost', 27017)
db = client.Chishiki

mf = dict()
mf["action"] = 0
mf["animation"] = 1
mf["comedy"] = 2
mf["family"] = 3
mf["horror"] = 4
mf["romance"] = 5
mf["war"] = 6
mf["adventure"] = 7
mf["crime"] = 8
mf["mystery"] = 9
mf["sci-fi"] = 10
mf["drama"] = 11

pve = [0.98, 0.80, 0.6, 0.3, 0.78, 0.20, 0.86, 0.7, 0.5, 0.99, 0.99, 0.2]

def get_pref_vec(pref):
    vec = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l']
    val = [0.880797077978, 0.869891525637, 0.8581489351, 0.845534734916, 0.832018385134, 0.817574476194, 0.802183888559, 0.785834983043, 0.768524783499, 0.750260105595, 0.73105857863, 0.72 ]
    pvec = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    for x in range (0, len(pref)):
        h = pref[x]
        ind = vec.index(h)
        pvec[ind] = val[x]
    return np.array(pvec)

res = dict()

def main():
	prefs = ['a', 'b', 'h', 'c', 'd', 'e', 'f', 'g', 'i', 'j', 'k', 'l']
	pv = get_pref_vec(prefs)
	pv = np.array(pve)
	print pv
	cursor = db.movies.find()
	for doc in cursor:
		gen = doc
		ge = gen["Genre"].split(", ")
		if ge != "n/a":
			title = gen["Title"]
			ge = [element.lower() for element in ge]
			fv = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
			for key in ge:
				if key in mf:
					fv[mf[key]] = 1
			fv = np.array(fv)
			dis = distance.cosine(pv, fv)
			print ge
			print fv, dis
			res[title] = dis
	sorted_x = sorted(res.items(), key=operator.itemgetter(1))
	print sorted_x
if __name__ == "__main__":
	main()
