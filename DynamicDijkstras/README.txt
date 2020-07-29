/*
 * Author : Naveen Mysore (800812071)
 * University of North Carolina at Charlotte
 * ITCS 6114 Project 2
 * Description : This is the implementation of Dijkstra`s Algorithm 
 * 		 for dynamic network.
 * 		 Status
 * 	         Dijstra`s algorithm : Implemented
 * 		 Graph based network changes:
 *		 aadedge and deleteedge: Implemented 							 edgedown and edgeup : Implemented
 * 		 print : Prints all vertcies and edges that are up not marked by down
 * 		 queue using heap : Implemented using binary heap
 * 		 reachable : Implemented using breadth first search
 */

Usage :

In exclipse : Go to Run options and under that go to run configuration and go to arguments and set argument parameter as network.txt apply and run

In command line: go the file path execute javac driver.class heap.class then execute java driver network.txt

commands: 
print 
addedge tailvertex headvertex transmit time
deleteedge tailvertex headvertex
edgedown tailvertex headvertex
edgeup tailvertex headvertex
vertexdown vertex 
vertexup vertex
reachable
