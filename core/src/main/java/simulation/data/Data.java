package simulation.data;

import simulation.model.Direction;
import simulation.model.Wind;

/**
 * Aggregation of all needed parameters for wildfire simulation.
 *
 * @author Sebastian
 */
final public class Data {

    public static double sigma = Data.sav;
    public static double betaOp = 3.348 * Math.pow(sigma, -0.8189);
    public static double C = 7.47 * Math.exp((-0.133 * Math.pow(sigma, 0.55)));
    public static double B = 0.02526 * Math.pow(sigma, 0.54);
    public static double E = 0.715 * Math.exp((-3.59 * 0.0001 * sigma));

    public static int lifetime = 5;

    public static Wind windInfo = new Wind(Direction.N,Direction.N,Double.parseDouble("4"),Double.parseDouble("4"));
    public static double wind = windInfo.getCurrentVelocity();  // 0 - 30, 30> huragan
    public static double terrain = 1;
    public static double fuel_humidity = 1.0;
    public static double air_humidity = 15;

    public static int lifetime_oak = 15;
    public static int lifetime_piny = 6;
    public static int toburn_oak = 18 + (int) air_humidity / 5;
    public static int toburn_piny = 8 + (int) air_humidity / 10;                //ile ciepla trzeba dostarczyc zeby sie zapalilo (na podstawie gestosci i artykulow o drzewach)
    public static double q_ig = 250 + 1.116 * fuel_humidity;                                    // kJ / kg
    public static double sav = 1600;                                    // powierzchnia do obj�tno�ci; jednostka 1/ft
    public static double e = Math.exp(-138 / sav);                                        // efektywne ogrzewanie w przediale (0,1)

    public static double density_piny = 460;                            // kg / m^3
    public static double density_oak = 760;

    public static double percent_oak = 15;
    public static double percent_piny = 85;

    public static int vegetation_probability = 50;

    public static double soil = 0; // 0 if dry, +150 if fertile, -200 if swampy, +50 if normal

    /* Rothermel */
    public static double r_0 = 4;
    public static double ip_0_oak = r_0 * (density_oak + soil) * e * q_ig;        // Ip_0 = R_0 * mianownik w rothermelu
    public static double ip_0_piny = r_0 * (density_piny + soil) * e * q_ig;    // Ip_0 = R_0 * mianownik w rothermelu

    public static Direction winddir = windInfo.getCurrentDirection();

    public static void updateWind(){
        Data.wind = windInfo.getCurrentVelocity();
        Data.winddir = windInfo.getCurrentDirection();
    }
}
