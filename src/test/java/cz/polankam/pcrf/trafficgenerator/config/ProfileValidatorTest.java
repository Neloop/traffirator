package cz.polankam.pcrf.trafficgenerator.config;

import cz.polankam.pcrf.trafficgenerator.exceptions.ValidationException;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProfileValidatorTest {

    @Test
    void testValidate_nullProfile() {
        ProfileValidator validator = new ProfileValidator(new ScenarioFactory());
        assertThrows(ValidationException.class, () -> {
            validator.validate(new Config());
        });
    }

    @Test
    void testValidate_emptyProfile() {
        ProfileValidator validator = new ProfileValidator(new ScenarioFactory());
        Config config = new Config();
        config.setProfile(new ArrayList<>());

        assertThrows(ValidationException.class, () -> {
            validator.validate(config);
        });
    }

    @Test
    void testValidate_negativeStart() {
        ProfileValidator validator = new ProfileValidator(new ScenarioFactory());
        Config config = new Config();

        ProfileItem item = new ProfileItem();
        item.setStart(-54);

        List<ProfileItem> profile = new ArrayList<>();
        profile.add(item);
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            validator.validate(config);
        });
    }

    @Test
    void testValidate_badStart() {
        ProfileValidator validator = new ProfileValidator(new ScenarioFactory());
        Config config = new Config();

        ProfileItem item1 = new ProfileItem();
        ProfileItem item2 = new ProfileItem();

        item1.setStart(345);
        item2.setStart(43);

        List<ProfileItem> profile = new ArrayList<>();
        profile.add(item1);
        profile.add(item2);
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            validator.validate(config);
        });
    }

    @Test
    void testValidate_badType() {
        ScenarioFactory factory = mock(ScenarioFactory.class);
        when(factory.check("non-existing")).thenReturn(false);

        ProfileValidator validator = new ProfileValidator(factory);
        Config config = new Config();

        ScenarioItem scenarioItem = new ScenarioItem();
        scenarioItem.setType("non-existing");

        ProfileItem item = new ProfileItem();
        item.setStart(6658);
        item.getScenarios().add(scenarioItem);

        List<ProfileItem> profile = new ArrayList<>();
        profile.add(item);
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            validator.validate(config);
        });
    }

    @Test
    void testValidate_correct() throws ValidationException {
        ScenarioFactory factory = mock(ScenarioFactory.class);
        when(factory.check("existing")).thenReturn(true);

        ProfileValidator validator = new ProfileValidator(factory);
        Config config = new Config();

        ScenarioItem scenarioItem = new ScenarioItem();
        scenarioItem.setType("existing");

        ProfileItem item1 = new ProfileItem();
        ProfileItem item2 = new ProfileItem();

        item1.setStart(45);
        item2.setStart(5684);

        item1.getScenarios().add(scenarioItem);
        item2.getScenarios().add(scenarioItem);

        List<ProfileItem> profile = new ArrayList<>();
        profile.add(item1);
        profile.add(item2);
        config.setProfile(profile);

        validator.validate(config);
        verify(factory, times(2)).check("existing");
    }
}