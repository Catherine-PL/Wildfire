package wildfire.simulation;

import java.util.HashSet;
import java.util.Set;

public class Cell {

	public enum State
	{
		BURNING, BURNED, TREE, FREE;
	}
	
	private State state;
	private int lifetime;
	private int[] coordinates = new int[2]; 	
	Set<Cell> neighbors = new HashSet<Cell>();  
	
	public Cell(int x, int y, int probability)				// powierzchnia 1m x 1m - przydatne w rozprzestrzenianiu siê po¿aru
	{
		coordinates[0] = x;
		coordinates[1] = y;	
		lifetime = 10;						// ilosc cykli, 1 cykl to 1 minuta
		
		if(probability > Terrain.randomGenerator.nextInt(100))
		{
			System.out.println("TREE");
			state = State.TREE;
		}
		else
		{
			System.out.println("FREE");
			state = State.FREE;
		}
		
	}	
	public void setState(State s)
	{
		state = s;
	}	
	public State getState()
	{
		return state;
	}
	public void spreadFire(Set<Cell> burning)
	{
		lifetime--;
		if(lifetime == 0)
		{
			state = State.BURNED;
			burning.remove(this);
		}
		
		for(Cell c : neighbors)
		{
			if(c.state == State.TREE)
			{			
				c.state = State.BURNING;
				burning.add(c);
			}									
		}
	}
	
}
