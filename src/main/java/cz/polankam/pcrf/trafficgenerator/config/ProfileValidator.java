package cz.polankam.pcrf.trafficgenerator.config;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;


public class ProfileValidator {

    /**
     * Validate test profile from given configuration. Validator checks for correct order of events and if correct types
     * of scenarios are given.
     * @param config
     * @throws Exception
     */
    public static void validate(Config config) throws Exception {
        if (config.getProfile().isEmpty()) {
            throw new Exception("Test profile has to contain at least one item");
        }

        long previous = 0;
        for (ProfileItem item : config.getProfile()) {
            if (item.getStart() < previous) {
                throw new Exception("Profile item '" + item.getStart() + "' start is lower then previous one '" + previous + "'.");
            }
            previous = item.getStart();

            // check correct scenarios types
            for (ScenarioItem scenario : item.getScenarios()) {
                if (!ScenarioFactory.check(scenario.getType())) {
                    throw new Exception("Unknow scenario type '" + scenario.getType() + "'");
                }
            }
        }
    }

}
