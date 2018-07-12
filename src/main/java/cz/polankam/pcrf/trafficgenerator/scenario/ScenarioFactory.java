package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.exceptions.ScenarioException;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.DemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.SimpleDemoScenario;
import cz.polankam.pcrf.trafficgenerator.scenario.impl.real.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for all hardcoded scenarios. If there is new scenario, it has to be registered in this factory to be able
 * to properly use it in configuration.
 */
public class ScenarioFactory {

    private final Map<String, Class<? extends Scenario>> scenarios;

    /**
     * Constructor which registers all possible scenarios within generator.
     */
    public ScenarioFactory() {
        scenarios = new HashMap<>();
        scenarios.put(DemoScenario.TYPE, DemoScenario.class);
        scenarios.put(SimpleDemoScenario.TYPE, SimpleDemoScenario.class);
        scenarios.put(ClassicUserScenario.TYPE, ClassicUserScenario.class);
        scenarios.put(CallPerformanceScenario.TYPE, CallPerformanceScenario.class);
        scenarios.put(MalfunctioningCellPhoneScenario.TYPE, MalfunctioningCellPhoneScenario.class);
        scenarios.put(CallCenterEmployeeScenario.TYPE, CallCenterEmployeeScenario.class);
        scenarios.put(TravellingManagerScenario.TYPE, TravellingManagerScenario.class);
    }


    /**
     * Check if given scenario type exists or not.
     * @param type scenario type
     * @return true if scenario exists, false otherwise
     */
    public boolean check(String type) {
        return scenarios.get(type) != null;
    }

    /**
     * Based on given scenario type, create new instance of scenario and return it.
     * @param type scenario type
     * @return new instance scenario
     * @throws Exception in case of creation error or unknown type
     */
    public Scenario create(String type) throws Exception {
        Class<? extends Scenario> factory = scenarios.get(type);
        if (factory == null) {
            throw new ScenarioException("Unknown type of scenario '" + type + "'");
        }

        return factory.newInstance();
    }

}
