package cz.polankam.pcrf.trafficgenerator.config;

import cz.polankam.pcrf.trafficgenerator.exceptions.ValidationException;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;


public class ProfileValidator {

    private ScenarioFactory scenarioFactory;

    public ProfileValidator(ScenarioFactory factory) {
        this.scenarioFactory = factory;
    }


    /**
     * Validate test profile from given configuration. Validator checks for correct order of events and if correct types
     * of scenarios are given.
     * @param config
     * @throws Exception
     */
    public void validate(Config config) throws ValidationException {
        if (config.getProfile() == null || config.getProfile().isEmpty()) {
            throw new ValidationException("Test profile has to contain at least one item");
        }

        long previous = 0;
        for (ProfileItem item : config.getProfile()) {
            if (item.getStart() < 0) {
                throw new ValidationException("Profile item start '" + item.getStart() + "' is lower than zero");
            }

            if (item.getStart() < previous) {
                throw new ValidationException("Profile item '" + item.getStart() + "' start is lower then previous one '" + previous + "'");
            }
            previous = item.getStart();

            // check correct scenarios types
            for (ScenarioItem scenario : item.getScenarios()) {
                if (!scenarioFactory.check(scenario.getType())) {
                    throw new ValidationException("Unknown scenario type '" + scenario.getType() + "'");
                }
            }
        }
    }

}
