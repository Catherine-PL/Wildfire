package simulation;

import simulation.data.Data;
import simulation.model.Wood;

/**
 * Created by Sebastian on 2016-11-30.
 */
public class Rothermel {

    /**
     * Calculates velocity of head of the fire, based on Rothermel model
     * @return velocity of the head of fire
     */
    public static double rothermel(Wood type, double U)
    {
        double density;
        double ip_0;

        if (type == Wood.OAK) {
            density = Data.density_oak;
            ip_0 = Data.ip_0_oak;
        } else {
            density = Data.density_piny;
            ip_0 = Data.ip_0_oak;
        }
        double betaBeforeOp = 2;
        double wind = Data.C * Math.pow(U, Data.B) * Math.pow((betaBeforeOp), -Data.E);
        double terrain = 0;
        double numerator = ip_0 * (1 + wind + terrain);
        double denominator = density * Data.e * Data.q_ig;

        return numerator / denominator;
    }
}
