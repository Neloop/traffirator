package cz.polankam.pcrf.trafficgenerator.scenario;


import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;

/**
 * Scenario node builder which brings simpler and fluent building mechanism for ScenarioNode structures.
 */
public class NodeBuilder {

    private final ScenarioNode node;

    public NodeBuilder() {
        node = new ScenarioNode();
    }

    public NodeBuilder setName(String name) {
        node.setName(name);
        return this;
    }

    public NodeBuilder addSendAction(ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(action));
        return this;
    }

    public NodeBuilder addSendAction(long delay, ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(delay, action));
        return this;
    }

    public NodeBuilder addReceiveGxAction(ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(gxAction, null));
        return this;
    }

    public NodeBuilder addReceiveGxAction(long timeout, ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, gxAction, null));
        return this;
    }

    public NodeBuilder addReceiveRxAction(ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(null, rxAction));
        return this;
    }

    public NodeBuilder addReceiveRxAction(long timeout, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, null, rxAction));
        return this;
    }

    public NodeBuilder addReceiveAction(ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(gxAction, rxAction));
        return this;
    }

    public NodeBuilder addReceiveAction(long timeout, ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, gxAction, rxAction));
        return this;
    }

    public ScenarioNode build() {
        return node;
    }

}
