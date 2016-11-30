package simulation.model;

import simulation.data.Data;

import java.util.*;

/**
 * Cell class represents basic unit of Cellular Automata simulation
 *
 * @author Sebastian
 */
public class Cell {

    private Coordinate coordinates;
    private int elevation;
    private State state;
    private Wood type;
    private int burnthreshold;
    private double U =  Data.wind;
    private int lifetime;                                    // ilosc minut palenia si� paliwa, mo�na doda� pole np. ilo�� ciep�a dla kom�rki
    private long heightSea;                                    // jednostka m
    private double heightTree;                                    // jednostka m
    private int size = 1;                                    // bok kwadratu - ilosc metrow

    /* Elispe factors */
    private double a;                                            // Fala Huygensa
    private double b;                                            // Na ich bazie robimy sasiedztwo
    private double c;                                            // odleglosc od punktu zaplonu do srodka elipsy

    Set<Cell> neighbors = new HashSet<Cell>();

    /**
     * Cell constructor
     * @param yx Coordinates of cell
     * @param state State of cell
     * @param type type of Wood if cell is in FUEL state
     * @param lifetime fuel's resitence to fire (in iterations)
     * @param heightSea lowest height of terrain
     * @param heightTree height of tree
     * @param size size of fuel
     */
    public Cell(Coordinate yx, State state, Wood type, int lifetime, long heightSea, double heightTree, int size) {
        this.coordinates = yx;
        this.state = state;
        this.type = type;
        this.lifetime = lifetime;
        this.heightSea = heightSea;
        this.heightTree = heightTree;
        this.size = size;

        if (type == Wood.OAK)
            this.burnthreshold = Data.toburn_oak;
        if (type == Wood.PINY)
            this.burnthreshold = Data.toburn_piny;
    }

    /**
     * Cell constructor
     * @param value threshold for reacting to fire nearby (how quickly fuel will catch fire)
     */
    public void setBurnThreshold(int value) {
        burnthreshold = value;
    }

    /**
     * Calculates velocity of head of the fire, based on Rothermel model
     * @return velocity of the head of fire
     */
    private double rothermel()
    {

        double density;
        double ip_0 = 0;

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

    private double lb() {
        double U = Data.wind;
        return 0.936 * Math.pow(Math.E, 0.2566 * U) + 0.461 * Math.pow(Math.E, -0.1548 * U) - 0.397;
    }

    private double hb() {
        double p = Math.pow(Math.pow(lb(), 2) - 1, 0.5);
        return (lb() + p) / (lb() - p);
    }

    private void factors() {
        double r = rothermel();
        this.a = 0.5 * (r + (r / hb())) / lb();
        this.b = (r + (r / hb())) / 2.0;
        this.c = this.b - (r / hb());
    }

    public void setElevation(int e) {
        elevation = e;
    }

    public void setState(State s) {
        state = s;
    }

    public int getElevation() {
        return elevation;
    }

    public State getState() {
        return state;
    }

    public Wood getType() {
        return type;
    }

    public Coordinate getCoordinates() {
        return this.coordinates;
    }

    /**
     * Calculates nieghbors in a shape of elipse which is related with rothermel without any restrictions.
     *
     * @param angle Angle of wind from East direction.
     * @return All neighbors to burn in HashMap where key is Integer x and value is a HashSet of Integers y
     */
    public HashMap<Integer, TreeSet<Integer>> elipse(double angle) {
        this.factors();

        HashMap<Integer, TreeSet<Integer>> elipse = new HashMap<Integer, TreeSet<Integer>>();
        HashMap<Integer, TreeSet<Integer>> result = new HashMap<Integer, TreeSet<Integer>>();

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
        Set<Integer> keys = new HashSet<Integer>(elipse.keySet());
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

		/* obr�t miejsca zap�onu */
        int xz = (int) Math.round(c * Math.cos(Math.PI + radian));
        int yz = (int) Math.round(c * Math.sin(Math.PI + radian));

        elipse.get(xz).remove(yz);                                        // usuniecie miejsca zaplonu z sasiadow

		/* Przesuniecie elipsy na wsp�rz�dne kom�rki */
        for (Integer x : elipse.keySet()) {
            int xn = coordinates.getX() + x - xz;
            result.put(xn, new TreeSet<Integer>());
            for (Integer y : elipse.get(x)) {
                result.get(xn).add(coordinates.getY() - (y - yz));            // bo o� y ro�nie w d�
            }
        }
        return result;
    }

    /**
     * Adding neighbor to cell
     *
     * @param n A cell from terrain which should be consider as neighbor
     */
    public void addNeighbor(Cell n) {
        neighbors.add(n);
    }

    /**
     * Spreading fire means ignite all neighbors and decrease lifetime.
     * It checks lifetime and type of neighbors.
     */
    public void spreadFire() {
        lifetime--;
        if (lifetime == 0) {
            state = State.BURNT;
            Terrain.treesOnFireRemove.add(this);
        }
        for (Cell c : neighbors) {
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

    public String toString() {
        return (this.coordinates.toString());
    }

    /**
     * Calculates distance for spotting fire
     * @param N parameter
     * @return spotting distance
     */
    public double getSpottingDistance(int N) {
        double distance = 0;

        //zmienne do r�wna�
        double U = Data.wind;        //wind at treetop height  [m/s]

        double d = 55;                //diameter at breast height of three torching out
        double h = 20;                //height of burning tree [m]
        double h_ = 12;                //mean vegetation cover height downwind of source [m]
        int n = 1 + N;                    //number of trees burining simultanesously
        double dF = this.getDF(1, d, n);                //adjusted stedy flame duration [none]
        double hF = this.getHF(1, d, n);                //adjusted steady flame height [m]

        //za�o�enie h/hF<0.5,  dF<3.5
        double z_0 = this.getZ_0(dF, hF, hF);        //initial firebrand height m

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

    private double getHF(int i, double d, int n) {
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

    private double getDF(int i, double d, int n) {
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

    private double getZ_0(double dF, double hF, double h) {
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

    void setBurnthresholdToZero() {
        this.burnthreshold = 0;
    }

    public static void main(String[] args) {
        Cell c = new Cell(new Coordinate(2, 2), State.FUEL, Wood.OAK, 10, 500, 10, 1);
        c.factors();

        System.out.println("Cell: x=2 y=2");
        System.out.println("-----------");
        System.out.println("E: " + c.elipse(0));
        System.out.println("W: " + c.elipse(180));
        System.out.println("N: " + c.elipse(Direction.N.getAngle()));
        System.out.println("S: " + c.elipse(Direction.S.getAngle()));


    }
}
