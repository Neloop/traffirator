package cz.polankam.pcrf.trafficgenerator.test.utils;

import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;

import java.util.HashMap;

/**
 * Mock for testing purposes.
 */
public class ScenarioMock extends Scenario {

    private HashMap<String, Object> state;
    private ScenarioNode rootNode;
    private int delayVariability;

    @Override
    protected HashMap<String, Object> createNewScenarioState() {
        return state;
    }

    public void setState(HashMap<String, Object> state) {
        this.state = state;
    }

    @Override
    protected ScenarioNode getRootNode() throws Exception {
        return rootNode;
    }

    public void setRootNode(ScenarioNode rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public String getType() {
        return "MOCK";
    }

    @Override
    public int getDelaysVariability() {
        return delayVariability;
    }

    public void setDelayVariability(int delayVariability) {
        this.delayVariability = delayVariability;
    }
}
