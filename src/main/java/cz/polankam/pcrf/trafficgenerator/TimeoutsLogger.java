package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Timeouts;

import java.io.FileNotFoundException;
import java.io.PrintStream;


public class TimeoutsLogger {

    private final Client client;
    private final Timeouts config;
    private PrintStream logFile;
    /** Client is returning timeouts in absolute number, this is cache for last count. */
    private long previousCount = 0;

    public TimeoutsLogger(Client client, Timeouts config) {
        this.client = client;
        this.config = config;
    }


    public void init() throws FileNotFoundException {
        logFile = new PrintStream(config.getLogFile());
        logFile.print("Time");
        logFile.print("\t");
        logFile.print("TimeoutsCount");
        logFile.println();
    }

    public synchronized void log() {
        long newCount = client.getTimeoutsCount();

        logFile.print(System.currentTimeMillis());
        logFile.print("\t");
        logFile.print(newCount - previousCount);
        logFile.println();

        previousCount = newCount;
    }

    public void close() {
        logFile.flush();
        logFile.close();
    }

}
