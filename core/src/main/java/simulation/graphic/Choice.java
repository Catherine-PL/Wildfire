package simulation.graphic;

/**
 * Available options in GUI
 */
public enum Choice {

    GENERATE(10), T_AREA(0), T_ROUGHNESS(1), T_MAXIMUM_HEIGHT(2), W_VELOCITY(3), W_DIRECTION(4), W_HUMIDITY(5), NONE(12), GENERATE_EXAMPLE(10), FINISHED(11),
    T_AREA2(6),W_DIRECTION2(7), W_VELOCITY2(8), PATH(9), GENERATE_FILE(10);
    private final int value;

    Choice(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
