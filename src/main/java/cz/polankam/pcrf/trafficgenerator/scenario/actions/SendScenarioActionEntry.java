package cz.polankam.pcrf.trafficgenerator.scenario.actions;


/**
 * @note Should not have setters! Read-only structure!
 */
public class SendScenarioActionEntry implements ScenarioActionEntry {

    private final String name;
    private final long averageDelay;
    private final ScenarioAction action;


    /**
     *
     * @param name
     * @param averageDelay in milliseconds
     * @param action
     */
    public SendScenarioActionEntry(String name, long averageDelay, ScenarioAction action) {
        this.name = name;
        this.averageDelay = averageDelay;
        this.action = action;
    }

    /**
     *
     * @param averageDelay in milliseconds
     * @param action
     */
    public SendScenarioActionEntry(long averageDelay, ScenarioAction action) {
        this("", averageDelay, action);
    }

    /**
     * New scenario entry with no delay or timeout.
     * @param action
     */
    public SendScenarioActionEntry(String name, ScenarioAction action) {
        this(name, 0, action);
    }

    /**
     * New scenario entry with no delay or timeout.
     * @param action
     */
    public SendScenarioActionEntry(ScenarioAction action) {
        this(0, action);
    }

    @Override
    public boolean isSending() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    public ScenarioAction getAction() {
        return action;
    }

    /**
     * Get delay of this action in milliseconds.
     * @return in milliseconds
     */
    public long getAverageDelay() {
        return averageDelay;
    }

}
