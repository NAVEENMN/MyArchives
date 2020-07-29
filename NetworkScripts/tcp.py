import socket

target_host = "0.0.0.0"
target_port = 9999

def connect_tcp():
        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.connect((target_host, target_port))
        return client
def connect_udp():
        client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return client

def main():
        con = connect_tcp()
        payload = "GET / HTTP/1.1\r\nHost:google.com\r\n\r\n"
        #print payload
        con.send(payload)
        response = con.recv(4096)
        print response
        #con1.sendto("AASDS", (target_host, target_port))
        #data, addr = con1.recvfrom(4096)
        #print data, addr

if __name__ == "__main__":
        main()
