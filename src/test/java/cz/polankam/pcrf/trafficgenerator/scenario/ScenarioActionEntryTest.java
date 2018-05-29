package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioActionEntryTest {

    @Test
    void test_sendingWithoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioActionEntry entry = new ScenarioActionEntry(true, action);

        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void test_sendingWithDelay() {
        long delay = 34566;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioActionEntry entry = new ScenarioActionEntry(true, delay, action);

        assertEquals(action, entry.getAction());
        assertEquals(delay, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void test_receivingWithoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioActionEntry entry = new ScenarioActionEntry(false, action);

        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertFalse(entry.isSending());
    }

    @Test
    void test_receivingWithDelay() {
        long delay = 511597;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioActionEntry entry = new ScenarioActionEntry(false, delay, action);

        assertEquals(action, entry.getAction());
        assertEquals(delay, entry.getDelay());
        assertFalse(entry.isSending());
    }

}