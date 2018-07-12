package cz.polankam.pcrf.trafficgenerator.scenario;

/**
 * Scenario node entry contains scenario node and in addition holds probabilities of transition and delays between two
 * nodes. It is used in scenario node for children definition and holds some various attributes for the relation between
 * parent and child.
 */
public class ScenarioNodeEntry {

    private int probability;
    private long averageDelay;
    private final ScenarioNode node;


    /**
     * Constructor.
     * @param probability probability of transition to the scenario node
     * @param averageDelay average delay which should be applied for the transition, should not be zero
     * @param node scenario node associated with this entry
     */
    public ScenarioNodeEntry(int probability, long averageDelay, ScenarioNode node) {
        this.probability = probability;
        this.averageDelay = averageDelay;
        this.node = node;
    }

    /**
     * Constructor without delay, which is set to zero.
     * @param probability probability of transition to the scenario node
     * @param node scenario node associated with this entry
     */
    public ScenarioNodeEntry(int probability, ScenarioNode node) {
        this(probability, 0, node);
    }

    /**
     * Constructor with undefined probability and delay set to zero.
     * @param node scenario node associated with this entry
     */
    public ScenarioNodeEntry(ScenarioNode node) {
        this(0, node);
    }


    /**
     * Get scenario node associated with this entry.
     * @return scenario node
     */
    public ScenarioNode getNode() {
        return node;
    }

    /**
     * Get average delay for the transition between parent and this node.
     * @return numeric delay representation
     */
    public long getAverageDelay() {
        return averageDelay;
    }

    /**
     * Get the probability of the transition between parent and this node.
     * @return number in the range of zero and hundred
     */
    public int getProbability() {
        return probability;
    }

    /**
     * Set the probability of the transition between parent and this node.
     * @param probability number in the range of zero and hundred
     */
    public void setProbability(int probability) {
        this.probability = probability;
    }

}
