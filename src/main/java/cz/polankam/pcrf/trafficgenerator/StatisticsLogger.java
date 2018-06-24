package cz.polankam.pcrf.trafficgenerator;

import com.sun.management.OperatingSystemMXBean;
import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Statistics;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;


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

    public StatisticsLogger(Client client, Statistics config) {
        this.client = client;
        this.config = config;
    }


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
        logFile.print("ProcessLoad [%]");
        logFile.println();
    }

    public synchronized void log() {
        long timeoutsCount = client.getTimeoutsCount();
        long sentCount = client.getSentCount();
        long receivedCount = client.getReceivedCount();

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
        logFile.printf("%.2f", osBean.getProcessCpuLoad() * 100);
        logFile.println();

        previousTimeouts = timeoutsCount;
        previousSent = sentCount;
        previousReceived = receivedCount;
    }

    public void close() {
        logFile.flush();
        logFile.close();
    }

}
