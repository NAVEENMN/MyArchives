package networks;

public class Entity2 extends Entity
{    
    // Perform any necessary initialization in the constructor
    public Entity2()
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
    		distanceTable[i][i] = NetworkSimulator.cost[2][i];
    	}
    	
    	//-----------------------------------------------------> finding the minimum cost array
        for(int h = 0; h < 4; h++){
        	minDist[h]= distanceTable[h][h];
          }
    	//------------------------------------------------------> Sending packets to neighbors
    	for(int i = 0; i < 4; i++){
            if(i != 2){
              Packet dtPacket = new Packet(2, i, minDist);
              NetworkSimulator.toLayer2(dtPacket);
            }
          }
    	//-------------------------------------------------------> Setup completed
        System.out.println("Node 2 Initializion Complete. Distance Table is:");
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
    	int[] neighbors={0,1,3}; // neighbors are hard coded
    	int[] minlist = new int[4];
    	int src=p.getSource();
    	boolean update=false,flag=false;
    	int min=0;
    	//--------------------------------------------------------------------> Bellman ford algo
    	for(int i=0;i<neighbors.length;i++)
    	{
    		if (distanceTable[neighbors[i]][src]>NetworkSimulator.cost[2][src] + p.getMincost(neighbors[i]))
    		{
    			distanceTable[neighbors[i]][src]=NetworkSimulator.cost[2][src] + p.getMincost(neighbors[i]);
    			flag=true; // set the flag if new low cost found
    		}
    	}	
    	//-----------------------------------------------------------------------------------------------
    	//-------------------------------------------------------> Fetching the new mincost array	
    		for(int k=0;k<NetworkSimulator.NUMENTITIES;k++){
    			min=999;
    			for (int m=0;m<NetworkSimulator.NUMENTITIES;m++)
    			{
    			if (min>distanceTable[k][m])
    			{
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
    	minlist[2]=0;
    	if (update)
    	{
    		Packet p2_0 = new Packet(2,0,minlist);
        	Packet p2_1 = new Packet(2,1,minlist);
        	Packet p2_3 = new Packet(2,3,minlist);
            NetworkSimulator.toLayer2(p2_0);
            NetworkSimulator.toLayer2(p2_1);
            NetworkSimulator.toLayer2(p2_3);
    	}
    	//-------------------------------------------------------------------------------------------------
    	System.out.println("Node 2 Updated Distance Table:");
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
        System.out.println(" D2 |   0   1   3");
        System.out.println("----+------------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            if (i == 2)
            {
                continue;
            }
            
            System.out.print("   " + i + "|");
            for (int j = 0; j < NetworkSimulator.NUMENTITIES; j++)
            {
                if (j == 2)
                {
                    continue;
                }
                
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
