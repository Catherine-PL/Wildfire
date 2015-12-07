package wildfire.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import wildfire.simulation.Cell.State;
import wildfire.simulation.Cell.Wood;

public class Terrain {

	public int size;
	private Cell [][] terrainState;
	private static Set<Cell> treesOnFire = new HashSet<Cell>();
	public static Set<Cell> treesOnFireAdd = new HashSet<Cell>();
	public static Set<Cell> treesOnFireRemove = new HashSet<Cell>();
	public static Random randomGenerator = new Random();	
	
	
	Terrain(int _size, int probability,int relief)
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
					if(Data.percent_oak > Terrain.randomGenerator.nextInt(100))
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
		
					
		System.out.println("Number of all cells: " + size*size);
		System.out.println("Number of all trees: " + l);
		System.out.println();
		defineNeighbors();		
		generateElevation(relief);
		ignite();
		
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
		
	
	private void defineNeighbors()			// wyliczanie sasiedztwa, na razie wszystko do okola
	{
		int [] yn = {-1,0,1};
		int [] xn = {-1,0,1};
		for (int y = 0; y<size; y++)
		{
			for (int x = 0; x<size; x++)
			{
				
				for(int i=0; i<3; i++)				
				{
					if((y+yn[i] < size) && (y+yn[i] >= 0))
					{										
						for(int j=0; j<3; j++)
						{
							if(j==1 && i==1)
								continue;
							else if((x+xn[j] < size) && (x+xn[j] >= 0))
							{
							//	System.out.print("y: " + y + ", x: " + x);								
							//	System.out.println("; y+yn[i]: " + (y+yn[i]) + ",  x+xn[j]: " + (x+xn[j]));
								terrainState[y][x].addNeighbor(terrainState[y+yn[i]][x+xn[j]]);
							}
						}
					}
				}	
			}															
		}														
	}
/*	public void defineNeighbor()			// zle nie wazne
	{
		for (int y = 0; y<size; y++)
		{
			for (int x = 0; x<size; x++)
			{
				
				for(Data.Direction d : Data.Direction.values())
				{										
					int r = (int) Math.floor(terrainState[y][x].rothermel(d));		
					System.out.print(d + ": " + r + "; ");
				
					for(int i=1; i<=r;i++)
					{
						switch(d)
						{
						case N:
							if(y-i >= 0)
								terrainState[y][x].addNeighbor(terrainState[y-i][x]);
							break;							
						case NE:
							if(y-i >= 0 && x-i >=0)
								terrainState[y][x].addNeighbor(terrainState[y-i][x-i]);
							break;												
						case E:
							if(x+i < size)
								terrainState[y][x].addNeighbor(terrainState[y][x+i]);
							break;
						case SE:
							if(y+i < size && x+i < size)
								terrainState[y][x].addNeighbor(terrainState[y+i][x+i]);
							break;						
						case S:
							if(y+i < size)
								terrainState[y][x].addNeighbor(terrainState[y+i][x]);
							break;						
						case SW:
							if(y+i < size && x-i >=0)
								terrainState[y][x].addNeighbor(terrainState[y+i][x-i]);
							break;						
						case W:
							if(x-i >= 0)
								terrainState[y][x].addNeighbor(terrainState[y][x-i]);
							break;						
						case NW:
							if(y-i >= 0 && x-i >= 0)
								terrainState[y][x].addNeighbor(terrainState[y-i][x-i]);
							break;												
						}
						
							
							
					}
					
				}
				System.out.println(" -- " +y + ":" + x);
		
		
			}
		}
		
	}*/
	private void ignite()
	{
		
		Cell choosenTree;
		do
		{
			choosenTree= terrainState[randomGenerator.nextInt(size)][randomGenerator.nextInt(size)];
		}
		while (choosenTree.getState() == Cell.State.FREE);
		
		System.out.println("--- " + choosenTree.getState() + " -- " + choosenTree.getCoordinates());
		choosenTree.setState(Cell.State.BURNING);
		System.out.println("--- " + choosenTree.getState() + " -- " + choosenTree.getCoordinates());
		treesOnFire.add(choosenTree);
		
	}	
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 6;
		Terrain t = new Terrain(size,100,50);		
		
		System.out.println(t.terrainState[2][2].neighbors);
		/*
		while(!t.isAllBurnt())
		{
			System.out.println("-------------------------");		
			System.out.println(t.toString());
			t.spreadFire();			
		}
		*/
		
		
	}
		
	

	

}
