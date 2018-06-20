package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Statistics;

import java.io.FileNotFoundException;
import java.io.PrintStream;


public class StatisticsLogger {

    private final Client client;
    private final Statistics config;
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
