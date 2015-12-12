package wildfire.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Cell class represents basic unit of Cellular Automata simulation 
 * @author Sebastian
 *
 */
public class Cell {

	/**
	 * Available states of our cell
	 * @author Sebastian
	 *
	 */
	public enum State
	{
		BURNING, BURNT, FUEL, FREE;
	}
	/**
	 * Types of wood
	 * @author Sebastian
	 *
	 */
	public enum Wood
	{
		OAK, PINY, NONE;
	}
	
	/**
	 * Class which contain coordinates y and x
	 * @author Sebastian
	 *
	 */
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
	
	/* Elispe factors */
	private double			a;											// Fala Huygensa
	private double			b;											// Na ich bazie robimy sasiedztwo
	private double			c;											// odleglosc od punktu zaplonu do srodka elipsy
						
	Set<Cell> 				neighbors = new HashSet<Cell>();  
		
	
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
	
	/* Set, Get funcitons */
	public void 		setElevation(int e)
	{
		elevation=e;
	}	
	public void 		setState(State s)
	{
		state = s;
	}	
	public int 			getElevation()
	{
		return elevation;
	}	
	public State	 	getState()
	{
		return state;
	}
	public Wood 		getType()
	{
		return type;
	}
	public Coordinate 	getCoordinates()
	{
		return this.coordinates;
	}
 	
	
	/**
	 * Calculates nieghbors in a shape of elipse which is related with rothermel without any restrictions.
	 * @param angle Angle of wind from East direction.
	 * @return All neighbors to burn in HashMap where key is Integer x and value is a HashSet of Integers y
	 */
	public HashMap<Integer, TreeSet<Integer>> elipse(double angle)		
	{		
		this.wspolczynniki();
		
		HashMap<Integer,TreeSet<Integer>> elipse = new HashMap<Integer,TreeSet<Integer>>();
		HashMap<Integer,TreeSet<Integer>> result = new HashMap<Integer,TreeSet<Integer>>();
			
		double step = Math.PI / 180;
		double radian = angle * step;				
		double e2 = ((b*b) - (a*a)) / (b*b);
		
		/* Stworzenie elipsy wraz z obrotem */
		for(double i=0; i<(2*Math.PI); i = i + step)
		{			
			double r = Math.sqrt((a*a) / (1 - e2*Math.cos(i-radian)*Math.cos(i-radian)));
									
			int x = (int) Math.round(r * Math.cos(i));
			int y = (int) Math.round(r * Math.sin(i));
						
			if(!elipse.containsKey(x))
				elipse.put(x, new TreeSet<Integer>());
			
			elipse.get(x).add(y);																										
		}
		
													
		/*	wypelnienie srodka */
		Set<Integer> keys = new HashSet<Integer>(elipse.keySet()); 		
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext())
		{
			Integer key = it.next();		
			TreeSet<Integer> set = elipse.get(key);
			
			Integer max= set.last();
			Integer min= set.first();
						
			for(int i=min+1; i < max-min-1; i++)
			{
				set.add(i);
			}
			elipse.remove(key);
			elipse.put(key, set);
		}

		
		/* obrót miejsca zap³onu */
		int xz = (int) Math.round(c * Math.cos(Math.PI+radian));	
		int yz = (int) Math.round(c * Math.sin(Math.PI+radian));

		elipse.get(xz).remove(yz);										// usuniecie miejsca zaplonu z sasiadow


		/* Przesuniecie elipsy na wspó³rzêdne komórki */
		for(Integer x : elipse.keySet())
		{
			int xn = coordinates.x + x - xz;			
			result.put(xn, new TreeSet<Integer>());
			for(Integer y : elipse.get(x))
			{
				result.get(xn).add(coordinates.y - (y - yz));			// bo oœ y roœnie w dó³
			}
		}
		
		return result;
	}	
	/**
	 * Adding neighbor to cell
	 * @param n A cell from terrain which should be consider as neighbor
	 */
	public void addNeighbor(Cell n)
	{
		neighbors.add(n);
	}
	/**
	 * Spreading fire means ignite all neighbors and decrease lifetime.
	 * It checks lifetime and type of neighbors.
	 */
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
		c.wspolczynniki();
				
		System.out.println("Cell: x=10 y=10");
		System.out.println("-----------");
		System.out.println(c.elipse(0));
		System.out.println(c.elipse(180));
		System.out.println(c.elipse(Data.Direction.W.angle));
		System.out.println(c.elipse(Data.Direction.NW.angle));
		
		
	}
}
