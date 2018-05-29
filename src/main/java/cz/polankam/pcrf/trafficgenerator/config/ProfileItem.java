package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;


public class ProfileItem {

    private long start;
    private List<ScenarioItem> scenarios;


    /**
     * Begin this testing profile item at, in milliseconds.
     * @return
     */
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    /**
     * List of scenarios used in this profile item.
     * @return
     */
    public List<ScenarioItem> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<ScenarioItem> scenarios) {
        this.scenarios = scenarios;
    }
}
