package cz.polankam.pcrf.trafficgenerator.config;


public class ScenarioItem {

    private String type;
    private int count;


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
    public int getCount() {
        return count;
    }

}
