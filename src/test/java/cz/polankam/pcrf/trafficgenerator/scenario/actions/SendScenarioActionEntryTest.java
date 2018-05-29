package cz.polankam.pcrf.trafficgenerator.scenario.actions;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendScenarioActionEntryTest {

    @Test
    void test_withoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        SendScenarioActionEntry entry = new SendScenarioActionEntry(action);

        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void test_withDelay() {
        long delay = 34566;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        SendScenarioActionEntry entry = new SendScenarioActionEntry(delay, action);

        assertEquals(action, entry.getAction());
        assertEquals(delay, entry.getDelay());
        assertTrue(entry.isSending());
    }

}