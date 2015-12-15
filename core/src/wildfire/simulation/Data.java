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

	
	static int lifetime = 5;

	
	static double wind = 4;										// 0 - 30, 30> huragan
	static double fuel_humidity = 1.0;		
	static double air_humidity = 1;								// dopisaæ przelicznik z air na fuel	

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
	static void setFuel_humidity(int value)
	{
		fuel_humidity = value;
	}
	
	
	
}
