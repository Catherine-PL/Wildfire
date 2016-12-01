package simulation;

import simulation.data.Data;

/**
 * Responsible for calculating phenomenon in wildfire as spotting,
 * which occurs when trees are throwing fire bombs which ignite trees,
 * which are not in surrounding.
 */
public class Spotting {

    /**
     * Calculates distance for spotting fire
     * @param N parameter
     * @return spotting distance
     */
    public static double getSpottingDistance(int N) {
        double distance = 0;

        //zmienne do r�wna�
        double U = Data.wind;        //wind at treetop height  [m/s]

        double d = 55;                //diameter at breast height of three torching out
        double h = 20;                //height of burning tree [m]
        double h_ = 12;                //mean vegetation cover height downwind of source [m]
        int n = 1 + N;                    //number of trees burining simultanesously
        double dF = getDF(1, d, n);                //adjusted stedy flame duration [none]
        double hF = getHF(1, d, n);                //adjusted steady flame height [m]

        //za�o�enie h/hF<0.5,  dF<3.5
        double z_0 = getZ_0(dF, hF, hF);        //initial firebrand height m

        double h_c = 2.2 * Math.pow(z_0, 0.337) - 4;//minimium value of h_ m
        double hG = Math.max(h_, h_c);            //the greater of h_ and h_c

		/*Flat-terrain spotting distance is calculated next. Albini 's equation for
		flat-terrain spotting distance over forest-covered terrain is
		*/
        //[km]
        double F = 1.30 * 0.001 * U * Math.pow((hG), 0.5) * (0.362 + Math.pow((z_0 / hG), 0.5) * 0.5 * Math.log(z_0 / hG));//flat-terrain distance spotting

        if (Data.terrain <= 1.1) {
            distance = F;
        } else {
            double[] X = {0.0, 0.0, 0.0, 0.0, 0.0, 0};

			/*
			 * 0=mildslope,windward side
			 * 1=valley bootm
			 * 2=midslope, leeward side
			 * 3=ridgetop
			 */
            double M = 1;//code number for location of firebrand source

            double H = 0.3;//Ridge-to-valley elevational differance[multiples of 300m]
            double D = 0.5;//ridge-to-valley horizontal distance (map?)[km]

            double B = H / 10;
            double A = F / D;

            X[0] = A;

            for (int i = 1; i < 6; i++) {
                X[i] = A - B * (Math.cos(Math.PI * X[i - 1] - M * Math.PI) - Math.cos(M * Math.PI / 2));
            }
            distance = D * X[5];
        }

        return distance * 1000;//[m]
    }

    private static double getHF(int i, double d, int n) {
        switch (i) {
            case 1: {
                return 3.11 * Math.pow(d, 0.515) * Math.pow(n, 0.4);
            }
            case 2: {
                return 3.14 * Math.pow(d, 0.451) * Math.pow(n, 0.4);
            }
            case 3: {
                return 2.58 * Math.pow(d, 0.453) * Math.pow(n, 0.4);
            }
            default: {
                return 1;
            }
        }
    }

    private static double getDF(int i, double d, int n) {
        switch (i) {
            case 1: {
                return 16 * Math.pow(d, -0.256) * Math.pow(n, -0.2);
            }
            case 2: {
                return 13.9 * Math.pow(d, -0.278) * Math.pow(n, -0.2);
            }
            case 3: {
                return 7.95 * Math.pow(d, -0.249) * Math.pow(n, -0.2);
            }
            default: {
                return 1;
            }
        }
    }

    private static double getZ_0(double dF, double hF, double h) {
        double i = h / hF;
        if (i >= 1)
            return 4.24 * Math.pow(dF, 0.332) * hF + h / 2;
        if (i >= 0.5)
            return 3.64 * Math.pow(dF, 0.391) * hF + h / 2;
        if (dF < 3.5)
            return 2.78 * Math.pow(dF, 0.418) * hF + h / 2;
        if (dF >= 3.5)
            return 4.7 * hF + h / 2;

        return 0;
    }

}
