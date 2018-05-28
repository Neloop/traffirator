package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.impl.DemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.SimpleDemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.real.CallPerformanceScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.real.ClassicUserScenario;

import java.util.HashMap;
import java.util.Map;


public class ScenarioFactory {

    private final Map<String, Class<? extends Scenario>> scenarios;

    public ScenarioFactory() {
        scenarios = new HashMap<>();
        scenarios.put(DemoScenario.TYPE, DemoScenario.class);
        scenarios.put(SimpleDemoScenario.TYPE, SimpleDemoScenario.class);
        scenarios.put(ClassicUserScenario.TYPE, ClassicUserScenario.class);
        scenarios.put(CallPerformanceScenario.TYPE, CallPerformanceScenario.class);
    }


    public boolean check(String type) {
        return scenarios.get(type) != null;
    }

    public Scenario create(String type) throws Exception {
        Class<? extends Scenario> factory = scenarios.get(type);
        if (factory == null) {
            throw new Exception("Unknown type of scenario '" + type + "'");
        }

        return factory.newInstance();
    }

}
