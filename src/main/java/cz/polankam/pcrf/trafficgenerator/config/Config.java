package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;


public class Config {

    private int initialScenariosCount;
    private int threadCount;
    private String summary;
    private List<ProfileItem> profile;
    private long end;


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

    /**
     * File to which summary should be written.
     * @return
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Testing profile configuration.
     * @return
     */
    public List<ProfileItem> getProfile() {
        return profile;
    }

    /**
     * When will the execution ends, in milliseconds.
     * @return
     */
    public long getEnd() {
        return end;
    }

}
