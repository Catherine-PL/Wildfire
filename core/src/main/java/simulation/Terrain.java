package simulation;

import org.apache.commons.lang3.tuple.Pair;
import simulation.data.Data;
import simulation.data.DataTemplate;
import simulation.model.*;

import java.util.*;

/**
 * Represents map section as cells. Main class for a simulation.
 *
 * @author Sebastian
 */
public class Terrain {

    private Cell[][] terrainState;
    private static Set<Cell> treesOnFire = new HashSet<>();

    public static Set<Cell> treesOnFireAdd = new HashSet<>();
    public static Set<Cell> treesOnFireRemove = new HashSet<>();
    public static Random randomGenerator = new Random();
    public static int outBorderFire = 0;
    public static int spotingCount = 0;
    public int sizeX;
    public int sizeY;
    public int fuelCount = 0;

    /**
     * This is constructor that creates Terrain
     * @param _sizeX width of terrain
     * @param _sizeY height of terrain
     * @param probability probability of growing tree on cell
     * @param vegtype probability of tree being oak
     * @param relief maximum elevation between cels
     * @param roughness the higher, the bigger differences between heights of neighbour cells
     */
    public Terrain(int _sizeX, int _sizeY, int probability, int vegtype, int relief, int roughness) //probability i roguhness zakres 0-100
    {
        fuelCount = 0;
        sizeX = _sizeX;
        sizeY = _sizeY;
        terrainState = new Cell[sizeY][sizeX];
        int life = 20;
        int heightS = 500;
        int heightT = 10;

        for (int y = 0; y < sizeY; y++)
            for (int x = 0; x < sizeX; x++) {
                Wood wood;
                State s;
                if (probability > Terrain.randomGenerator.nextInt(100)) {
                    s = State.FUEL;
                    if (vegtype > Terrain.randomGenerator.nextInt(100)) {
                        wood = Wood.OAK;
                        life = Data.lifetime_oak;
                    } else {
                        wood = Wood.PINY;
                        life = Data.lifetime_piny;
                    }
                } else {
                    s = State.FREE;
                    wood = Wood.NONE;
                }

                terrainState[y][x] = new Cell(new Coordinate(y, x), s, wood, life, heightS, heightT, 1);

                if (terrainState[y][x].state == State.FUEL) {
                    fuelCount++;
                }
            }
        terrainState[sizeY / 2][sizeX / 2] = new Cell(new Coordinate(sizeY / 2, sizeX / 2), State.FUEL, Wood.OAK, life, heightS, heightT, 1);
        defineNeighbors(Data.winddir);
        generateElevation(relief);
        ignite();
    }

    /**
     * Create a terrain basing on DataTemplate class which store needed parameters.
     * Used generally during loading json file from file.
     * @param dt Parsed object from json file.
     * @return Terrain class which is set with data from DataTemplate.
     */
    public static Terrain create(DataTemplate dt) {
        dt.updateData();

        return new Terrain(dt.terrain.a, dt.terrain.b, Data.vegetation_probability, (int) (Data.percent_oak), dt.terrain.height, dt.terrain.roughness);
    }

