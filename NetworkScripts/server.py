import socket
import threading

target_host = "127.0.0.1"
target_port = 80

bind_ip = "0.0.0.0"
bind_port = 9999

def connect_tcp():
        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.connect((target_host, target_port))
        return client

def connect_udp():
        client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return client

def connect():
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind((bind_ip, bind_port))
        server.listen(5)
        print "[*] Listening on %s:%d" % (bind_ip, bind_port)
        return server
def handle_client(client_socket):
        request = client_socket.recv(1024)
        print "[*] Received: %s" % request
        client_socket.send("ACK!")
        client_socket.close()

def main():
        server = connect()
        while True:
            client,addr = server.accept()
            print "[*] Accepted connection from: %s:%d" % (addr[0], addr[1])
            client_handler = threading.Thread(target=handle_client, args=(client,))
            client_handler.start()

if __name__ == "__main__":
        main()
