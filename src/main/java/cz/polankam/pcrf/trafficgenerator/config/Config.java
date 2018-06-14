package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;


public class Config {

    private String description;
    private int threadCount;
    private String summary;
    private Timeouts timeouts;
    private List<ProfileItem> profile;
    private long end;


    /**
     * Human readable description which might be copied to summary file.
     * @return
     */
    public String getDescription() {
        return description;
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

    /**
     * Configuration for sampling and logging timeouts.
     * @return
     */
    public Timeouts getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(Timeouts timeouts) {
        this.timeouts = timeouts;
    }

    /**
     * Testing profile configuration.
     * @return
     */
    public List<ProfileItem> getProfile() {
        return profile;
    }

    public void setProfile(List<ProfileItem> profile) {
        this.profile = profile;
    }

    /**
     * When will the execution ends, in milliseconds.
     * @return
     */
    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
