package cz.polankam.pcrf.trafficgenerator.scenario.actions;


/**
 * @note Should not have setters! Read-only structure!
 */
public class SendScenarioActionEntry implements ScenarioActionEntry {

    private final long averageDelay;
    private final ScenarioAction action;


    /**
     *
     * @param averageDelay in milliseconds
     * @param action
     */
    public SendScenarioActionEntry(long averageDelay, ScenarioAction action) {
        this.averageDelay = averageDelay;
        this.action = action;
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
        return ""; // TODO
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
