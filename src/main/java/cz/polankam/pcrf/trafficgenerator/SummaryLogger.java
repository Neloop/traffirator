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
 * Ensures that proper summary log is printed, the summary contains information concerning current execution.
 * There is a start of the execution, end, there is also description and initial setup which was provided by
 * the configuration. Also changes made to the scenarios count are logged here.
 */
public class SummaryLogger {

    private long start;
    private long end;
    private String status;
    private Config config;
    private final List<Map.Entry<Long, ProfileItem>> changes = new ArrayList<>();

    /**
     * Set the starting point of the execution.
     */
    public void setStart() {
        start = System.currentTimeMillis();
    }

    /**
     * Set the end of the execution.
     */
    public void setEnd() {
        end = System.currentTimeMillis();
    }

    /**
     * Set the status of the execution, if it was OK or not.
     * @param status textual representation of status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Set configuration used during the execution and given by the user.
     * @param config configuration structure loaded from the YAML
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Add change entry, which changed the amount of the currently active scenarios.
     * @param item item from the configuration describing the change
     */
    public synchronized void addChange(ProfileItem item) {
        changes.add(new AbstractMap.SimpleEntry<>(System.currentTimeMillis(), item));
    }

    /**
     * Print the summary log into given output stream.
     * @param out output stream
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
