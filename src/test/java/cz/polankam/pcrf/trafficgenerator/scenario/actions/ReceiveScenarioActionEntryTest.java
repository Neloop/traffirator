package cz.polankam.pcrf.trafficgenerator.scenario.actions;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceiveScenarioActionEntryTest {

    @Test
    void test_withoutTimeout() {
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ReceiveScenarioActionEntry entry = new ReceiveScenarioActionEntry(gxAction, rxAction);

        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void test_withTimeout() {
        long timeout = 511597;
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ReceiveScenarioActionEntry entry = new ReceiveScenarioActionEntry(timeout, gxAction, rxAction);

        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

}