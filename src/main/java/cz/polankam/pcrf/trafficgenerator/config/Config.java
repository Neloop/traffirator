package cz.polankam.pcrf.trafficgenerator.config;


public class Config {

    private final int callCount;
    private final int initialScenariosCount;
    private final int threadCount;


    /**
     * Config constructor.
     * @param callCount number of repetitions of scenario; -1 if there is no limit
     * @param initialScenariosCount count of devices (instances of scenarios) which should be run at the beginning
     * @param threadCount number of thread on client
     * @throws java.lang.Exception
     */
    public Config(int callCount, int initialScenariosCount, int threadCount) throws Exception {
        this.callCount = callCount;
        this.initialScenariosCount = initialScenariosCount;
        this.threadCount = threadCount;

        if (callCount != -1 && callCount < initialScenariosCount) {
            throw new Exception("Call count of scenarios cannot be lesser than number of scenarios");
        }
    }


    /**
     * How many times scenarios should be run, -1 if there is no limit.
     * @return
     */
    public int getCallCount() {
        return callCount;
    }

    /**
     * Number of scenario executors which should be spawned by client.
     * @return
     */
    public int getInitialScenariosCount() {
        return initialScenariosCount;
    }

    /**
     * Get size of internal client thread-pool.
     * @return
     */
    public int getThreadCount() {
        return threadCount;
    }

}
