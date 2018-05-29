package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeBuilderTest {

    @Test
    void testAddSendAction_withoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(action).build();

        ScenarioActionEntry entry = node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddSendAction_withDelay() {
        long delay = 33432;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(delay, action).build();

        ScenarioActionEntry entry = node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(delay, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction(action).build();

        ScenarioActionEntry entry = node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withTimeout() {
        long timeout = 456212;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction(timeout, action).build();

        ScenarioActionEntry entry = node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(timeout, entry.getDelay());
        assertFalse(entry.isSending());
    }

}