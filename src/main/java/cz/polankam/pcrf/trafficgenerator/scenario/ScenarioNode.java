package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;

import java.util.*;


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

    public ScenarioNodeEntry getChild(int probability) throws Exception {
        if (probability < 0 || probability > 100) {
            throw new Exception("Bad probability value");
        }

        if (children.isEmpty()) {
            return null;
        } else if (children.size() == 1) {
            return children.get(0);
        }

        ScenarioNodeEntry nodeEntry = null;
        int sum = 0;
        for (ScenarioNodeEntry entry : children) {
            if ((sum + entry.getProbability()) >= probability) {
                nodeEntry = entry;
                break;
            }

            sum += entry.getProbability();
        }

        // node was not found, that means there was missing padding at the end... so grab the last node
        if (nodeEntry == null) {
            nodeEntry = children.get(children.size() - 1);
        }

        return nodeEntry;
    }

    protected List<ScenarioNodeEntry> getChildren() {
        return children;
    }

    public ScenarioNode addChild(ScenarioNode child) {
        children.add(new ScenarioNodeEntry(child));
        return this;
    }

    public ScenarioNode addChild(int probability, ScenarioNode child) {
        children.add(new ScenarioNodeEntry(probability, child));
        return this;
    }

    public ScenarioNode addChild(int probability, long averageDelay, ScenarioNode child) {
        children.add(new ScenarioNodeEntry(probability, averageDelay, child));
        return this;
    }

    public Deque<ScenarioActionEntry> getActionsCopy() {
        Deque<ScenarioActionEntry> copy = new LinkedList<>();
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
        boolean notSet = children.stream().allMatch((ScenarioNodeEntry entry) -> entry.getProbability() == 0);

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
            throw new Exception("Sum of the probabilities of children is not equal to 100");
        }
    }

}
