package wildfire.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import wildfire.simulation.Cell.State;
import wildfire.simulation.Cell.Wood;

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
	public int 						size;
	
	
	Terrain(int _size, int probability, int vegtype, int relief, int roughness) //probability i roguhness zakres 0-100
	{		
		double l=0;
		size = _size;
		terrainState = new Cell[size][size];
		int life = 10;
		int heightS = 500;
		int heightT = 10;
		
		for (int y = 0; y<size; y++)
			for (int x = 0; x<size; x++)
			{
				Wood wood;
				State s;
				if(probability > Terrain.randomGenerator.nextInt(100))
				{
					s = State.FUEL;
					if(vegtype > Terrain.randomGenerator.nextInt(100))
						wood = Wood.OAK;		
					else		
						wood = Wood.PINY;
					
				}
				else		
				{
					s = State.FREE;
					wood = Wood.NONE;
				}																
				
				terrainState[y][x]=new Cell(new Cell.Coordinate(y, x), s, wood, life, heightS, heightT, 1);
				
				if(terrainState[y][x].getState() == Cell.State.FUEL)
					l++;
			}
		
		terrainState[size/2][size/2]=new Cell(new Cell.Coordinate(size/2, size/2), State.FUEL, Wood.OAK, life, heightS, heightT, 1);
		defineNeighbors(Data.winddir);		
		generateElevation(relief);
		
	}

	private void defineNeighbors(Data.Direction wind_direction)			
	{
		
		for (int y = 0; y<size; y++)
		{
			for (int x = 0; x<size; x++)
			{
				HashMap<Integer, TreeSet<Integer>> neighbors = terrainState[y][x].elipse(wind_direction.angle);
				for(Integer xn : neighbors.keySet())
				{
					for(Integer yn : neighbors.get(xn))
					{
						if(yn > 0 && yn < size && xn > 0 && xn < size)
							terrainState[y][x].addNeighbor(terrainState[yn][xn]);
					}
				}
			}												
		}														
	}
	public void ignite()														// losowe drzewo podpolane
	{
		
		Cell choosenTree;
		choosenTree= terrainState[size/2][size/2];
		choosenTree.setState(Cell.State.BURNING);
		treesOnFire.add(choosenTree);
		
	}	
	
	
	//podstawowy generator, nie ma uwzglednienia wysokosci na jakiej sa sasiedzi
	public void generateElevation(int relief)
		{
			int probability=0;
			for (int y = 0; y<size; y++)
				for (int x = 0; x<size; x++)
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
		for(int y = 0 ; y<size; y++)
		{
			for(int x = 0 ; x<size; x++)
			{
				txt = txt + terrainState[y][x].getState() + ", ";
			}
			txt = txt + "\n";
		}
		
		return txt;
	}
	
	public void spreadSpotting(){
		Set<Cell> onFire= this.treesOnFire;
		Set<Cell> newOnFire=new HashSet<Cell>();
		double spDist;
		
		int N,Y=0,X=0;
		int skalar=1;
		switch (Data.winddir){
		case N :{ X=0;Y=1; break;}
		case NE :{ X=1;Y=1; break;}
		case E :{ X=1;Y=0; break;}
		case SE :{ X=1;Y=-1; break;}
		case S :{ X=0;Y=-1; break;}
		case SW : { X=-1;Y=-1; break;}
		case W : { X=-1;Y=0; break;}
		case NW : { X=-1;Y=1; break;}		
		}
		for(Cell cell:onFire){
			int cX=cell.getCoordinates().x;
			int cY=cell.getCoordinates().y;
			N=getNeighnoursOnFire(cX,cY);
			spDist=cell.getSpottingDistance(1+N);
			skalar=randomGenerator.nextInt((int)spDist);
			Y=Y*skalar+cY;
			X=X*skalar+cX;
			if(X>0 && Y>0 && Y<size && X<size){
				newOnFire.add(Terrain.this.getCell(Y, X));
			}
		}
		for(Cell cell:newOnFire){
			if(cell.getState()==Cell.State.FUEL)
				treesOnFireAdd.add(cell);				
		}
	}
	private int getNeighnoursOnFire(int x,int y) {
		int n=0;
		if(x+1<Terrain.this.size && y+1<Terrain.this.size && x>1 && y>1){
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
		/*
		while(!t.isAllBurnt())
		{
			System.out.println("-------------------------");		
			System.out.println(t.toString());
			t.spreadFire();			
		}
		*/
		Terrain t = new Terrain(60,50,(int)(Data.percent_oak),50,60);
		System.out.println(t.terrainState[20][20].neighbors);
		
	}
		
	

	

}
