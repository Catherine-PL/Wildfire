package wildfire.simulation;

final class Data {
	
	public enum Direction
	{
		N, NE, E, SE, S, SW, W, NW;
	}
	
	static final int x = 0;
	
	static double wind = 1.4;
	static double terrain = 1.2;		// not sure
	static double fuel_humidity = 1.0;
	static double q_ig = 250;
	static double sav = 1600;			// powierzchnia do objêtnoœci; jednostka 1/ft
	static double e = 1;
	static double density_piny = 460;			// kg / m^3
	static double density_oak = 760;
	static double percent_oak = 85;		// kuznia raciborska
	static double percent_piny = 15;
	
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

}
