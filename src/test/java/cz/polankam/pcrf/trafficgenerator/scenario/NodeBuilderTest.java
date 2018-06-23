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
    void testAddSendAction_withNameWithoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction("actionTestName", action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(0, entry.getAverageDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddSendAction_withNameWithDelay() {
        long delay = 33432;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction("actionTestName", delay, action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(delay, entry.getAverageDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddSendAction_withoutDelay() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals("", entry.getName());
        assertEquals(0, entry.getAverageDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddSendAction_withDelay() {
        long delay = 33432;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addSendAction(delay, action).build();

        SendScenarioActionEntry entry = (SendScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getAction());
        assertEquals("", entry.getName());
        assertEquals(delay, entry.getAverageDelay());
        assertTrue(entry.isSending());
    }

    @Test
    void testAddReceiveGxAction_withNameWithoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveGxAction("actionTestName", action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getGxAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(null, entry.getRxAction());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveGxAction_withNameWithTimeout() {
        long timeout = 456212;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveGxAction("actionTestName", timeout, action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getGxAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(null, entry.getRxAction());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveGxAction_withoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveGxAction(action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(action, entry.getGxAction());
        assertEquals(null, entry.getRxAction());
        assertEquals("", entry.getName());
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
        assertEquals("", entry.getName());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveRxAction_withNameWithoutTimeout() {
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveRxAction("actionTestName", action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(null, entry.getGxAction());
        assertEquals(action, entry.getRxAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveRxAction_withNameWithTimeout() {
        long timeout = 4682578;
        ScenarioAction action = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveRxAction("actionTestName", timeout, action).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(null, entry.getGxAction());
        assertEquals(action, entry.getRxAction());
        assertEquals("actionTestName", entry.getName());
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
        assertEquals("", entry.getName());
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
        assertEquals("", entry.getName());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withNameWithoutTimeout() {
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction("actionTestName", gxAction, rxAction).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals("actionTestName", entry.getName());
        assertEquals(0, entry.getTimeout());
        assertFalse(entry.isSending());
    }

    @Test
    void testAddReceiveAction_withNameWithTimeout() {
        long timeout = 9864974;
        ScenarioAction gxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioAction rxAction = (ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) -> {};
        ScenarioNode node = new NodeBuilder().addReceiveAction("actionTestName", timeout, gxAction, rxAction).build();

        ReceiveScenarioActionEntry entry = (ReceiveScenarioActionEntry) node.getActionsCopy().peek();
        assertEquals(gxAction, entry.getGxAction());
        assertEquals(rxAction, entry.getRxAction());
        assertEquals("actionTestName", entry.getName());
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
        assertEquals("", entry.getName());
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
        assertEquals("", entry.getName());
        assertEquals(timeout, entry.getTimeout());
        assertFalse(entry.isSending());
    }

}