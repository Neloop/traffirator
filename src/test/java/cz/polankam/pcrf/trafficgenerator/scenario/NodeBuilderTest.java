package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeBuilderTest {

    @Test
    void testAddSendAction_withoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(0, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddSendAction_withDelay() {
        long delay = 33432;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(delay, action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals(delay, entry.getDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddReceiveGxAction_withoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveGxAction(action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getGxAction());
        assertEquals(null, entry.getRxAction());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveGxAction_withTimeout() {
        long timeout = 456212;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveGxAction(timeout, action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getGxAction());
        assertEquals(null, entry.getRxAction());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveRxAction_withoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveRxAction(action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(null, entry.getGxAction());
        assertEquals(action, entry.getRxAction());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveRxAction_withTimeout() {
        long timeout = 4682578;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveRxAction(timeout, action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(null, entry.getGxAction());
        assertEquals(action, entry.getRxAction());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withoutTimeout() {
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction(gxAction, rxAction).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withTimeout() {
        long timeout = 9864974;
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction(timeout, gxAction, rxAction).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

}