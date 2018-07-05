package cz.polankam.pcrf.trafficgenerator.scenario.actions;

/**
 * Interface for the scenario action entries which are holding the action and appropriate information about the action.
 */
public interface ScenarioActionEntry {

    /**
     * Determine if the current action is sending or receiving.
     * @return true if the action is sending, false otherwise
     */
    boolean isSending();

    /**
     * Get the debug name of the action.
     * @return action name
     */
    String getName();
}
