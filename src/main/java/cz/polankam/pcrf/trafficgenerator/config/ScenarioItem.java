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

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Number of scenarios with particular type which will be spawned, in absolute numbers.
     * @return
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
