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
	
	static double wind = 1.4;
	static double terrain = 1.2;								// not sure
	static double fuel_humidity = 1.0;							
	static double q_ig = 250;									// kJ / kg
	static double sav = 1600;									// powierzchnia do objêtnoœci; jednostka 1/ft
	static double e = 1;										// efektywne ogrzewanie w przediale (0,1)
	static double density_piny = 460;							// kg / m^3
	static double density_oak = 760;
	static double percent_oak = 85;								// kuznia raciborska
	static double percent_piny = 15;
	static double ip_0 = 0;										// Ip_0 = R_0 * mianownik w rothermelu
	static double r_0 = 1;		
	
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
	static void calculateQ_ig()
	{
		q_ig = 250 + 1.116 * fuel_humidity;
	}
	static void calculateE()
	{
		e = Math.exp(-138 / sav);
	}
	static void calculateIp(Cell.Wood wood)
	{
		double density;
		if(wood == Wood.OAK)
			density = Data.density_oak;
		else
			density = Data.density_piny;
		
		ip_0 = r_0 * density * Data.e * Data.q_ig; 
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
