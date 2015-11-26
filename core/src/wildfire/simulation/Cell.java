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
		OAK, PINY, NONE;
	}
	
	static class Coordinate{
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
	
	private Coordinate 		coordinates; 
	private State 			state;
	private Wood 			type;
	private int 			lifetime;									// ilosc minut palenia siê paliwa, mo¿na dodaæ pole np. iloœæ ciep³a dla komórki	
	private long 			heightSea;									// jednostka m
	private double 			heightTree;									// jednostka m
	private int 			size = 1;									// bok kwadratu - ilosc metrow
	Set<Cell> 				neighbors = new HashSet<Cell>();  
		
		
	public Cell(Coordinate yx, State state, Wood type, int lifetime, long heightSea, double heightTree, int size)		
	{
		this.coordinates = yx;					
		this.state = state;		
		this.type = type;
		this.lifetime = lifetime;
		this.heightSea = heightSea;
		this.heightTree = heightTree;
		this.size = size;
	}
	
	public double rothermel(Data.Direction d)	// trzeba bedzie poprawic, moze jendak na katach
	{
					
		
		
		double density;
		double wind = Data.windValue(d);
		double terrain = 0;
		
		double ip_0 = 0;
		
		if(type == Wood.OAK)
		{
			density = Data.density_oak;
			ip_0 = Data.ip_0_oak;
		}
		else
		{
			density = Data.density_piny;
			ip_0 = Data.ip_0_oak;
		}
				
		double licznik = ip_0*(1 + wind + terrain);
		double mianownik = density * Data.e * Data.q_ig; 
		
		/*
		System.out.println("----Dla kierunku: " + d + "----");		
		System.out.println("Ip_0:		" + ip_0);
		System.out.println("wind:		" + wind);
		System.out.println("terrain:	" + terrain);
		
		System.out.println();
		
		System.out.println("density:	" + density);
		System.out.println("e:		" + Data.e);
		System.out.println("Q_iq:		" + Data.q_ig);
		System.out.println();
		
		System.out.println("licznik: " + licznik + ", mianownik: " + mianownik);
		System.out.println("Rothermel:");
		*/
		
		return  licznik / mianownik; 
		//return  Math.floor(licznik / mianownik); 
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
	
	public static void main(String[] args)
	{
		Cell c = new Cell(new Cell.Coordinate(1, 1), State.TREE, Wood.OAK, 10, 500, 10, 1);
		System.out.println(c.rothermel(Data.Direction.N));
		System.out.println(c.rothermel(Data.Direction.NE));
		System.out.println(c.rothermel(Data.Direction.E));
		System.out.println(c.rothermel(Data.Direction.SE));
		System.out.println(c.rothermel(Data.Direction.S));
		System.out.println(c.rothermel(Data.Direction.SW));
		System.out.println(c.rothermel(Data.Direction.W));
		System.out.println(c.rothermel(Data.Direction.NW));
		
	}
}
