package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;

/**
 * Scenario node builder brings simpler and fluent building mechanism for ScenarioNode structures.
 */
public class NodeBuilder {

    private final ScenarioNode node;

    /**
     * Create new builder instance with a newly created scenario node.
     */
    public NodeBuilder() {
        node = new ScenarioNode();
    }


    /**
     * Set the name of the node. Can be used for debugging purposes.
     * @param name name of the node
     * @return this
     */
    public NodeBuilder setName(String name) {
        node.setName(name);
        return this;
    }

    /**
     * Add send action to the scenario node.
     * @param name name of the action
     * @param delay delay before performing the action
     * @param action scenario action
     * @return this
     */
    public NodeBuilder addSendAction(String name, long delay, ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(name, delay, action));
        return this;
    }

    /**
     * Add send action to the scenario node, the delay is set to zero.
     * @param name name of the action
     * @param action scenario action
     * @return this
     */
    public NodeBuilder addSendAction(String name, ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(name, action));
        return this;
    }

    /**
     * Add send action to the scenario node, the name of the action is undefined.
     * @param delay delay before performing the action
     * @param action scenario action
     * @return this
     */
    public NodeBuilder addSendAction(long delay, ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(delay, action));
        return this;
    }

    /**
     * Add scenario action to the scenario node, the delay is set to zero and name is undefined.
     * @param action scenario action
     * @return this
     */
    public NodeBuilder addSendAction(ScenarioAction action) {
        node.addAction(new SendScenarioActionEntry(action));
        return this;
    }

    /**
     * Add receive action which receives only message from the Gx interface.
     * @param name debug name of the action
     * @param timeout timeout for reception of the message
     * @param gxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveGxAction(String name, long timeout, ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, timeout, gxAction, null));
        return this;
    }

    /**
     * Add receive action which receives only message from the Gx interface.
     * The timeout defaults to zero.
     * @param name debug name of the action
     * @param gxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveGxAction(String name, ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, gxAction, null));
        return this;
    }

    /**
     * Add receive action which receives only message from the Gx interface.
     * The timeout defaults to zero, name of the action is undefined.
     * @param gxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveGxAction(ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(gxAction, null));
        return this;
    }

    /**
     * Add receive action which receives only message from the Gx interface.
     * The name of the action is undefined.
     * @param timeout timeout for reception of the message
     * @param gxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveGxAction(long timeout, ScenarioAction gxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, gxAction, null));
        return this;
    }

    /**
     * Add receive action which receives only message from the Rx interface.
     * @param name debug name of the action
     * @param timeout timeout for reception of the message
     * @param rxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveRxAction(String name, long timeout, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, timeout, null, rxAction));
        return this;
    }

    /**
     * Add receive action which receives only message from the Rx interface.
     * The timeout defaults to zero.
     * @param name debug name of the action
     * @param rxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveRxAction(String name, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, null, rxAction));
        return this;
    }

    /**
     * Add receive action which receives only message from the Rx interface.
     * The timeout defaults to zero, the name of the action is undefined.
     * @param rxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveRxAction(ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(null, rxAction));
        return this;
    }

    /**
     * Add receive action which receives only message from the Rx interface.
     * The name of the action is undefined.
     * @param timeout timeout for reception of the message
     * @param rxAction scenario action
     * @return this
     */
    public NodeBuilder addReceiveRxAction(long timeout, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, null, rxAction));
        return this;
    }

    /**
     * Add receive action which receives messages from both of the interfaces.
     * @param name debug name of the action
     * @param timeout timeout for reception of messages
     * @param gxAction Gx scenario action
     * @param rxAction Rx scenario action
     * @return this
     */
    public NodeBuilder addReceiveAction(String name, long timeout, ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, timeout, gxAction, rxAction));
        return this;
    }

    /**
     * Add receive action which receives messages from both of the interfaces.
     * The timeout defaults to zero.
     * @param name debug name of the actions
     * @param gxAction Gx scenario action
     * @param rxAction Rx scenario action
     * @return this
     */
    public NodeBuilder addReceiveAction(String name, ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(name, gxAction, rxAction));
        return this;
    }

    /**
     * Add receive action which receives messages from both of the interfaces.
     * The timeout defaults to zero, the name of the actions is undefined.
     * @param gxAction Gx scenario action
     * @param rxAction Rx scenario action
     * @return this
     */
    public NodeBuilder addReceiveAction(ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(gxAction, rxAction));
        return this;
    }

    /**
     * Add receive action which receives messages from both of the interfaces.
     * The name of the actions is undefined.
     * @param timeout timeout for reception of messages
     * @param gxAction Gx scenario action
     * @param rxAction Rx scenario action
     * @return this
     */
    public NodeBuilder addReceiveAction(long timeout, ScenarioAction gxAction, ScenarioAction rxAction) {
        node.addAction(new ReceiveScenarioActionEntry(timeout, gxAction, rxAction));
        return this;
    }

    /**
     * Build the scenario node and return the instance which was constructed using this builder.
     * @return scenario node instance
     */
    public ScenarioNode build() {
        return node;
    }

}
