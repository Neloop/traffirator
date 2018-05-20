package cz.polankam.pcrf.trafficgenerator.config;


public class ScenarioItem {

    private String type;
    private long count;


    /**
     * Type of the used scenarios.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Number of scenarios with particular type which will be spawned, in absolute numbers.
     * @return
     */
    public long getCount() {
        return count;
    }

}
