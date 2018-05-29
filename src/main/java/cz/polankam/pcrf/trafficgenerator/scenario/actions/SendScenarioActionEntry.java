package cz.polankam.pcrf.trafficgenerator.scenario.actions;


public class SendScenarioActionEntry implements ScenarioActionEntry {

    private final long delay;
    private final ScenarioAction action;


    /**
     *
     * @param delay in milliseconds
     * @param action
     */
    public SendScenarioActionEntry(long delay, ScenarioAction action) {
        this.delay = delay;
        this.action = action;
    }

    /**
     * New scenario entry with no delay or timeout.
     * @param action
     */
    public SendScenarioActionEntry(ScenarioAction action) {
        this(0, action);
    }

    public boolean isSending() {
        return true;
    }

    public ScenarioAction getAction() {
        return action;
    }

    /**
     * Get delay of this action in milliseconds.
     * @return in milliseconds
     */
    public long getDelay() {
        return delay;
    }
    
}
