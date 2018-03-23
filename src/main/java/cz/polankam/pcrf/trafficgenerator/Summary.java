package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.ClientConfig;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Summary {

    private long start;
    private long end;
    private ClientConfig config;
    private List<Map.Entry<Long, Integer>> changes = new ArrayList<>();

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
        changes.add(new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), scenariosCount));
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
            for (Map.Entry<Long, Integer> entry : changes) {
                System.out.println("    " + entry.getKey() + "; " + entry.getValue());
            }
        }

        System.out.println("***** SUMMARY END *****");
    }

}
