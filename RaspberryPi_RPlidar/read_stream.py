import serial
file_name = "points.txt"
f = open(file_name, 'w')

def main():
	ser = serial.Serial('/dev/ttyACM0', 115200)
	counter = 0
	while counter < 10000:
		dat = ser.readline()
		f.write(dat+'\n')
		counter = counter + 1
	f.close()

if __name__ == "__main__":
	main()
