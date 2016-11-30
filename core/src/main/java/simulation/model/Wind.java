package simulation.model;

import simulation.data.Data.Direction;
import simulation.util.Pair;

import java.util.concurrent.ThreadLocalRandom;

public class Wind {
     Pair<Direction> directions;
     Pair<Double> velocities;

    public Wind (Direction dir1, Direction dir2, Double vel1, Double vel2) {
        directions = new Pair<Direction>(dir1, dir2);
        velocities = new Pair<Double>(vel1, vel2);
    }

    public void setDirections(Pair<Direction> directions) {
        this.directions = directions;
    }

    public void setVelocities(Pair<Double> velocities) {
        this.velocities = velocities;
    }

    public double getCurrentVelocity() {
        if (velocities.getFirst() < velocities.getSecond()) {
            return ThreadLocalRandom.current().nextDouble(velocities.getFirst(), velocities.getSecond());
        } else if (velocities.getFirst() > velocities.getSecond()) {
            return ThreadLocalRandom.current().nextDouble(velocities.getSecond(), velocities.getFirst());
        } else return velocities.getFirst();

    }

    public Direction getCurrentDirection() {
        if(Math.random() < 0.5) {
            return directions.getFirst();
        } else {
            return directions.getSecond();
        }
    }
}