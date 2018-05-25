package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;


public class Config {

    private int threadCount;
    private String summary;
    private Timeouts timeouts;
    private List<ProfileItem> profile;
    private long end;


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
     * Configuration for sampling and logging timeouts.
     * @return
     */
    public Timeouts getTimeouts() {
        return timeouts;
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
