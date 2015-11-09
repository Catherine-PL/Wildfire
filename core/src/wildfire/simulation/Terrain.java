package wildfire.simulation;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import wildfire.simulation.Ellipse.Coordinate;

public class Terrain {

	
	private int size;
	private Cell [][] terrainState;
	Set<Cell> treesOnFire = new HashSet<Cell>();
	Ellipse wave = new Ellipse();
	
	static Random randomGenerator = new Random();
	
	
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
	
	

	private void defineNeighbors()
	{
		for (int y = 0; y<size; y++)
			for (int x = 0; x<size; x++)
				for(Coordinate c: wave.xy)
					if(y+c.y < size && y+c.y >= 0 && x+c.x < size && x+c.x >= 0 )
						terrainState[y][x].neighbors.add(terrainState[y+c.y][x+c.x]);					
	}
	private void ignite()
	{
		
		Cell choosenTree;
		do
		{
			choosenTree= terrainState[randomGenerator.nextInt(size)][randomGenerator.nextInt(size)];
		}
		while (choosenTree.getState() == Cell.State.FREE);
		
		System.out.println("--- " + choosenTree.getState());
		choosenTree.setState(Cell.State.BURNING);
		System.out.println("--- " + choosenTree.getState());
		treesOnFire.add(choosenTree);
		
	}	
	public void spreadFire()
	{
		for(Cell c : treesOnFire)
		{
			c.spreadFire(treesOnFire);
		}
	}	
	public boolean isAllBurnt()
	{
		return treesOnFire.isEmpty();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Terrain t = new Terrain(2,100);
		
		for(int y = 0 ; y<2; y++)
			for(int x = 0 ; x<2; x++)
				System.out.println(t.terrainState[y][x].getState());

	}

}
