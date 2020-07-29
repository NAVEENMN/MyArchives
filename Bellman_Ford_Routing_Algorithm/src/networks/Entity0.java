package networks;

public class Entity0 extends Entity
{    
    // Perform any necessary initialization in the constructor
    public Entity0()
    {
    	int[] minDist = new int[NetworkSimulator.NUMENTITIES];
    	//-----------------------------------------------------> Initialize the cost to infinity
    	for(int i = 0; i< NetworkSimulator.NUMENTITIES; i++)
    	{
    		for(int j = 0; j< NetworkSimulator.NUMENTITIES; j++)
    		{
    			distanceTable[i][j]  = 999 ;
    		}
    		 
    	}
    	//-----------------------------------------------------> Initialize the costs provided
    	
    	for(int i = 0; i< NetworkSimulator.NUMENTITIES; i++)
    	{
    		distanceTable[i][i] = NetworkSimulator.cost[0][i];
    	}
  
    	//-----------------------------------------------------> finding the minimum cost array
    	for(int h = 0; h < 4; h++){
    		minDist[h]= distanceTable[h][h];
          }
    	//------------------------------------------------------> Sending packets to neighbors
    	for(int i = 0; i < NetworkSimulator.NUMENTITIES; i++){
    		
    	          Packet dtPacket = new Packet(0, i, minDist);
    	          NetworkSimulator.toLayer2(dtPacket);
    	        
          }
    	//-------------------------------------------------------> Setup completed
        System.out.println("Node 0 Initializion Complete. Distance Table is:");
        System.out.println("time"+NetworkSimulator.time);
        printDT();
    }
    
    // Handle updates when a packet is received.  Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p)
    {   
    	int[] neighbors={1,2,3}; // neighbors are hard coded
    	int[] minlist = new int[4];
    	int src=p.getSource();
    	boolean update=false,flag=false;
    	int min=0;
    	//--------------------------------------------------------------------> Bellman ford algo
    	for(int i=0;i<neighbors.length;i++)
    	{
    		if (distanceTable[neighbors[i]][src]>NetworkSimulator.cost[0][src] + p.getMincost(neighbors[i]))
    		{
    			distanceTable[neighbors[i]][src]=NetworkSimulator.cost[0][src] + p.getMincost(neighbors[i]);
    			flag=true;// set the flag if new low cost found
    		}
    	}	
    	//-----------------------------------------------------------------------------------------------
    	//-------------------------------------------------------> Fetching the new mincost array
    	for(int k=0;k<NetworkSimulator.NUMENTITIES;k++){
    		min=999;
    		for (int m=0;m<NetworkSimulator.NUMENTITIES;m++){
    		if (min>distanceTable[k][m]){
    			min=distanceTable[k][m];
    		}
    		minlist[k]=min;
    	  }
    	}
    	if (flag){
    		update=true; // set the update DV table flag to send new packets to other nodes
    		flag=false;
    	}
    	//------------------------------------------------------------------------------------------------	
    	//------------------------------------------------------> Send the new packets to neighbors 
    	minlist[0]=0;
    	if (update){
    		Packet p0_1 = new Packet(0,1,minlist);
        	Packet p0_2 = new Packet(0,2,minlist);
        	Packet p0_3 = new Packet(0,3,minlist);
            NetworkSimulator.toLayer2(p0_1);
            NetworkSimulator.toLayer2(p0_2);
            NetworkSimulator.toLayer2(p0_3);
    	}
    	//-------------------------------------------------------------------------------------------------
    	 System.out.println(" Node 0 Updated Distance Table:");
         System.out.println(" Time : " + NetworkSimulator.time);
         printDT();
    	
    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
    }
    
    public void printDT()
    {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D0 |   1   2   3");
        System.out.println("----+------------");
        for (int i = 1; i < NetworkSimulator.NUMENTITIES; i++)
        {
            System.out.print("   " + i + "|");
            for (int j = 1; j < NetworkSimulator.NUMENTITIES; j++)
            {
                if (distanceTable[i][j] < 10)
                {    
                    System.out.print("   ");
                }
                else if (distanceTable[i][j] < 100)
                {
                    System.out.print("  ");
                }
                else 
                {
                    System.out.print(" ");
                }
                
                System.out.print(distanceTable[i][j]);
            }
            System.out.println();
        }
    }
}
