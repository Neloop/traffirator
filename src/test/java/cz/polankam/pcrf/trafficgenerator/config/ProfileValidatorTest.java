package cz.polankam.pcrf.trafficgenerator.config;

import cz.polankam.pcrf.trafficgenerator.exceptions.ValidationException;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProfileValidatorTest {

    @Test
    void testValidate_nullProfile() {
        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(new Config());
        });
    }

    @Test
    void testValidate_emptyProfile() {
        Profile profile = new Profile();
        profile.setBurstLimit(345);
        profile.setEnd(678);
        profile.setFlow(new ArrayList<>());

        Config config = new Config();
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_invalidBurstLimit() {
        Profile profile = new Profile();
        profile.setBurstLimit(-82);
        profile.setEnd(346);
        profile.setFlow(Collections.singletonList(new ProfileItem()));

        Config config = new Config();
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_invalidEnd() {
        Profile profile = new Profile();
        profile.setBurstLimit(5462);
        profile.setEnd(-836);
        profile.setFlow(Collections.singletonList(new ProfileItem()));

        Config config = new Config();
        config.setProfile(profile);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_negativeStart() {
        Profile profile = new Profile();
        profile.setBurstLimit(351);
        profile.setEnd(38);

        Config config = new Config();
        config.setProfile(profile);

        ProfileItem item = new ProfileItem();
        item.setStart(-54);

        List<ProfileItem> items = new ArrayList<>();
        items.add(item);
        profile.setFlow(items);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_badStart() {
        Profile profile = new Profile();
        profile.setBurstLimit(27);
        profile.setEnd(927);

        Config config = new Config();
        config.setProfile(profile);

        ProfileItem item1 = new ProfileItem();
        ProfileItem item2 = new ProfileItem();

        item1.setStart(345);
        item2.setStart(43);

        List<ProfileItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        profile.setFlow(items);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_badType() {
        ScenarioFactory factory = mock(ScenarioFactory.class);
        when(factory.check("non-existing")).thenReturn(false);

        Profile profile = new Profile();
        profile.setBurstLimit(937);
        profile.setEnd(354);

        Config config = new Config();
        config.setProfile(profile);

        ScenarioItem scenarioItem = new ScenarioItem();
        scenarioItem.setType("non-existing");

        ProfileItem item = new ProfileItem();
        item.setStart(6658);
        item.getScenarios().add(scenarioItem);

        List<ProfileItem> items = new ArrayList<>();
        items.add(item);
        profile.setFlow(items);

        assertThrows(ValidationException.class, () -> {
            new ProfileValidator(new ScenarioFactory()).validate(config);
        });
    }

    @Test
    void testValidate_correct() throws ValidationException {
        ScenarioFactory factory = mock(ScenarioFactory.class);
        when(factory.check("existing")).thenReturn(true);

        Profile profile = new Profile();
        profile.setBurstLimit(976);
        profile.setEnd(2485);

        Config config = new Config();
        config.setProfile(profile);

        ScenarioItem scenarioItem = new ScenarioItem();
        scenarioItem.setType("existing");

        ProfileItem item1 = new ProfileItem();
        ProfileItem item2 = new ProfileItem();

        item1.setStart(45);
        item2.setStart(5684);

        item1.getScenarios().add(scenarioItem);
        item2.getScenarios().add(scenarioItem);

        List<ProfileItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        profile.setFlow(items);

        new ProfileValidator(factory).validate(config);
        verify(factory, times(2)).check("existing");
    }
}