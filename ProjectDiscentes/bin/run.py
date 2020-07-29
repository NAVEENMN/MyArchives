import os
os.system("rm data.txt")
os.system("rm data/finaldata.txt")
os.system("python digits_video.py >> data.txt")
os.system("python get.py >> data/finaldata.txt")
exit()
