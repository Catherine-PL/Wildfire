package wildfire.simulation;

import java.util.HashSet;
import java.util.Set;

public class Cell {

	public enum State
	{
		BURNING, BURNT, TREE, FREE;
	}
	
	class Coordinate{
		int x;
		int y;
		
		Coordinate(int _y, int _x)
		{
			x = _x;
			y = _y;
		}
		
		public String toString()
		{
			return ("y:" + y + " - x:" + x);
		}
	}
	
	private State state;
	private int lifetime;		// ilosc minut palenia siê paliwa, mo¿na dodaæ pole np. iloœæ ciep³a dla komórki
	private Coordinate coordinates; 	
	Set<Cell> neighbors = new HashSet<Cell>();  
	

	public Cell(int y, int x, int probability)				// powierzchnia 1m x 1m - przydatne w rozprzestrzenianiu siê po¿aru
	{
		coordinates = new Coordinate(y,x);
		lifetime = 10;						// ilosc cykli, 1 cykl to 1 minuta
		
		if(probability > Terrain.randomGenerator.nextInt(100))
		{
			//System.out.println("TREE");
			state = State.TREE;
		}
		else
		{
			//System.out.println("FREE");
			state = State.FREE;
		}
		
	}	
	public Cell(int y, int x, Cell.State s, int life)		
	{
		coordinates = new Coordinate(y,x);
		lifetime = life;						
		state = s;
		
	}
	public void setState(State s)
	{
		state = s;
	}	
	public State getState()
	{
		return state;
	}
	public void addNeighbor(Cell n)
	{
		neighbors.add(n);
	}
	public Coordinate getCoordinates()
	{
		return this.coordinates;
	}
 	public void spreadFire()
	{
 		HashSet<Cell> r = new HashSet<Cell>();
		lifetime--;					
		if(lifetime == 0)
		{
			state = State.BURNT;
			Terrain.treesOnFireRemove.add(this);
		}
		
		for(Cell c : neighbors)
		{
			if(c.state == State.TREE)
			{			
				c.state = State.BURNING;
				Terrain.treesOnFireAdd.add(c);
			}									
		}
		
	}
	
	public String toString()
	{
		return (this.coordinates.toString());
	}
	
}
