package simulation;

/**
 * Simulation contains general simulation parameters (time, speed)
 *
 * @author Katarzyna
 */
public class Simulation {
    private int simulationSpeed;
    private long simulationTime;
    private long simulationStart;
    private int speedCounter;

    /**
     * Starts counting time of simulation
     */
    public void startTimer() {
        simulationStart = System.nanoTime();
    }

    /**
     * Stops counting time of simulation
     */
    public void stopTimer() {
        simulationTime = (System.nanoTime() - simulationStart) / 100000;
    }

    /**
     * Get total simulation time
     * @return simulation time in seconds
     */
    public long getSimulationTime() {
        return simulationTime;
    }

    /**
     * Simulation speed setter
     * @param speed speed of simulation
     */
    public void setSimulationSpeed(int speed) {
        simulationSpeed = speed;
    }

    /**
     * Simulation speed getter
     * @return simulation speed
     */
    public int getSimulationSpeed() {
        return simulationSpeed;
    }

    /**
     * Checks if next step can be performed according to simulation speed
     * @return True if next step can be performed
     */
    public boolean readyNextStep() {
        if ((speedCounter >= 5 * simulationSpeed)) {
            speedCounter = 0;
            return true;
        } else {
            speedCounter++;
            return false;
        }
    }
}
