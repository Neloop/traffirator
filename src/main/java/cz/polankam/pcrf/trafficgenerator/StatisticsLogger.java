package cz.polankam.pcrf.trafficgenerator;

import com.sun.management.OperatingSystemMXBean;
import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Statistics;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;

/**
 * Logs the statistics information to the given file. Log method should be sampled with period given in
 * the configuration. This class does not ensure the execution of the logging, this has to be provided from outside.
 * To the log file following information are written - current time, count of scenarios, count of timeouts, count of
 * sent messages, count of received messages, count of failures and current process load. All counts are relative to
 * the previous sampled data.
 */
public class StatisticsLogger {

    private final Client client;
    private final Statistics config;
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private PrintStream logFile;
    /** Client is returning timeouts in absolute numbers, this is cache for last count. */
    private long previousTimeouts = 0;
    /** Client is returning sent messages in absolute numbers, this is cache for last count. */
    private long previousSent = 0;
    /** Client is returning received messages in absolute numbers, this is cache for last count. */
    private long previousReceived = 0;
    /** Client is returning failures in absolute numbers, this is cache for last count. */
    private long previousFailures = 0;

    /**
     * Constructor.
     * @param client that is where the information is taken from
     * @param config configuration of statistics logger
     */
    public StatisticsLogger(Client client, Statistics config) {
        this.client = client;
        this.config = config;
    }


    /**
     * Initialize the log file, open it and write a header.
     * @throws FileNotFoundException in case of error
     */
    public void init() throws FileNotFoundException {
        logFile = new PrintStream(config.getLogFile());
        logFile.print("Time");
        logFile.print("\t");
        logFile.print("ScenariosCount");
        logFile.print("\t");
        logFile.print("TimeoutsCount");
        logFile.print("\t");
        logFile.print("SentCount");
        logFile.print("\t");
        logFile.print("ReceivedCount");
        logFile.print("\t");
        logFile.print("FailuresCount");
        logFile.print("\t");
        logFile.print("ProcessLoad [%]");
        logFile.println();
    }

    /**
     * Log the information taken from the <code>Client</code> class and write it into the statistics log file.
     */
    public synchronized void log() {
        long timeoutsCount = client.getTimeoutsCount();
        long sentCount = client.getSentCount();
        long receivedCount = client.getReceivedCount();
        long failuresCount = client.getFailuresCount();

        logFile.print(System.currentTimeMillis());
        logFile.print("\t");
        logFile.print(client.getScenariosCount());
        logFile.print("\t");
        logFile.print(timeoutsCount - previousTimeouts);
        logFile.print("\t");
        logFile.print(sentCount - previousSent);
        logFile.print("\t");
        logFile.print(receivedCount - previousReceived);
        logFile.print("\t");
        logFile.print(failuresCount - previousFailures);
        logFile.print("\t");
        logFile.printf("%.2f", osBean.getProcessCpuLoad() * 100);
        logFile.println();

        previousTimeouts = timeoutsCount;
        previousSent = sentCount;
        previousReceived = receivedCount;
        previousFailures = failuresCount;
    }

    /**
     * Properly close the statistics log file.
     */
    public void close() {
        logFile.flush();
        logFile.close();
    }

}
