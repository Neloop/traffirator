package cz.polankam.pcrf.trafficgenerator.config;

/**
 * Class which serves as a holder for the whole configuration of the application.
 */
public class Config {

    private String description;
    private int threadCount;
    private String summary;
    private Statistics statistics;
    private Profile profile;


    /**
     * Human readable description which will be copied to summary file.
     * @return textual description of the provided configuration
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get size of the internal client thread-pool.
     * @return number of threads
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Set the size of the internal client thread-pool.
     * @param threadCount number of threads
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * File to which summary log should be written.
     * @return file path
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Set file to which summary log should be written.
     * @param summary file path
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Configuration for sampling and logging statistics.
     * @return holder of the statistics configuration
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Set the statistics configuration structure.
     * @param statistics holder of the statistics configuration
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Get testing profile configuration.
     * @return holder of the profile configuration
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Set the profile configuration structure.
     * @param profile holder of the profile configuration
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
