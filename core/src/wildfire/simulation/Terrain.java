package wildfire.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import wildfire.simulation.Cell.State;
import wildfire.simulation.Cell.Wood;
import wildfire.simulation.Data.Direction;

/**
 * Represents map section as cells. Main class for a simulation.
 * @author Sebastian
 *
 */
public class Terrain {

	private Cell [][] 				terrainState;
	private static Set<Cell> 		treesOnFire = new HashSet<Cell>();
	
	public static Set<Cell> 		treesOnFireAdd = new HashSet<Cell>();
	public static Set<Cell> 		treesOnFireRemove = new HashSet<Cell>();
	public static Random 			randomGenerator = new Random();		
	public static int 				outBorderFire=0;
	public static int 				spotingCount=0;
	public int 						sizeX;
	public int 						sizeY;
	public int 						fuelCount = 0;
	
	
	Terrain(int _sizeX, int _sizeY, int probability, int vegtype, int relief, int roughness) //probability i roguhness zakres 0-100
	{		
		double l=0;
		fuelCount = 0;
		sizeX = _sizeX;
		sizeY = _sizeY;
		terrainState = new Cell[sizeY][sizeX];
		int life=20;
		int heightS = 500;
		int heightT = 10;
		
		for (int y = 0; y<sizeY; y++)
			for (int x = 0; x<sizeX; x++)
			{
				Wood wood;
				State s;
				if(probability > Terrain.randomGenerator.nextInt(100))
				{
					s = State.FUEL;
					if(vegtype > Terrain.randomGenerator.nextInt(100))
						{
						wood = Wood.OAK;
						life=Data.lifetime_oak;
						}
					else	
					{
						wood = Wood.PINY;
						life=Data.lifetime_piny;
					}
						
					
				}
				else		
				{
					s = State.FREE;
					wood = Wood.NONE;
				}																
				
				terrainState[y][x]=new Cell(new Cell.Coordinate(y, x), s, wood, life, heightS, heightT, 1);
				
				if(terrainState[y][x].getState() == Cell.State.FUEL) {
					l++;
					fuelCount++;
				}
			}
		
		System.out.println("Number of all cells: " + sizeX*sizeY);
		System.out.println("Number of all trees: " + l);
		System.out.println();
		terrainState[sizeY/2][sizeX/2]=new Cell(new Cell.Coordinate(sizeY/2, sizeX/2), State.FUEL, Wood.OAK, life, heightS, heightT, 1);
		defineNeighbors(Data.winddir);		
		generateElevation(relief);
		ignite();
		
	}

	private void defineNeighbors(Data.Direction wind_direction)			
	{
		
		for (int y = 0; y<sizeY; y++)
		{
			for (int x = 0; x<sizeX; x++)
			{
				if(terrainState[y][x].getState() == Cell.State.FUEL)		// dla ka¿dego drzewa, a nie dla kamieni
				{
					HashMap<Integer, TreeSet<Integer>> neighbors = terrainState[y][x].elipse(wind_direction.angle);					
					for(Integer xn : neighbors.keySet())						
					{
						for(Integer yn : neighbors.get(xn))
						{
							if(yn >= 0 && yn < sizeY && xn >= 0 && xn < sizeX)
								terrainState[y][x].addNeighbor(terrainState[yn][xn]);							
						}
					}					
				}
			}												
		}														
	}
		//rozpoczęcie pożaru - 9 środkowych drzew podpalanych
	public void ignite()														
	{
		
		Cell choosenTree;
		
		for(int i=-1;i<2;i++)
		{
			for(int j=-1;j<2;j++)
			{
				choosenTree= terrainState[sizeY/2-i][sizeX/2-j];
				choosenTree.setBurnThreshold(0);
				choosenTree.setState(Cell.State.BURNING);
				treesOnFire.add(choosenTree);
			}
		}
		
		
		
		
	}	
	
	
	//podstawowy generator, nie ma uwzglednienia wysokosci na jakiej sa sasiedzi
	public void generateElevation(int relief)
		{
			int probability=0;
			for (int y = 0; y<sizeY; y++)
				for (int x = 0; x<sizeX; x++)
				{
					probability = randomGenerator.nextInt(100);
					if (probability<10) terrainState[y][x].setElevation(relief);
					else if (probability<20) terrainState[y][x].setElevation(relief/5);
					else if (probability<50) terrainState[y][x].setElevation(relief/3);
					else if (probability<75) terrainState[y][x].setElevation(relief/2 + relief/5);
					else if (probability<90) terrainState[y][x].setElevation(relief/2 - relief/5);				
				}	
		}
		
