package cz.polankam.pcrf.trafficgenerator.config;


public class Config {

    private String description;
    private int threadCount;
    private String summary;
    private Statistics statistics;
    private Profile profile;


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
     * Configuration for sampling and logging statistics.
     * @return
     */
    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Testing profile configuration.
     * @return
     */
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
