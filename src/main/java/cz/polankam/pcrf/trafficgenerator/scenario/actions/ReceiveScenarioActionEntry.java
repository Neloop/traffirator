package cz.polankam.pcrf.trafficgenerator.scenario.actions;

/**
 * Represents action which receives the message or messages from the server. The entry can hold two receiving action
 * each for both interfaces - Gx and Rx. This is in the case when two messages can arrive at the same time.
 * In addition to the action it holds the timeout for the message reception.
 * @note Should not have setters! Read-only structure!
 */
public class ReceiveScenarioActionEntry implements ScenarioActionEntry {

    private final String name;
    private final long timeout;
    private ScenarioAction gxAction;
    private ScenarioAction rxAction;


    /**
     * Constructor.
     * @param name debug name of the action
     * @param timeout in milliseconds
     * @param gxAction action received on Gx interface, can be null
     * @param rxAction action received on Rx interface, can be null
     */
    public ReceiveScenarioActionEntry(String name, long timeout, ScenarioAction gxAction, ScenarioAction rxAction) {
        this.name = name;
        this.timeout = timeout;
        this.gxAction = gxAction;
        this.rxAction = rxAction;
    }

    /**
     * Constructor with default empty action name.
     * @param timeout in milliseconds
     * @param gxAction action received on Gx interface, can be null
     * @param rxAction action received on Rx interface, can be null
     */
    public ReceiveScenarioActionEntry(long timeout, ScenarioAction gxAction, ScenarioAction rxAction) {
        this("", timeout, gxAction, rxAction);
    }

    /**
     * New scenario entry with no timeout.
     * @param name name of the action
     * @param gxAction action received on Gx interface, can be null
     * @param rxAction action received on Rx interface, can be null
     */
    public ReceiveScenarioActionEntry(String name, ScenarioAction gxAction, ScenarioAction rxAction) {
        this(name, 0, gxAction, rxAction);
    }

    /**
     * New scenario entry with no timeout and default empty name.
     * @param gxAction action received on Gx interface, can be null
     * @param rxAction action received on Rx interface, can be null
     */
    public ReceiveScenarioActionEntry(ScenarioAction gxAction, ScenarioAction rxAction) {
        this("", 0, gxAction, rxAction);
    }

    @Override
    public boolean isSending() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get Gx action which is held by this entry.
     * @return receiving scenario action
     */
    public ScenarioAction getGxAction() {
        return gxAction;
    }

    /**
     * Get Rx action which is held by this entry.
     * @return receiving scenario action
     */
    public ScenarioAction getRxAction() {
        return rxAction;
    }

    /**
     * Get timeout of this action in milliseconds.
     * @return in milliseconds
     */
    public long getTimeout() {
        return timeout;
    }

}
