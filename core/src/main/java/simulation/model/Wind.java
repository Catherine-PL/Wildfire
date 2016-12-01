package simulation.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class which tries to reflect wind.
 */
public class Wind {
     Pair<Direction, Direction> directions;
     Pair<Double, Double> velocities;

    public Wind (Direction dir1, Direction dir2, Double vel1, Double vel2) {
        directions = Pair.of(dir1, dir2);
        velocities = Pair.of(vel1, vel2);
    }

    public void setDirections(Pair<Direction, Direction> directions) {
        this.directions = directions;
    }

    public void setVelocities(Pair<Double, Double> velocities) {
        this.velocities = velocities;
    }

    public Pair<Direction, Direction> getDirections() {
        return directions;
    }

    public Pair<Double, Double> getVelocities() {
        return velocities;
    }

    public double getCurrentVelocity() {
        if (velocities.getLeft() < velocities.getRight()) {
            return ThreadLocalRandom.current().nextDouble(velocities.getLeft(), velocities.getRight());
        } else if (velocities.getLeft() > velocities.getRight()) {
            return ThreadLocalRandom.current().nextDouble(velocities.getRight(), velocities.getLeft());
        } else return velocities.getLeft();

    }

    public Direction getCurrentDirection() {
        if(Math.random() < 0.5) {
            return directions.getLeft();
        } else {
            return directions.getRight();
        }
    }
}
