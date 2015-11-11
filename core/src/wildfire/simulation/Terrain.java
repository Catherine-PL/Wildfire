package wildfire.simulation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Terrain {

	public int size;
	private Cell [][] terrainState;
	private static Set<Cell> treesOnFire = new HashSet<Cell>();
	public static Set<Cell> treesOnFireAdd = new HashSet<Cell>();
	public static Set<Cell> treesOnFireRemove = new HashSet<Cell>();
	public static Random randomGenerator = new Random();
	
	
	Terrain(int _size, int trees)
	{		
		size = _size;
		terrainState = new Cell[size][size];
		
		for (int y = 0; y<size; y++)
			for (int x = 0; x<size; x++)
				terrainState[y][x]=new Cell(y,x,trees);
			
		defineNeighbors();		
		ignite();
		
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
		int size = 4;
		Terrain t = new Terrain(size,50);
		
		while(!t.isAllBurnt())
		{
			System.out.println("-------------------------");		
			System.out.println(t.toString());
			t.spreadFire();			
		}
		
	}
		
	

	

}
