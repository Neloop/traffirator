package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.impl.DemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.SimpleDemoScenario;
import java.util.HashMap;
import java.util.Map;


public class ScenarioFactory {

    private final static Map<String, Class<? extends Scenario>> scenarios = new HashMap<>();

    static {
        scenarios.put(DemoScenario.TYPE, DemoScenario.class);
        scenarios.put(SimpleDemoScenario.TYPE, SimpleDemoScenario.class);
    }


    public static boolean check(String type) {
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
