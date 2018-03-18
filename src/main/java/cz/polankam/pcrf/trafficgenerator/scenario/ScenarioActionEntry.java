package cz.polankam.pcrf.trafficgenerator.scenario;


public class ScenarioActionEntry {
    
    private final boolean sending;
    private final long delay;
    private final ScenarioAction action;

    
    /**
     * 
     * @param sending
     * @param delay in milliseconds
     * @param action
     * @throws Exception 
     */
    public ScenarioActionEntry(boolean sending, long delay, ScenarioAction action) throws Exception {
        this.sending = sending;
        this.delay = delay;
        this.action = action;
    }
    
    /**
     * New scenario entry with no delay.
     * @param sending
     * @param action
     * @throws Exception 
     */
    public ScenarioActionEntry(boolean sending, ScenarioAction action) throws Exception {
        this(sending, 0, action);
    }

    public boolean isSending() {
        return sending;
    }

    public ScenarioAction getAction() {
        return action;
    }

    /**
     * Get delay of this action in milliseconds.
     * @return 
     */
    public long getDelay() {
        return delay;
    }
    
}
