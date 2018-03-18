package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.ClientConfig;
import java.util.HashMap;
import java.util.Map;


public class Summary {

    private long start;
    private long end;
    private ClientConfig config;
    private Map<Long, Integer> changes = new HashMap<>();

    public void setStart() {
        start = System.currentTimeMillis();
    }

    public void setEnd() {
        end = System.currentTimeMillis();
    }

    public void setClientConfig(ClientConfig config) {
        this.config = config;
    }

    public synchronized void addChange(int scenariosCount) {
        changes.put(System.currentTimeMillis(), scenariosCount);
    }

    public void printSummary() {
        System.out.println("***** SUMMARY START *****");
        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        System.out.println("Initial Setup: ");
        System.out.println("    Call Count: " + config.getCallCount());
        System.out.println("    Initial Scenarios Count: " + config.getInitialScenariosCount());
        System.out.println("    Thread Count: " + config.getThreadCount());

        if (!changes.isEmpty()) {
            System.out.println("Changes (Time; Scenarios Count):");
            for (Map.Entry<Long, Integer> entry : changes.entrySet()) {
                System.out.println("    " + entry.getKey() + "; " + entry.getValue());
            }
        }

        System.out.println("***** SUMMARY END *****");
    }

}
