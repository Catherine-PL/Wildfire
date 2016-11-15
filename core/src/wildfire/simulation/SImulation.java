package wildfire.simulation;

/**
 * Created by USER on 2016-11-15.
 * simulation parameters
 */
public class Simulation
{
    public int simulationSpeed;
    private long simulationTime;
    private long simulationStart;

    public void startTimer(){
         simulationStart = System.nanoTime();
    }

    public void stopTimer(){
        simulationTime = (System.nanoTime() - simulationStart)/100000;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

}
