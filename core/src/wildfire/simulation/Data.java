package wildfire.simulation;

/**
 * Aggregation of all needed parameters to wildfire simulation.
 * @author Sebastian
 *
 */
final public class Data {
	
	public enum Direction
	{
		N(90), NE(45), E(0), SE(-45), S(-90), SW(-135), W(180), NW(135);
		
		int angle;
		
		Direction(int angle_from_E)
		{
			angle = angle_from_E; 
		}
	}
	
	//MJ  wektory kierunkowe odpowiadaj¹ce kierunkom œwiata
	//N(0,1) NE(1,1) E(1,0), SE(1,-1), S(0,-1), SW(-1,-1), W(-1,0), NW(-1,1)
	//pobieramy getDirVector(int i) i jest 0-7 i zwraca nam który chcemy
	private static int[][] dirVector={{0,1,1,1,0,-1,-1,-1},{1,1,0,-1,-1,-1,0,1}};//get w linii 110  -- co to? (Sebastian siê zastanawia)
																				//np. getDirVector(1) zwróci {1,1} i ten wektor symbolizuje NE,
																			// to s¹ kierunki z uk³adu wspó³rzêdnych, 0,1 to N, 0,-1 to S


	static final int x = 0;
	
	static double wind = 4;										// 0 - 30, 30> huragan
	static double terrain = 1.2;								// not sure //MJ to jest wspó³czynnik nachylenia??
	static double fuel_humidity = 1.0;		
	static int air_humidity = 1;	

	static double q_ig = 250 + 1.116 * fuel_humidity;									// kJ / kg
	static double sav = 1600;									// powierzchnia do objêtnoœci; jednostka 1/ft
	static double e = Math.exp(-138 / sav);										// efektywne ogrzewanie w przediale (0,1)
	
	static double density_piny = 460;							// kg / m^3
	static double density_oak = 760;
	//1. oak to d¹b
	//2. 85% gatunki iglaste
	// 15% liœciaste
	//3. chyba ¿e oak to iglaste u nas, a piny to liœciaste
	//%%static double percent_oak = 85;								// kuznia raciborska
	//static double percent_piny = 15;
	//conifer=drzewo iglaste, conifers drzewa iglaste
	//Hardwood=liœciate
	static double percent_oak = 15;	
	static double percent_piny = 85;
	
	/* Rothermel */
	static double r_0 = 4;			
	static double ip_0_oak = r_0 * density_oak * e * q_ig;		// Ip_0 = R_0 * mianownik w rothermelu
	static double ip_0_piny = r_0 * density_piny * e * q_ig;	// Ip_0 = R_0 * mianownik w rothermelu
	
	
	static Direction winddir = Direction.N;
	
	static void setHumidity(int h)
	{
		air_humidity = h;
	}
	static void setDirection(Direction d)
	{
		winddir = d;
	}
	static void setWind(double value)
	{
		wind = value;
	}
	static void setTerrain(int value)
	{
		terrain = value;
	}
	static void setFuel_humidity(int value)
	{
		fuel_humidity = value;
	}
	
	
	static double windValue(Direction d)
	{
		/*
		if(d == winddir) return wind;
		else
		{
			int diff = Math.abs(winddir.dir - d.dir);
			
			if(diff == 1 || diff == 7)
				return wind/2;
			
			if(diff == 2 || diff == 6)
				return 0;
			
			if(diff == 3 || diff == 5)
				return - wind / 2;
			
			if(diff == 4)
				return -wind;
		}*/
		return 0;
	}
	
	//zwraca wektor kierunkowy
	public static int[] getDirVector(int i){
		int [] tab=new int[2];
		tab[0]=dirVector[0][i];
		tab[1]=dirVector[1][i];
		return tab;
		}
	
}
