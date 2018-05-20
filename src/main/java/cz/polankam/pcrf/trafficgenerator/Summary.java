package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.config.Config;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Summary {

    private long start;
    private long end;
    private Config config;
    private List<Map.Entry<Long, Integer>> changes = new ArrayList<>();

    public void setStart() {
        start = System.currentTimeMillis();
    }

    public void setEnd() {
        end = System.currentTimeMillis();
    }

    public void setClientConfig(Config config) {
        this.config = config;
    }

    public synchronized void addChange(int scenariosCount) {
        changes.add(new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), scenariosCount));
    }

    public void printSummary(PrintStream out) {
        out.println("***** SUMMARY START *****");
        out.println("Start: " + start);
        out.println("End: " + end);

        out.println("Initial Setup: ");
        out.println("    Thread Count: " + config.getThreadCount());
        out.println("    End: " + config.getEnd());

        if (!changes.isEmpty()) {
            out.println("Changes (Time; Scenarios Count):");
            for (Map.Entry<Long, Integer> entry : changes) {
                out.println("    " + entry.getKey() + "; " + entry.getValue());
            }
        }

        out.println("***** SUMMARY END *****");
        out.flush();
    }

}
