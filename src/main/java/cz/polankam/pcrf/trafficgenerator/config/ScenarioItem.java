package cz.polankam.pcrf.trafficgenerator.config;

/**
 * The scenario item holds information about a single scenario item from the profile item configuration.
 */
public class ScenarioItem {

    private String type;
    private int count;


    /**
     * Type of the used scenarios.
     * @return textual identification of type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the used scenarios.
     * @param type textual identification of the type
     * @return this
     */
    public ScenarioItem setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Number of scenarios with particular type which will be spawned, in absolute numbers.
     * @return count of the scenarios with the particular types
     */
    public int getCount() {
        return count;
    }

    /**
     * Set the number of scenario with particular given type.
     * @param count count of the scenarios, in absolute numbers
     * @return this
     */
    public ScenarioItem setCount(int count) {
        this.count = count;
        return this;
    }
}
