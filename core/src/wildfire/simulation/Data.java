package wildfire.simulation;

/**
 * Aggregation of all needed parameters for wildfire simulation.
 * @author Sebastian
 *
 */
final public class Data {
	
	public enum Direction
	{
		N(90), NE(45), E(0), SE(-45), S(270), SW(-135), W(180), NW(135);
		
		int angle;
		
		Direction(int angle_from_E)
		{
			angle = angle_from_E; 
		}
	}
//
	static double sigma=Data.sav;
	static double betaOp=3.348*Math.pow(sigma,-0.8189);
	static double C=7.47*Math.exp((-0.133*Math.pow(sigma,0.55)));
	static double B=0.02526*Math.pow(sigma,0.54);
	static double E=0.715*Math.exp((-3.59*0.0001*sigma));
//	

	
	static int lifetime = 5;
	
	static double wind = 4;										// 0 - 30, 30> huragan
	static double terrain = 1;
	static double fuel_humidity = 1.0;		
	static double air_humidity = 15;								

	static int lifetime_oak =15;
	static int lifetime_piny =6;
	static int toburn_oak =18+ (int)air_humidity/5 ;
	static int toburn_piny =8+ (int)air_humidity/10;				//ile ciepla trzeba dostarczyc zeby sie zapalilo (na podstawie gestosci i artykulow o drzewach)
	static double q_ig = 250 + 1.116 * fuel_humidity;									// kJ / kg
	static double sav = 1600;									// powierzchnia do obj�tno�ci; jednostka 1/ft
	static double e = Math.exp(-138 / sav);										// efektywne ogrzewanie w przediale (0,1)
	
	static double density_piny = 460;							// kg / m^3
	static double density_oak = 760;

	static double percent_oak = 15;
	static double percent_piny = 85;

	static int vegetation_probability = 50;

	static double soil = 0; // 0 if dry, +150 if fertile, -200 if swampy, +50 if normal
	
	/* Rothermel */
	static double r_0 = 4;			
	static double ip_0_oak = r_0 * (density_oak + soil) * e * q_ig;		// Ip_0 = R_0 * mianownik w rothermelu
	static double ip_0_piny = r_0 * (density_piny + soil) * e * q_ig;	// Ip_0 = R_0 * mianownik w rothermelu
	
	
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
