package wildfire.simulation;

/**
 * Created by USER on 2016-11-15.
 * simulation parameters
 */
public class Simulation
{
    private int simulationSpeed;
    private long simulationTime;
    private long simulationStart;
    private int speedCounter;

    public void startTimer(){
         simulationStart = System.nanoTime();
    }

    public void stopTimer(){
        simulationTime = (System.nanoTime() - simulationStart)/100000;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationSpeed (int speed) {
        simulationSpeed = speed;
    }

    public int getSimulationSpeed () {
        return simulationSpeed;
    }

    public boolean readyNextStep() {
        if ((speedCounter >= 5*simulationSpeed)) {
            speedCounter = 0;
            return true;
        } else {
            speedCounter++;
            return false;
        }
    }
}
