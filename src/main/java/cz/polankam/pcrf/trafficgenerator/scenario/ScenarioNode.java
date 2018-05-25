package cz.polankam.pcrf.trafficgenerator.scenario;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ScenarioNode {

    private final List<ScenarioNodeEntry> children;
    private final Queue<ScenarioActionEntry> actions;


    public ScenarioNode() {
        children = new ArrayList<>();
        actions = new LinkedList<>();
    }


    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public ScenarioNode getChild(int probability) throws Exception {
        if (probability < 0 || probability > 100) {
            throw new Exception("Bad probability value");
        }

        if (children.isEmpty()) {
            return null;
        } else if (children.size() == 1) {
            return children.get(0).getNode();
        }

        ScenarioNode node = null;
        int sum = 0;
        for (ScenarioNodeEntry entry : children) {
            if ((sum + entry.getProbability()) >= probability) {
                node = entry.getNode();
                break;
            }

            sum += entry.getProbability();
        }

        // node was not found, that means there was missing padding at the end... so grab the last node
        if (node == null) {
            node = children.get(children.size() - 1).getNode();
        }

        return node;
    }

    protected List<ScenarioNodeEntry> getChildren() {
        return children;
    }

    public ScenarioNode addChild(ScenarioNode child) throws Exception {
        children.add(new ScenarioNodeEntry(child));
        return this;
    }

    public ScenarioNode addChild(int probability, ScenarioNode child) throws Exception {
        children.add(new ScenarioNodeEntry(probability, child));
        return this;
    }

    public Queue<ScenarioActionEntry> getActionsCopy() {
        Queue<ScenarioActionEntry> copy = new LinkedList<>();
        copy.addAll(actions);
        return copy;
    }

    public ScenarioNode addAction(ScenarioActionEntry action) {
        actions.add(action);
        return this;
    }

    /**
     * Should be called after all children were added to the node.
     * @throws Exception
     */
    public void validateProbabilities() throws Exception {
        if (children.isEmpty()) {
            return;
        }

        // determine if probabilities were set or not
        boolean notSet = children.stream().allMatch((ScenarioNodeEntry entry) -> {
            return entry.getProbability() == 0;
        });

        // validate or compute probabilities
        int sum = 0;
        int uniform = 100 / children.size();
        for (ScenarioNodeEntry entry : children) {
            if (notSet) {
                // if probabilities were not set, then do uniform distribution
                entry.setProbability(uniform);
            } else {
                sum += entry.getProbability();
            }
        }

        if (!notSet && sum != 100) {
            throw new Exception("Sum of the probabilites of children is not equal to 100");
        }
    }

}
