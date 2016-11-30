package simulation.model;

/**
 * Available wind directions and their translations to angles
 */
public enum Direction {

    N(90), NE(45), E(0), SE(-45), S(270), SW(-135), W(180), NW(135);

    int angle;

    Direction(int angle_from_E) {
        angle = angle_from_E;
    }

    public int getAngle() {
        return angle;
    }
}
