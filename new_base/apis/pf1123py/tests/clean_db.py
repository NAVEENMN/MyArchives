from pymongo import MongoClient

client = MongoClient('localhost', 27017)
db = client.Chishiki


def clean_db():
	dat = db.theater.find()
	for rec in dat:
		mid = rec["mid"]
		c  = db.theater.find({"mid":mid}).count()
		if c > 1:
			print mid, c
			db.theater.delete_many({"mid":mid})
			db.theater.insert_one(rec)
def main():
	clean_db()

if __name__ == "__main__":
	main()
