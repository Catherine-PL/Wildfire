package wildfire.simulation;

import java.util.HashSet;
import java.util.Set;

public class Cell {

	public enum State
	{
		BURNING, BURNT, TREE, FREE;
	}
	public enum Wood
	{
		OAK, PINY;
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
	private Wood type;
	private int lifetime;		// ilosc minut palenia siê paliwa, mo¿na dodaæ pole np. iloœæ ciep³a dla komórki
	private Coordinate coordinates; 	
	Set<Cell> neighbors = new HashSet<Cell>();  
		
	private void setParam(int y, int x, int life)
	{
		coordinates = new Coordinate(y,x);
		lifetime = life;						// ilosc cykli, 1 cykl to 1 minuta		
		if(Data.percent_piny > Terrain.randomGenerator.nextInt(100))
			type = Wood.PINY;
		else
			type = Wood.OAK;
		
	}
	
	public Cell(int y, int x, int probability)				// powierzchnia 1m x 1m - przydatne w rozprzestrzenianiu siê po¿aru
	{
		setParam(y,x,10);
		
		if(probability > Terrain.randomGenerator.nextInt(100))
			state = State.TREE;		
		else		
			state = State.FREE;				
	}	
	public Cell(int y, int x, Cell.State s, int life)		
	{
		setParam(y,x,life);						
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
