package networks;

public class Entity3 extends Entity
{    
    // Perform any necessary initialization in the constructor
    public Entity3()
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
    		distanceTable[i][i] = NetworkSimulator.cost[3][i];
    	}
    	
    	//-----------------------------------------------------> finding the minimum cost array
        for(int h = 0; h < 4; h++){
        	minDist[h]= distanceTable[h][h];
          }
    	//------------------------------------------------------> Sending packets to neighbors
    	Packet dtPacket = new Packet(3, 0, minDist);
        NetworkSimulator.toLayer2(dtPacket);
        dtPacket = new Packet(3, 2, minDist);
        NetworkSimulator.toLayer2(dtPacket);
    	//-------------------------------------------------------> Setup completed
        System.out.println("Node 3 Initializion Complete. Distance Table is:");
        printDT();
    	
    }
    
    // Handle updates when a packet is received.  Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p)
    {
    	int[] neighbors={0,1,2}; // neighbors are hard coded
    	int[] minlist = new int[4];
    	int src=p.getSource();
    	boolean update=false,flag=false;
    	int min=0;
    	//--------------------------------------------------------------------> Bellman ford algo
    	for(int i=0;i<neighbors.length;i++)
    	{
    		if (distanceTable[neighbors[i]][src]>NetworkSimulator.cost[3][src] + p.getMincost(neighbors[i]))
    		{
    			distanceTable[neighbors[i]][src]=NetworkSimulator.cost[3][src] + p.getMincost(neighbors[i]);
    			flag=true; // set the flag if new low cost found
    		}
    	}	
    	//-----------------------------------------------------------------------------------------------
    	//-------------------------------------------------------> Fetching the new mincost array
    		for(int k=0;k<NetworkSimulator.NUMENTITIES;k++)
    		{
    			min=999;
    			for (int m=0;m<NetworkSimulator.NUMENTITIES;m++)
    			{
    			if (min>distanceTable[k][m])
    			{
    				min=distanceTable[k][m];
    			}
    			minlist[k]=min; // set the flag if new low cost found
    		  }
    		}
    		
    		if (flag){
    			update=true; // set the update DV table flag to send new packets to other nodes
    			flag=false;
    			
    		}
    	//------------------------------------------------------------------------------------------------	
        //------------------------------------------------------> Send the new packets to neighbors
    	minlist[3]=0;
    	if (update){
    		Packet p3_0 = new Packet(3,0,minlist);
        	Packet p3_2 = new Packet(3,2,minlist);
        	NetworkSimulator.toLayer2(p3_0);
            NetworkSimulator.toLayer2(p3_2);     
    	}
    	//-------------------------------------------------------------------------------------------------
    	System.out.println("Node 3 Updated Distance Table:");
    	System.out.println(" Time : " + NetworkSimulator.time);
        printDT();
    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
    }
    
    public void printDT()
    {
        System.out.println("         via");
        System.out.println(" D3 |   0   2");
        System.out.println("----+--------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            if (i == 3)
            {
                continue;
            }
            
            System.out.print("   " + i + "|");
            for (int j = 0; j < NetworkSimulator.NUMENTITIES; j += 2)
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
