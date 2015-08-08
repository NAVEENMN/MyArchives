import json
import os
import sys

def build_payload():
	data = {}
	data['host'] =  sys.argv[1]
	data['to_id'] = sys.argv[2]
	data['payload_type'] = sys.argv[3]
	data['payload_message'] = sys.argv[4]
	json_data = json.dumps(data)
	os.system("php send_gcm_message.php " + sys.argv[1] +" " + sys.argv[2] + " "+ json_data)

def main():
	build_payload()

if __name__=="__main__":
	main()
