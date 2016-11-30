package simulation;

import java.util.Random;

/**
 * Created by Sebastian on 2016-11-30.
 */
public class Node {

    static Random randomGenerator = new Random();
    boolean isTree = false;
    boolean isBurning = false;
    boolean burnt = false;
    boolean burningSinceThisStep = false;

    Node(int _probability) {
        if (generateRandom() < _probability) {
            isTree = true;
        }
    }

    int generateRandom() {
        return randomGenerator.nextInt(100);

    }
}
