package cz.polankam.pcrf.trafficgenerator.scenario;


import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;

/**
 * Scenario node builder which brings simpler and fluent building mechanism for ScenarioNode structures.
 */
public class NodeBuilder {

    private final ScenarioNode node;

    public NodeBuilder() {
        node = new ScenarioNode();
    }

    public NodeBuilder addSendAction(ScenarioAction action) {
        node.addAction(new ScenarioActionEntry(true, action));
        return this;
    }

    public NodeBuilder addSendAction(long delay, ScenarioAction action) {
        node.addAction(new ScenarioActionEntry(true, delay, action));
        return this;
    }

    public NodeBuilder addReceiveAction(ScenarioAction action) {
        node.addAction(new ScenarioActionEntry(false, action));
        return this;
    }

    public NodeBuilder addReceiveAction(long timeout, ScenarioAction action) {
        node.addAction(new ScenarioActionEntry(false, timeout, action));
        return this;
    }

    public ScenarioNode build() {
        return node;
    }

}
