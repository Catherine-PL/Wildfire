package wildfire.simulation;

import java.util.HashMap;


import wildfire.simulation.Cell.Wood;

final class Data {
	
	public enum Direction
	{
		N(90), NE(45), E(0), SE(-45), S(-90), SW(-135), W(180), NW(135);
		
		int angle;
		
		Direction(int angle_from_E)
		{
			angle = angle_from_E; 
		}
	}
	//o coœ takiego chodi³o ci Sebastianie
//	<String,Integer> ja bêdzie lepiej 
//	private static HashMap<Direction,Integer> directionAzimuth=new HashMap<Direction,Integer>();
	private static int[][] dirVector={{0,1,1,1,0,-1,-1,-1},{1,1,0,-1,-1,-1,0,1}};//get w linii 110


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
	
	
	public static int[] getDirVector(int i){
		int [] tab=new int[2];
		tab[0]=dirVector[0][i];
		tab[1]=dirVector[1][i];
		return tab;
		}
	/*public void setDirAimuth(){
		directionAzimuth.put(Direction.N,0);
		directionAzimuth.put(Direction.NE, 45);
		directionAzimuth.put(Direction.E, 90);
		directionAzimuth.put(Direction.SE, 135);
		directionAzimuth.put(Direction.S, 180);
		directionAzimuth.put(Direction.SW, 225);
		directionAzimuth.put(Direction.W, 270);
		directionAzimuth.put(Direction.NW, 315);
	}

	public static int getDirAzimuth(Direction dir){
		return directionAzimuth.get(dir);
	}*/
}
