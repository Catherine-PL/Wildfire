package wildfire.simulation;

import wildfire.simulation.Cell.Wood;

final class Data {
	
	public enum Direction
	{
		N(1), NE(2), E(3), SE(4), S(5), SW(6), W(7), NW(8);
		
		int dir;
		
		Direction(int d)
		{
			dir = d; 
		}
	}
	
	static final int x = 0;
	
	static double wind = 5;										// 0 - 30, 30> huragan
	static double terrain = 1.2;								// not sure
	static double fuel_humidity = 1.0;							
	static double q_ig = 250 + 1.116 * fuel_humidity;									// kJ / kg
	static double sav = 1600;									// powierzchnia do objêtnoœci; jednostka 1/ft
	static double e = Math.exp(-138 / sav);										// efektywne ogrzewanie w przediale (0,1)
	
	static double density_piny = 460;							// kg / m^3
	static double density_oak = 760;
	static double percent_oak = 85;								// kuznia raciborska
	static double percent_piny = 15;
	
	static double r_0 = 4;		
	
	static double ip_0_oak = r_0 * density_oak * e * q_ig;		// Ip_0 = R_0 * mianownik w rothermelu
	static double ip_0_piny = r_0 * density_piny * e * q_ig;	// Ip_0 = R_0 * mianownik w rothermelu
	
	
	static Direction winddir = Direction.N;
	
	static void setDirection(Direction d)
	{
		winddir = d;
	}
	static void setWind(int value)
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
		}
		return 0;
	}
}
