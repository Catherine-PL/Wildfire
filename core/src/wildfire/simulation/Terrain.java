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
	public int 						size;
	
	
	Terrain(int _size, int probability, int vegtype, int relief, int roughness) //probability i roguhness zakres 0-100
	{		
		double l=0;
		size = _size;
		terrainState = new Cell[size][size];
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
				
				terrainState[y][x]=new Cell(new Cell.Coordinate(y, x), s, wood, heightS, heightT, 1);
				
				if(terrainState[y][x].getState() == Cell.State.FUEL)
					l++;
			}
		
					
		System.out.println("Number of all cells: " + size*size);
		System.out.println("Number of all trees: " + l);
		System.out.println();
		defineNeighbors(Data.winddir);		
		generateElevation(relief);
		ignite();
		
	}

	private void defineNeighbors(Data.Direction wind_direction)			
	{
		
		for (int y = 0; y<size; y++)
		{
			for (int x = 0; x<size; x++)
			{
				if(terrainState[y][x].getState() == Cell.State.FUEL)		// dla ka¿dego drzewa, a nie dla kamieni
				{
					HashMap<Integer, TreeSet<Integer>> neighbors = terrainState[y][x].elipse(wind_direction.angle);					
					for(Integer xn : neighbors.keySet())						
					{
						for(Integer yn : neighbors.get(xn))
						{
							if(yn >= 0 && yn < size && xn >= 0 && xn < size)
								terrainState[y][x].addNeighbor(terrainState[yn][xn]);							
						}
					}					
				}
			}												
		}														
	}
	private void ignite()														// losowe drzewo podpolane
	{
		
		int s = size/2;
		Cell choosenTree;
		do
		{
			choosenTree= terrainState[s][s];			
			s++;			
		}
		while (choosenTree.getState() != Cell.State.FUEL);
		
		System.out.println("--- " + choosenTree.getState() + " -- " + choosenTree.getCoordinates());
		choosenTree.setState(Cell.State.BURNING);
		System.out.println("--- " + choosenTree.getState() + " -- " + choosenTree.getCoordinates());
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 6;
		//Terrain t = new Terrain(size,100,50);		
		
		//System.out.println(t.terrainState[2][2].neighbors);
		
		
		// testowanie
		Data.setDirection(Direction.S);
		Terrain t = new Terrain(5,100,(int)(Data.percent_oak),50,60);
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
		
	

	

}
