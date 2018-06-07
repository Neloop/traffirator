package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.test.utils.ScenarioMock;
import cz.polankam.pcrf.trafficgenerator.utils.DiameterAppType;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScenarioTest {

    private ScenarioMock scenario;
    private HashMap<String, Object> state;
    private ScenarioNode rootNode;
    private ScenarioNode childNode;
    private GxStack gxStack;
    private RxStack rxStack;
    private SessionCreator sessionCreator;

    @BeforeEach
    void setUp() throws Exception {
        gxStack = mock(GxStack.class);
        rxStack = mock(RxStack.class);

        sessionCreator = mock(SessionCreator.class);

        state = new HashMap<>();
        state.put("stateItem", "stateValue");
        rootNode = new ScenarioNode();
        childNode = new ScenarioNode();

        rootNode.addChild(childNode).validateProbabilities();

        scenario = new ScenarioMock();
        scenario.setState(state);
        scenario.setRootNode(rootNode);
        scenario.setDelayVariability(0);
    }

    @Test
    void testInit() throws Exception {
        scenario.init(sessionCreator, gxStack, rxStack);

        ScenarioContext context = scenario.getContext();
        assertEquals(gxStack, context.getGxStack());
        assertEquals(rxStack, context.getRxStack());
        assertNotNull(context.getReceivedEvents());
        assertEquals(state, context.getState());
        assertEquals(rootNode, scenario.getCurrentNode());
        assertFalse(scenario.isEmpty());
        assertFalse(scenario.isDestroyed());
        assertEquals(0, scenario.getSentCount());
        assertEquals(0, scenario.getReceivedCount());
    }

    @Test
    void testDestroy() throws Exception {
        scenario.init(sessionCreator, gxStack, rxStack);
        scenario.destroy();

        assertTrue(scenario.isDestroyed());
    }

    @Test
    void testFindNextNode_notEmptyActions() throws Exception {
        rootNode.addAction(new SendScenarioActionEntry(mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);

        scenario.findNextNode();
        assertEquals(rootNode, scenario.getCurrentNode());
        assertFalse(scenario.isEmpty());
    }

    @Test
    void testFindNextNode_emptyChildren() throws Exception {
        scenario.setRootNode(new ScenarioNode());
        scenario.init(sessionCreator, gxStack, rxStack);

        scenario.findNextNode();
        assertNull(scenario.getCurrentNode());
        assertTrue(scenario.isEmpty());
    }

    @Test
    void testFindNextNode_correct() throws Exception {
        scenario.init(sessionCreator, gxStack, rxStack);

        scenario.findNextNode();
        assertEquals(childNode, scenario.getCurrentNode());
        assertFalse(scenario.isEmpty());
    }

    @Test
    void testGetNextDelay() throws Exception {
        rootNode.addAction(new SendScenarioActionEntry(5783, mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);

        assertEquals(5783, scenario.getNextDelay());
        assertEquals(0, scenario.getNextTimeout());
    }

    @Test
    void testGetNextDelay_variable() throws Exception {
        scenario.setDelayVariability(10);
        rootNode.addAction(new SendScenarioActionEntry(100, mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);

        long delay = scenario.getNextDelay();
        System.out.println(delay);
        assertTrue(delay >= 90);
        assertTrue(delay <= 110);
        assertEquals(0, scenario.getNextTimeout());
    }

    @Test
    void testGetNextTimeout() throws Exception {
        rootNode.addAction(new ReceiveScenarioActionEntry(88567, mock(ScenarioAction.class), mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);

        assertEquals(0, scenario.getNextDelay());
        assertEquals(88567, scenario.getNextTimeout());
    }

    @Test
    void testIsNextSending_bad() throws Exception {
        rootNode.addAction(new ReceiveScenarioActionEntry(mock(ScenarioAction.class), mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);
        assertFalse(scenario.isNextSending());
    }

    @Test
    void testIsNextSending_correct() throws Exception {
        rootNode.addAction(new SendScenarioActionEntry(mock(ScenarioAction.class)));
        scenario.init(sessionCreator, gxStack, rxStack);
        assertTrue(scenario.isNextSending());
    }

    @Test
    void testSendNext_nextSending() throws Exception {
        ScenarioAction action = mock(ScenarioAction.class);
        rootNode.addAction(new SendScenarioActionEntry(action));
        scenario.init(sessionCreator, gxStack, rxStack);

        assertTrue(scenario.sendNext());
        assertEquals(1, scenario.getSentCount());
        assertEquals(childNode, scenario.getCurrentNode());

        verify(action).perform(scenario.getContext(), null, null);
    }

    @Test
    void testSendNext_nextReceiving() throws Exception {
        rootNode.addAction(new ReceiveScenarioActionEntry(null, null));
        scenario.init(sessionCreator, gxStack, rxStack);
        assertFalse(scenario.sendNext());
    }

    @Test
    void testReceiveNext_nextSending() throws Exception {
        rootNode.addAction(new SendScenarioActionEntry(null));
        scenario.init(sessionCreator, gxStack, rxStack);
        assertThrows(Exception.class, () -> {
            scenario.receiveNext(null, null, null);
        });
    }

    @Test
    void testReceiveNext_onlyGx() throws Exception {
        ScenarioAction action = mock(ScenarioAction.class);
        ReceiveScenarioActionEntry actionEntry = new ReceiveScenarioActionEntry(action, null);
        rootNode.addAction(actionEntry);
        scenario.init(sessionCreator, gxStack, rxStack);

        AppRequestEvent requestEvent = mock(AppRequestEvent.class);
        AppAnswerEvent answerEvent = mock(AppAnswerEvent.class);

        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Gx);
        assertEquals(1, scenario.getReceivedCount());
        assertEquals(childNode, scenario.getCurrentNode());
        assertNotNull(actionEntry.getGxAction());
        assertNull(actionEntry.getRxAction());

        verify(action).perform(scenario.getContext(), requestEvent, answerEvent);
    }

    @Test
    void testReceiveNext_onlyRx() throws Exception {
        ScenarioAction action = mock(ScenarioAction.class);
        ReceiveScenarioActionEntry actionEntry = new ReceiveScenarioActionEntry(null, action);
        rootNode.addAction(actionEntry);
        scenario.init(sessionCreator, gxStack, rxStack);

        AppRequestEvent requestEvent = mock(AppRequestEvent.class);
        AppAnswerEvent answerEvent = mock(AppAnswerEvent.class);

        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Rx);
        assertEquals(1, scenario.getReceivedCount());
        assertEquals(childNode, scenario.getCurrentNode());
        assertNull(actionEntry.getGxAction());
        assertNotNull(actionEntry.getRxAction());

        verify(action).perform(scenario.getContext(), requestEvent, answerEvent);
    }

    @Test
    void testReceiveNext_bothInAction_GxReceived() throws Exception {
        ScenarioAction gxAction = mock(ScenarioAction.class);
        ScenarioAction rxAction = mock(ScenarioAction.class);
        ReceiveScenarioActionEntry actionEntry = new ReceiveScenarioActionEntry(gxAction, rxAction);
        rootNode.addAction(actionEntry);
        scenario.init(sessionCreator, gxStack, rxStack);

        AppRequestEvent requestEvent = mock(AppRequestEvent.class);
        AppAnswerEvent answerEvent = mock(AppAnswerEvent.class);

        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Gx);
        assertEquals(1, scenario.getReceivedCount());
        assertEquals(rootNode, scenario.getCurrentNode());
        assertEquals(gxAction, actionEntry.getGxAction());
        assertEquals(rxAction, actionEntry.getRxAction());

        verifyZeroInteractions(rxAction);
        verify(gxAction).perform(scenario.getContext(), requestEvent, answerEvent);
    }

    @Test
    void testReceiveNext_bothInAction_RxReceived() throws Exception {
        ScenarioAction gxAction = mock(ScenarioAction.class);
        ScenarioAction rxAction = mock(ScenarioAction.class);
        ReceiveScenarioActionEntry actionEntry = new ReceiveScenarioActionEntry(gxAction, rxAction);
        rootNode.addAction(actionEntry);
        scenario.init(sessionCreator, gxStack, rxStack);

        AppRequestEvent requestEvent = mock(AppRequestEvent.class);
        AppAnswerEvent answerEvent = mock(AppAnswerEvent.class);

        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Rx);
        assertEquals(1, scenario.getReceivedCount());
        assertEquals(rootNode, scenario.getCurrentNode());
        assertEquals(gxAction, actionEntry.getGxAction());
        assertEquals(rxAction, actionEntry.getRxAction());

        verifyZeroInteractions(gxAction);
        verify(rxAction).perform(scenario.getContext(), requestEvent, answerEvent);
    }

    @Test
    void testReceiveNext_bothInAction_bothReceived() throws Exception {
        ScenarioAction gxAction = mock(ScenarioAction.class);
        ScenarioAction rxAction = mock(ScenarioAction.class);
        ReceiveScenarioActionEntry actionEntry = new ReceiveScenarioActionEntry(gxAction, rxAction);
        rootNode.addAction(actionEntry);
        scenario.init(sessionCreator, gxStack, rxStack);

        AppRequestEvent requestEvent = mock(AppRequestEvent.class);
        AppAnswerEvent answerEvent = mock(AppAnswerEvent.class);

        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Rx);
        scenario.receiveNext(requestEvent, answerEvent, DiameterAppType.Gx);

        assertEquals(2, scenario.getReceivedCount());
        assertEquals(childNode, scenario.getCurrentNode());
        assertEquals(gxAction, actionEntry.getGxAction());
        assertEquals(rxAction, actionEntry.getRxAction());

        verify(gxAction).perform(scenario.getContext(), requestEvent, answerEvent);
        verify(rxAction).perform(scenario.getContext(), requestEvent, answerEvent);
    }
}