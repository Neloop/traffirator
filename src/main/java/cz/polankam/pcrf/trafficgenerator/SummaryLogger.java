package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.config.Config;
import cz.polankam.pcrf.trafficgenerator.config.ProfileItem;
import cz.polankam.pcrf.trafficgenerator.config.ScenarioItem;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SummaryLogger {

    private long start;
    private long end;
    private String status;
    private Config config;
    private final List<Map.Entry<Long, ProfileItem>> changes = new ArrayList<>();

    /**
     *
     */
    public void setStart() {
        start = System.currentTimeMillis();
    }

    /**
     *
     */
    public void setEnd() {
        end = System.currentTimeMillis();
    }

    /**
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     *
     * @param item
     */
    public synchronized void addChange(ProfileItem item) {
        changes.add(new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), item));
    }

    /**
     *
     * @param out
     */
    public void printSummary(PrintStream out) {
        out.println("***** SUMMARY START *****");
        out.println("Start: " + start);
        out.println("End: " + end);
        out.println("Description: " + config.getDescription());
        out.println("Status: " + status);

        out.println("Initial Setup: ");
        out.println("    Thread Count: " + config.getThreadCount());
        out.println("    End: " + config.getProfile().getEnd());
        out.println("    Burst Limit: " + config.getProfile().getBurstLimit());

        if (!changes.isEmpty()) {
            out.println("Changes:");
            for (Map.Entry<Long, ProfileItem> entry : changes) {
                out.println("    " + entry.getKey());

                ProfileItem item = entry.getValue();
                for (ScenarioItem scenario : item.getScenarios()) {
                    out.println("        " + scenario.getType() + "; " + scenario.getCount());
                }
            }
        }

        out.println("***** SUMMARY END *****");
        out.flush();
    }

}
