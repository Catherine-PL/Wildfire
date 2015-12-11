package wildfire.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Cell {

	public enum State
	{
		BURNING, BURNT, FUEL, FREE;
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
	private int 			elevation;
	private State 			state;
	private Wood 			type;
	private int 			lifetime;									// ilosc minut palenia siê paliwa, mo¿na dodaæ pole np. iloœæ ciep³a dla komórki	
	private long 			heightSea;									// jednostka m
	private double 			heightTree;									// jednostka m
	private int 			size = 1;									// bok kwadratu - ilosc metrow
	
	private double			a;											// Fala Huygensa
	private double			b;											// Na ich bazie robimy sasiedztwo
	private double			c;											// odleglosc od punktu zaplonu do srodka elipsy
					
	/**
	 * (x^2 / b^2) + y^2 / a^2 = 1
	 * 
	 *   x = b * cos(t)  t: 0 - 2PI
	 *   y = a * sin(t)
	 *   
	 * */
	
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
	
	
	public void setElevation(int e)
	{
		elevation=e;
	}	
	public int getElevation()
	{
		return elevation;
	}
	
	private double lb()
	{
		double U = Data.wind;
		return 0.936*Math.pow(Math.E, 0.2566*U) + 0.461*Math.pow(Math.E, -0.1548*U) - 0.397; 
	}
	private double hb()
	{
		double p = Math.pow(Math.pow(lb(), 2) - 1, 0.5);
		return (lb() + p)/(lb() - p);
	}
	private void wspolczynniki()
	{
		double r = rothermel();
		//System.out.println("r: " + r);
		
		this.a = 0.5 * (r + (r/hb())) / lb();
		this.b = (r + (r/hb())) / 2.0;
		this.c = this.b - ( r / hb() );
		/*
		System.out.println("----Wspolczynniki elipsy: ");
		System.out.println("a: " + a);
		System.out.println("b: " + b);
		System.out.println("c: " + c);
		*/
	}
	public HashMap<Integer, HashSet<Integer>> elipse(double angle)		// kat wzgledem wiatru na wschod (x), odwrotnie niz zegar
	{		
		this.wspolczynniki();
		
		HashMap<Integer,HashSet<Integer>> elipse = new HashMap<Integer,HashSet<Integer>>();
		HashMap<Integer,HashSet<Integer>> result = new HashMap<Integer,HashSet<Integer>>();
		
	
		double step = Math.PI / 180;
		double radian = angle * step;
				
		double e2 = ((b*b) - (a*a)) / (b*b);
		
		
		for(double i=0; i<(2*Math.PI); i = i + step)					// ca³a elipsa z obrotem
		{
			
			double r = Math.sqrt((a*a) / (1 - e2*Math.cos(i-radian)*Math.cos(i-radian)));
			
						
			int x = (int) Math.round(r * Math.cos(i));
			int y = (int) Math.round(r * Math.sin(i));
			
			
			if(!elipse.containsKey(x))
				elipse.put(x, new HashSet<Integer>());
			
			elipse.get(x).add(y);
			
			//System.out.println("r:" + r + "; cos:" + Math.cos(i-radian));
			//System.out.print("xy:" + x + y + "; ");																		
			
		}
		
		
											
		//*	wypelnienie srodka
		Set<Integer> keys = new HashSet<Integer>(elipse.keySet()); 		
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext())
		{
			Integer key = it.next();		
			HashSet<Integer> set = elipse.get(key);											// moze tree set?
			Integer max=0;
			Integer min=0;
			
			for(Integer v : set)
			{
				if(max < v)
					max = v;
				if(min > v)
					min = v;
			}
			for(int i=min+1; i < max-min-1; i++)
			{
				set.add(i);
			}
			elipse.remove(key);
			elipse.put(key, set);
		}
		//*/
		
		
		int xz = (int) Math.round(c * Math.cos(Math.PI+radian));
		int yz = (int) Math.round(c * Math.sin(Math.PI+radian));
	
		/*
		System.out.println();
		System.out.println(elipse);			// elipsa, ale od jej srodka a nie od miejsca zaplonu.	
		System.out.println("----zaplon----");
		System.out.println("x: " + xz + "  y: " + yz);			// miejsce zaplonu.		
		*///
		
		elipse.get(xz).remove(yz);								// usuniecie miejsca zaplonu z sasiadow
		//System.out.println(elipse);			// elipsa, ale od jej srodka a nie od miejsca zaplonu.
		
		for(Integer x : elipse.keySet())
		{
			int xn = coordinates.x + x - xz;			
			result.put(xn, new HashSet<Integer>());
			for(Integer y : elipse.get(x))
			{
				result.get(xn).add(coordinates.y - (y - yz));			// bo oœ y roœnie w dó³
			}
			
			
		}
		
		return result;
	}
	
	private double rothermel()	// szybkosc rozchodzenia sie pozaru dla jego czola
	{
		
		double density;
		//double wind = Data.wind;
		double wind = 0;
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
	
	public Wood getType()
	{
		return type;
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
			if(c.state == State.FUEL)
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
		Cell c = new Cell(new Cell.Coordinate(10, 10), State.FUEL, Wood.OAK, 10, 500, 10, 1);
/*		System.out.println(c.rothermel(Data.Direction.N));
		System.out.println(c.rothermel(Data.Direction.NE));
		System.out.println(c.rothermel(Data.Direction.E));
		System.out.println(c.rothermel(Data.Direction.SE));
		System.out.println(c.rothermel(Data.Direction.S));
		System.out.println(c.rothermel(Data.Direction.SW));
		System.out.println(c.rothermel(Data.Direction.W));
		System.out.println(c.rothermel(Data.Direction.NW));
*/		System.out.println("-----------");
		c.wspolczynniki();
		
		System.out.println("Cell: x=10 y=10");
		System.out.println(c.elipse(0));
		//c.defineNeighbor();
		
	}
}