	/**
	 * Spreading fire among trees. It invoke spreadFire() function from Cell class to ignite neighbors. 
	 */
	public void spreadFire()
	{
		
		for(Cell t : treesOnFire)
		{
			t.spreadFire();
		}	
		
		System.out.println("---treesOnFire: " + treesOnFire.size() + ", treesOnFireAdd: " + treesOnFireAdd.size()+ ", treesOnFireRemove: " + treesOnFireRemove.size());
		for(Cell temp : treesOnFireAdd)
		{
			treesOnFire.add(temp);
			
		}		
		treesOnFireAdd.clear();
		for(Cell temp : treesOnFireRemove)
		{
			treesOnFire.remove(temp);
		}		
		treesOnFireRemove.clear();
		
		spreadSpotting2(treesOnFire);
		System.out.println("Poza zasiêgiem albo nie trafi³o w cos palnego: "+Terrain.outBorderFire+" Spotting fire zap³on: "+Terrain.spotingCount);
	}	
	public boolean isAllBurnt()
	{
		return treesOnFire.isEmpty();
	}
	public Cell getCell(int y, int x)
	{
		return terrainState[y][x];
	}
	
	public String toString()
	{
		String txt = new String();
		for(int y = 0 ; y<sizeY; y++)
		{
			for(int x = 0 ; x<sizeX; x++)
			{
				txt = txt + terrainState[y][x].getState() + ", ";
			}
			txt = txt + "\n";
		}
		
		return txt;
	}
	
	
	public void spreadSpotting2(Set<Cell> paliSie){
		int X=0,Y=0,N=0,kX=0,kY=0,skalar=0,nowyX,nowyY;		
		double spottingDist=0;
		Random ran=new Random();
		
		switch (Data.winddir){		
		case N :{ X=0;Y=-1; break;}		
		case E :{ X=1;Y=0; break;}		
		case W : { X=-1;Y=0; break;}		
		case S :{ X=0;Y=1; break;}		
		case SW :{ X=-1;Y=1; break;}
		case SE :{ X=1;Y=1; break;}		
		case NE : { X=1;Y=-1; break;}
		case NW : { X=-1;Y=-1; break;}
		}
		
		
		for(int i=0;i<sizeX;i++)
		  for(int j=0;j<sizeY;j++){
			  Cell komorka=Terrain.this.getCell(j, i);
			  if(komorka.getState()==Cell.State.BURNING){
			//komórka dla której liczê
			
				  kX=i;
				  kY=j;
			
			nowyX=kX;
			nowyY=kY;
			//obliczenie iloœci pal¹cych siê drzew w okolicy 3x3
			N=getNeighnoursOnFire(kX,kY)+1;
			//wyliczenie maksymalnego dystansu spotiingu z Albiniego
			spottingDist=komorka.getSpottingDistance(N);
			
			//losowo wybiermay spotting distance z progiem górnym z Albiniego
			skalar=ran.nextInt((int) spottingDist);
			
			if(X!=0)nowyX=kX+X*skalar;
			if(Y!=0)nowyY=kY+Y*skalar;		
			if(nowyY>0 && nowyY<100 && nowyX>0 && nowyX<100){
			if(Terrain.this.terrainState[nowyY][nowyX].getState()==Cell.State.FUEL){
				Terrain.this.terrainState[nowyY][nowyX].setBurnthresholdToZero();				
				Terrain.spotingCount++;
					}
				}else Terrain.outBorderFire++;
			}		
		}
	}
	
	
	private int getNeighnoursOnFire(int x,int y) {
		int n=0;
		if(x+1<Terrain.this.sizeY && y+1<Terrain.this.sizeX && x>1 && y>1){
			if(this.terrainState[x+1][y+1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x][y+1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x+1][y].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x+1][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y+1].getState()==Cell.State.BURNING)n=n+1;
		}
		return n;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 6;
		//Terrain t = new Terrain(size,100,50);		
		
		//System.out.println(t.terrainState[2][2].neighbors);
		
		
		// testowanie
		Data.setDirection(Direction.S);
		Terrain t = new Terrain(5,100,50,(int)(Data.percent_oak),50,60);
		System.out.println(t.terrainState[2][2]);			
		System.out.println(t.terrainState[2][2].elipse(Direction.S.angle));
		while(!t.isAllBurnt())
		{
			System.out.println("-------------------------");		
			System.out.println(t.toString());
			
			t.spreadFire();			
		}
		
		System.out.println("-----------------------------");
		
		System.out.println("Cell: x=2 y=2");
		System.out.println("-----------");		
		System.out.println("E: " + t.terrainState[2][2].elipse(0));
		System.out.println("W: " + t.terrainState[2][2].elipse(180));
		System.out.println("N: " + t.terrainState[2][2].elipse(Data.Direction.N.angle));
		System.out.println("S: " + t.terrainState[2][2].elipse(Data.Direction.S.angle));
		
		
		
	}

	public double getFuelAlivePercent() {
		return (double)(fuelCount - getFuelBurntCount()) / fuelCount * 100;
	}

	public double getFuelBurntPercent() {
		return (double)getFuelBurntCount() / fuelCount * 100;
	}

	public int getFuelBurntCount() {
		int fuelBurnt = 0;
		for(int i=0;i<sizeX;i++)
			for(int j=0;j<sizeY;j++){
				if(Terrain.this.getCell(j, i).getState()== Cell.State.BURNT){
					fuelBurnt ++;
				}
			}
		if (fuelBurnt > fuelCount) return fuelCount -2;
		return fuelBurnt;
	}
	

}
