package simulation.model;

import simulation.FireSpread;
import simulation.data.Data;

import java.util.*;

/**
 * Cell class represents basic unit of Cellular Automata simulation
 *
 * @author Sebastian
 */
public class Cell {

    public Coordinate coordinates;
    public State state;
    public Wood type;
    public int burnthreshold;
    public int lifetime;                                    // ilosc minut palenia sie paliwa, mozna dodac pole np. ilosc ciepla dla komorki
    public int elevation;

    public long heightSea;                                    // jednostka m
    public double heightTree;                                    // jednostka m
    public int size = 1;                                    // bok kwadratu - ilosc metrow

    public Set<Cell> neighbors = new HashSet<>();

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

    public static void main(String[] args) {
        Cell c = new Cell(new Coordinate(2, 2), State.FUEL, Wood.OAK, 10, 500, 10, 1);

        System.out.println("Cell: x=2 y=2");
        System.out.println("-----------");
        System.out.println("E: " + FireSpread.elipse(0, c));
        System.out.println("W: " + FireSpread.elipse(180, c));
        System.out.println("N: " + FireSpread.elipse(Direction.N.getAngle(), c));
        System.out.println("S: " + FireSpread.elipse(Direction.S.getAngle(), c));
    }
}