    /**
     * Saves neighbours for each cell - based on eliptical shape
     * @param wind_direction direction of wind
     */
    private void defineNeighbors(Direction wind_direction) {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (terrainState[y][x].state == State.FUEL)        // dla ka¿dego drzewa, a nie dla kamieni
                {
                    HashMap<Integer, TreeSet<Integer>> neighbors = FireSpread.elipse(wind_direction.getAngle(), terrainState[y][x]);
                    for (Integer xn : neighbors.keySet()) {
                        for (Integer yn : neighbors.get(xn)) {
                            if (yn >= 0 && yn < sizeY && xn >= 0 && xn < sizeX)
                                terrainState[y][x].neighbors.add(terrainState[yn][xn]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts the fire: changes state of nine cells in the middle of terrain to Burning
     */
    public void ignite() {
        Cell choosenTree;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                choosenTree = terrainState[sizeY / 2 - i][sizeX / 2 - j];
                choosenTree.burnthreshold = 0;
                choosenTree.state = State.BURNING;
                treesOnFire.add(choosenTree);
            }
        }
    }

    /**
     * For each cell of terrain randomizes its height between zero and maximum height set by user
     * @param relief maximum height
     */
    public void generateElevation(int relief) {
        int probability = 0;
        for (int y = 0; y < sizeY; y++)
            for (int x = 0; x < sizeX; x++) {
                probability = randomGenerator.nextInt(100);
                if (probability < 10) terrainState[y][x].elevation = relief;
                else if (probability < 20) terrainState[y][x].elevation = relief / 5;
                else if (probability < 50) terrainState[y][x].elevation = relief / 3;
                else if (probability < 75) terrainState[y][x].elevation = relief / 2 + relief / 5;
                else if (probability < 90) terrainState[y][x].elevation = relief / 2 - relief / 5;
            }
    }

    /**
     * Spreading fire among trees. It invoke spreadFire() function from Cell class to ignite neighbors.
     */
    public void spreadFire() {

        for (Cell t : treesOnFire) {
            FireSpread.spreadFire(t);
        }
        for (Cell temp : treesOnFireAdd) {
            treesOnFire.add(temp);

        }
        treesOnFireAdd.clear();
        for (Cell temp : treesOnFireRemove) {
            treesOnFire.remove(temp);
        }
        treesOnFireRemove.clear();
        spreadSpotting(treesOnFire);
    }

    /**
     * Checks if there are no trees on fire
     * @return True if no trees on fire
     */
    public boolean isAllBurnt() {
        return treesOnFire.isEmpty();
    }

    /**
     * Getter for chosen cell from terrain
     * @param y y position of cell
     * @param x x position of cell
     * @return chosen Cell
     */
    public Cell getCell(int y, int x) {
        return terrainState[y][x];
    }

    @Override
    public String toString() {
        String txt = new String();
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                txt = txt + terrainState[y][x].state + ", ";
            }
            txt = txt + "\n";
        }
        return txt;
    }

    /**
     * Spreading spot fire among trees.
     * @param isOnFire set of trees on fire
     */
    public void spreadSpotting(Set<Cell> isOnFire) {
        int X = 0, Y = 0, N = 0, kX = 0, kY = 0, skalar = 0, nowyX, nowyY;
        double spottingDist = 0;
        Random ran = new Random();

        switch (Data.winddir) {
            case N: {
                X = 0;
                Y = -1;
                break;
            }
            case E: {
                X = 1;
                Y = 0;
                break;
            }
            case W: {
                X = -1;
                Y = 0;
                break;
            }
            case S: {
                X = 0;
                Y = 1;
                break;
            }
            case SW: {
                X = -1;
                Y = 1;
                break;
            }
            case SE: {
                X = 1;
                Y = 1;
                break;
            }
            case NE: {
                X = 1;
                Y = -1;
                break;
            }
            case NW: {
                X = -1;
                Y = -1;
                break;
            }
        }
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                Cell komorka = Terrain.this.getCell(j, i);
                if (komorka.state == State.BURNING) {
                    //komórka dla której liczê

                    kX = i;
                    kY = j;

                    nowyX = kX;
                    nowyY = kY;
                    //obliczenie iloœci pal¹cych siê drzew w okolicy 3x3
                    N = getNeighnoursOnFire(kX, kY) + 1;
                    //wyliczenie maksymalnego dystansu spotiingu z Albiniego
                    spottingDist = Spotting.getSpottingDistance(N);

                    //losowo wybiermay spotting distance z progiem górnym z Albiniego
                    skalar = ran.nextInt((int) spottingDist);

                    if (X != 0) nowyX = kX + X * skalar;
                    if (Y != 0) nowyY = kY + Y * skalar;
                    if (nowyY > 0 && nowyY < 100 && nowyX > 0 && nowyX < 100) {
                        if (Terrain.this.terrainState[nowyY][nowyX].state == State.FUEL) {
                            Terrain.this.terrainState[nowyY][nowyX].burnthreshold = 0;
                            Terrain.spotingCount++;
                        }
                    } else Terrain.outBorderFire++;
                }
            }
    }

    /**
     * Get numer of cell's neighbours on fire
     * @param x x position of cell
     * @param y y position of cell
     * @return number of neighbour cells on fire
     */
    private int getNeighnoursOnFire(int x, int y) {
        int n = 0;
        if (x + 1 < Terrain.this.sizeY && y + 1 < Terrain.this.sizeX && x > 1 && y > 1) {
            if (this.terrainState[x + 1][y + 1].state == State.BURNING) n = n + 1;
            if (this.terrainState[x][y + 1].state == State.BURNING) n = n + 1;
            if (this.terrainState[x + 1][y].state == State.BURNING) n = n + 1;
            if (this.terrainState[x - 1][y - 1].state == State.BURNING) n = n + 1;
            if (this.terrainState[x - 1][y].state == State.BURNING) n = n + 1;
            if (this.terrainState[x][y - 1].state == State.BURNING) n = n + 1;
            if (this.terrainState[x + 1][y - 1].state == State.BURNING) n = n + 1;
            if (this.terrainState[x - 1][y + 1].state == State.BURNING) n = n + 1;
        }
        return n;
    }

    /**
     * Getting percentage of alive trees from all trees on terrain
     * @return percentage of alive trees
     */
    public double getFuelAlivePercent() {
        return (double) (fuelCount - getFuelBurntCount()) / fuelCount * 100;
    }

    /**
     * Getting percentage of burnt trees from all trees on terrain
     * @return percentage of burnt trees
     */
    public double getFuelBurntPercent() {
        return (double) getFuelBurntCount() / fuelCount * 100;
    }

    /**
     * Counts burnt trees on terrain
     * @return amount of burnt trees
     */
    public int getFuelBurntCount() {
        int fuelBurnt = 0;
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < sizeY; j++) {
                if (Terrain.this.getCell(j, i).state == State.BURNT) {
                    fuelBurnt++;
                }
            }
        if (fuelBurnt > fuelCount) return fuelCount - 2;
        return fuelBurnt;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int size = 6;

        // testowanie
        Data.windInfo.setDirections(Pair.of(Direction.S, Direction.S));
        Terrain t = new Terrain(5, 100, 50, (int) (Data.percent_oak), 50, 60);
        System.out.println(t.terrainState[2][2]);
        System.out.println(FireSpread.elipse(Direction.S.getAngle(), t.terrainState[2][2]));

        while (!t.isAllBurnt()) {
            System.out.println("-------------------------");
            System.out.println(t.toString());

            t.spreadFire();
        }

        System.out.println("-----------------------------");

        System.out.println("Cell: x=2 y=2");
        System.out.println("-----------");
        System.out.println("E: " + FireSpread.elipse(0, t.terrainState[2][2]));
        System.out.println("W: " + FireSpread.elipse(180, t.terrainState[2][2]));
        System.out.println("N: " + FireSpread.elipse(Direction.N.getAngle(), t.terrainState[2][2]));
        System.out.println("S: " + FireSpread.elipse(Direction.S.getAngle(), t.terrainState[2][2]));
    }
}
