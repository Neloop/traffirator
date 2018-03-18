package cz.polankam.pcrf.trafficgenerator.scenario.factory;

import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.SimpleDemoScenario;


public class SimpleDemoScenarioFactory implements ScenarioFactory {

    @Override
    public Scenario create() {
        return new SimpleDemoScenario();
    }

}
