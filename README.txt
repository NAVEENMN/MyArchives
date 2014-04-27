-- This is the implementation of Distributed Bellman ford algorithm
All the basic methods like sending the packets between node and data structures to maintain
the table and methods to print the tables were already provided in the skeleton code. In this
project The Distance vector routing algorithm in implement using distributed Bellman Ford
Algorithm.
All the method is implementation described below is same across all entities.
In the constructor part we first fetch the cost that are assigned in NetworkSimulator.cost[][].
Then an array is used to hold the current minimum distance cost values. These distances are
feeded to the distance vector table which is a 4X4 matix and represents DistanceTable[to
node][via node]. Once all these are done these tables are formed into packets and they are sent
to their neighbors using NetworkSimulator.tolayer2(packet p) method.
In the update method (this is called when an entity received packets from its neighbors). Here as
soon as we get a packet we get its source of origin. And we use an array called minlist to hold
current least cost values. Then we apply the Distributed Bellman Ford algorithm to find the least
cost paths. After this we update the table.
If the cost from a node to its neighbor has changed then we have to notify our neighbors, So, to
do this we use a update flag which gets set when there is a change. If this flag is set then we
send our current table to all our neighbors.