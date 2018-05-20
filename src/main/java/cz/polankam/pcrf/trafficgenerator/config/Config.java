package cz.polankam.pcrf.trafficgenerator.config;


public class Config {

    private int callCount;
    private int initialScenariosCount;
    private int threadCount;
    private String summary;


    /**
     * How many times scenarios should be run, -1 if there is no limit.
     * @return
     */
    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    /**
     * Number of scenario executors which should be spawned by client.
     * @return
     */
    public int getInitialScenariosCount() {
        return initialScenariosCount;
    }

    public void setInitialScenariosCount(int initialScenariosCount) {
        this.initialScenariosCount = initialScenariosCount;
    }

    /**
     * Get size of internal client thread-pool.
     * @return
     */
    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * File to which summary should be written.
     * @return
     */
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }



}
