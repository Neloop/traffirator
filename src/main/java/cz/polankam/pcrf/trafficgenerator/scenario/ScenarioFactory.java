package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.impl.DemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.SimpleDemoScenario;
import java.util.HashMap;
import java.util.Map;


public class ScenarioFactory {

    private Map<String, Class<? extends Scenario>> scenarios = new HashMap<>();

    public ScenarioFactory() {
        scenarios.put(DemoScenario.TYPE, DemoScenario.class);
        scenarios.put(SimpleDemoScenario.TYPE, SimpleDemoScenario.class);
    }


    public Scenario create(String type) throws Exception {
        Class<? extends Scenario> factory = scenarios.get(type);
        if (factory == null) {
            throw new Exception("Unknow type of scenario '" + type + "'");
        }

        return factory.newInstance();
    }

}
