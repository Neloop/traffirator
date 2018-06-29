package cz.polankam.pcrf.trafficgenerator.config;

/**
 * Holds information about statistics item from the configuration file.
 */
public class Statistics {

    private String logFile;
    private long samplingPeriod;


    /**
     * File where sampled data counts will be written.
     * @return file path
     */
    public String getLogFile() {
        return logFile;
    }

    /**
     * Set log file where sampled data should be written.
     * @param logFile file path
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    /**
     * Period of sampling for statistics logging, in milliseconds.
     * @return sampling period in milliseconds
     */
    public long getSamplingPeriod() {
        return samplingPeriod;
    }

    /**
     * Set sampling period for statistics logging.
     * @param samplingPeriod sampling period in milliseconds
     */
    public void setSamplingPeriod(long samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }
}
