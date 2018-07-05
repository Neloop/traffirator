package cz.polankam.pcrf.trafficgenerator.scenario.actions;


/**
 * Represents action which sends the message to the remote server.
 * In addition to the action it holds the delay for the action.
 * @note Should not have setters! Read-only structure!
 */
public class SendScenarioActionEntry implements ScenarioActionEntry {

    private final String name;
    private final long averageDelay;
    private final ScenarioAction action;


    /**
     * Constructor.
     * @param name name of the action
     * @param averageDelay in milliseconds
     * @param action performed action
     */
    public SendScenarioActionEntry(String name, long averageDelay, ScenarioAction action) {
        this.name = name;
        this.averageDelay = averageDelay;
        this.action = action;
    }

    /**
     * Constructor with default empty action name.
     * @param averageDelay in milliseconds
     * @param action performed action
     */
    public SendScenarioActionEntry(long averageDelay, ScenarioAction action) {
        this("", averageDelay, action);
    }

    /**
     * New scenario entry with no delay.
     * @param name name of the action
     * @param action performed action
     */
    public SendScenarioActionEntry(String name, ScenarioAction action) {
        this(name, 0, action);
    }

    /**
     * New scenario entry with no delay and default empty name.
     * @param action performed action
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

    /**
     * Get the sending action for this entry.
     * @return specific action
     */
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
