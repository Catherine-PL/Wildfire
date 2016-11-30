package simulation;

import simulation.data.Data;
import simulation.model.Cell;
import simulation.model.State;
import simulation.model.Wood;

import java.util.*;

/**
 * Created by Sebastian on 2016-11-30.
 */
public class FireSpread {

    private static double U =  Data.wind;
    /* Elispe factors */
    private static double a;                                            // Fala Huygensa
    private static double b;                                            // Na ich bazie robimy sasiedztwo
    private static double c;                                            // odleglosc od punktu zaplonu do srodka elipsy

    private static double lb() {
        double U = Data.wind;
        return 0.936 * Math.pow(Math.E, 0.2566 * U) + 0.461 * Math.pow(Math.E, -0.1548 * U) - 0.397;
    }

    private static double hb() {
        double p = Math.pow(Math.pow(lb(), 2) - 1, 0.5);
        return (lb() + p) / (lb() - p);
    }

    private static void factors(Wood type) {
        double r = Rothermel.rothermel(type, U);
        a = 0.5 * (r + (r / hb())) / lb();
        b = (r + (r / hb())) / 2.0;
        c = b - (r / hb());
    }


    /**
     * Calculates nieghbors in a shape of elipse which is related with rothermel without any restrictions.
     *
     * @param angle Angle of wind from East direction.
     * @return All neighbors to burn in HashMap where key is Integer x and value is a HashSet of Integers y
     */
    public static HashMap<Integer, TreeSet<Integer>> elipse(double angle, Cell cell) {
        factors(cell.type);

        HashMap<Integer, TreeSet<Integer>> elipse = new HashMap<>();
        HashMap<Integer, TreeSet<Integer>> result = new HashMap<>();

        double step = Math.PI / 180;
        double radian = angle * step;
        double e2 = ((b * b) - (a * a)) / (b * b);

		/* Stworzenie elipsy wraz z obrotem */
        for (double i = 0; i < (2 * Math.PI); i = i + step) {
            double r = Math.sqrt((a * a) / (1 - e2 * Math.cos(i - radian) * Math.cos(i - radian)));

            int x = (int) Math.round(r * Math.cos(i));
            int y = (int) Math.round(r * Math.sin(i));

            if (!elipse.containsKey(x))
                elipse.put(x, new TreeSet<Integer>());

            elipse.get(x).add(y);
        }

		/*	wypelnienie srodka */
        Set<Integer> keys = new HashSet<>(elipse.keySet());
        Iterator<Integer> it = keys.iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            TreeSet<Integer> set = elipse.get(key);

            Integer max = set.last();
            Integer min = set.first();

            for (int i = min + 1; i < max; i++) {
                set.add(i);
            }
            elipse.put(key, set);
        }

		/* obrot miejsca zaplonu */
        int xz = (int) Math.round(c * Math.cos(Math.PI + radian));
        int yz = (int) Math.round(c * Math.sin(Math.PI + radian));

        elipse.get(xz).remove(yz);                                        // usuniecie miejsca zaplonu z sasiadow

		/* Przesuniecie elipsy na wspolrzedne komorki */
        for (Integer x : elipse.keySet()) {
            int xn = cell.coordinates.getX() + x - xz;
            result.put(xn, new TreeSet<Integer>());
            for (Integer y : elipse.get(x)) {
                result.get(xn).add(cell.coordinates.getY() - (y - yz));            // bo o� y ro�nie w d�
            }
        }

        return result;
    }

    /**
     * Spreading fire means ignite all neighbors and decrease lifetime.
     * It checks lifetime and type of neighbors.
     */
    public static void spreadFire(Cell cell) {
        cell.lifetime--;
        if (cell.lifetime == 0) {
            cell.state = State.BURNT;
            Terrain.treesOnFireRemove.add(cell);
        }
        for (Cell c : cell.neighbors) {
            if (c.state == State.FUEL) {
                if (c.burnthreshold == 0) {
                    c.state = State.BURNING;
                    Terrain.treesOnFireAdd.add(c);
                }
                if (c.burnthreshold > 0) {
                    c.burnthreshold = c.burnthreshold - 1;
                }
            }
        }
    }
}
