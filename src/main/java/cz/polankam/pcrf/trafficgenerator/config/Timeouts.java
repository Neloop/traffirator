package cz.polankam.pcrf.trafficgenerator.config;


public class Timeouts {

    private String logFile;
    private long samplingPeriod;


    /**
     * File where sampled timeouts counts will be written.
     * @return
     */
    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    /**
     * Period of sampling for timeouts logging, in milliseconds.
     * @return
     */
    public long getSamplingPeriod() {
        return samplingPeriod;
    }

    public void setSamplingPeriod(long samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }
}
