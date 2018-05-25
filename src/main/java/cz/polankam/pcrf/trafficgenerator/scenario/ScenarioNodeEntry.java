package cz.polankam.pcrf.trafficgenerator.scenario;


public class ScenarioNodeEntry {

    private int probability;
    private final ScenarioNode node;


    public ScenarioNodeEntry(int probability, ScenarioNode node) {
        this.probability = probability;
        this.node = node;
    }

    /**
     * New scenario node entry with undefined probability.
     * @param node
     */
    public ScenarioNodeEntry(ScenarioNode node) {
        this(0, node);
    }


    public ScenarioNode getNode() {
        return node;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

}
