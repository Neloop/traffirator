package cz.polankam.pcrf.trafficgenerator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * The class holds the information of a single profile item which was loaded from the configuration.
 */
public class ProfileItem {

    private long start = 0;
    private List<ScenarioItem> scenarios = new ArrayList<>();


    /**
     * Begin this testing profile item at, in seconds.
     * @return start of this profile entry in the relative seconds
     */
    public long getStart() {
        return start;
    }

    /**
     * Set the start of this profile item.
     * @param start start in seconds
     * @return this
     */
    public ProfileItem setStart(long start) {
        this.start = start;
        return this;
    }

    /**
     * List of scenarios used in this profile item.
     * @return list of the scenario items
     */
    public List<ScenarioItem> getScenarios() {
        return scenarios;
    }

    /**
     * Set the scenario which should be active in this profile item.
     * @param scenarios list of the scenario items
     * @return this
     */
    public ProfileItem setScenarios(List<ScenarioItem> scenarios) {
        this.scenarios = scenarios;
        return this;
    }
}
