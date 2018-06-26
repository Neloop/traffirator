package cz.polankam.pcrf.trafficgenerator.config;

import java.util.ArrayList;
import java.util.List;


public class ProfileItem {

    private long start = 0;
    private List<ScenarioItem> scenarios = new ArrayList<>();


    /**
     * Begin this testing profile item at, in seconds.
     * @return
     */
    public long getStart() {
        return start;
    }

    public ProfileItem setStart(long start) {
        this.start = start;
        return this;
    }

    /**
     * List of scenarios used in this profile item.
     * @return
     */
    public List<ScenarioItem> getScenarios() {
        return scenarios;
    }

    public ProfileItem setScenarios(List<ScenarioItem> scenarios) {
        this.scenarios = scenarios;
        return this;
    }
}
