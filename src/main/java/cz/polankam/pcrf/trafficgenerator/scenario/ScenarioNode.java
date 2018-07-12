package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.exceptions.ScenarioException;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;

import java.util.*;

/**
 * Scenario nodes are used for construction of the scenario which is based on the automatons. Therefore, there are
 * graphs of nodes in which the scenario operates. The scenario node contains information about actions which should be
 * performed if the node is active. Another attributes of the node are list of children and debug name of the node.
 * @note Read-only structure! During the execution, changing of the attributes can have unpredictable consequences.
 */
public class ScenarioNode {

    private final List<ScenarioNodeEntry> children;
    private final Queue<ScenarioActionEntry> actions;
    private String name;


    /**
     * Constructor.
     */
    public ScenarioNode() {
        children = new ArrayList<>();
        actions = new LinkedList<>();
    }


    /**
     * Get debug name of the scenario node.
     * @return name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Set debug name of the scenario node.
     * @param name name of the node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Determine if the node has children or not.
     * @return true if there are no children, false otherwise
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Based on given probability, get the most suitable child.
     * The node contains children and their corresponding attributes like the transition probabilities, based on these
     * the decision which is the next node is done.
     * @param probability probability of transition
     * @return next child which will be processed
     * @throws ScenarioException in case the probabilities are not in range of zero and hundred
     */
    public ScenarioNodeEntry getChild(int probability) throws ScenarioException {
        if (probability < 0 || probability > 100) {
            throw new ScenarioException("Bad probability value");
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

    /**
     * Get the list of children nodes for the node.
     * @return list of scenario node entries
     */
    protected List<ScenarioNodeEntry> getChildren() {
        return children;
    }

    /**
     * Add given child node to this node.
     * The probability is undefined, delay is set to zero.
     * @param child child scenario node
     * @return this
     */
    public ScenarioNode addChild(ScenarioNode child) {
        children.add(new ScenarioNodeEntry(child));
        return this;
    }

    /**
     * Add given child node to this node.
     * The delay is set to zero.
     * @param probability probability of transition to the given child
     * @param child child scenario node
     * @return this
     */
    public ScenarioNode addChild(int probability, ScenarioNode child) {
        children.add(new ScenarioNodeEntry(probability, child));
        return this;
    }

    /**
     * Add given child node to this node.
     * @param probability probability of transition to the given child
     * @param averageDelay delay for the given child node
     * @param child child scenario node
     * @return this
     */
    public ScenarioNode addChild(int probability, long averageDelay, ScenarioNode child) {
        children.add(new ScenarioNodeEntry(probability, averageDelay, child));
        return this;
    }

    /**
     * Get a newly create copy of the actions defined in this scenario node.
     * @return copied queue of the actions
     */
    public Deque<ScenarioActionEntry> getActionsCopy() {
        Deque<ScenarioActionEntry> copy = new LinkedList<>();
        copy.addAll(actions);
        return copy;
    }

    /**
     * Add action to this scenario node.
     * @param action scenario action entry
     * @return this
     */
    public ScenarioNode addAction(ScenarioActionEntry action) {
        actions.add(action);
        return this;
    }

    /**
     * Validate all probabilities defined in the children nodes entries.
     * If all of the sums are undefined, the uniform distribution of probabilities is applied for all children.
     * @note Should be called after all children were added to the node.
     * @throws ScenarioException in case the sums were not set or the sum is not 100
     */
    public void validateProbabilities() throws ScenarioException {
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
            throw new ScenarioException("Sum of the probabilities of children is not equal to 100");
        }
    }

}
