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
     * @throws ValidationException
     */
    public void validate(Config config) throws ValidationException {
        if (config.getProfile() == null) {
            throw new ValidationException("Configuration does not contain test profile");
        }

        if (config.getProfile().getBurstLimit() <= 0) {
            throw new ValidationException("Burst limit in the profile cannot be less than or equals to zero");
        }

        if (config.getProfile().getEnd() <= 0) {
            throw new ValidationException("End in the profile cannot be less than or equals to zero");
        }

        if (config.getProfile().getFlow().isEmpty()) {
            throw new ValidationException("Test profile has to contain at least one item");
        }

        long previous = 0;
        for (ProfileItem item : config.getProfile().getFlow()) {
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
