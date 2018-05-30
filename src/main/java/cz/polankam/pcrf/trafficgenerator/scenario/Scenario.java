package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.utils.DiameterAppType;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.rx.ClientRxSession;


public abstract class Scenario {

    protected final Random random = new Random();
    protected ScenarioContext context;
    private ScenarioNode currentNode;
    private Queue<ScenarioActionEntry> currentActions;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);
    private final AtomicLong sentCount = new AtomicLong(0);
    private final AtomicLong receivedCount = new AtomicLong(0);


    public void init(GxStack gx, RxStack rx, List<AppRequestEvent> receivedRequests) throws Exception {
        // create sessions
        ClientGxSession gxSession = gx.getSessionFactory().getNewAppSession(GxStack.authAppId, ClientGxSession.class);
        ClientRxSession rxSession = rx.getSessionFactory().getNewAppSession(RxStack.authAppId, ClientRxSession.class);

        // create scenario state and initialize context
        HashMap<String, Object> state = createNewScenarioState();
        context = new ScenarioContext(gx, rx, gxSession, rxSession, receivedRequests, state);

        // initialize current node
        currentNode = getRootNode();
        currentActions = currentNode.getActionsCopy();
    }

    public synchronized void destroy() {
        context.getGxSession().release();
        context.getRxSession().release();
        destroyed.set(true);
    }

    protected ScenarioContext getContext() {
        return context;
    }

    protected ScenarioNode getCurrentNode() {
        return currentNode;
    }

    public boolean isDestroyed() {
        return destroyed.get();
    }

    public synchronized boolean isEmpty() {
        return currentNode == null;
    }

    public ClientGxSession getGxSession() {
        return context.getGxSession();
    }

    public ClientRxSession getRxSession() {
        return context.getRxSession();
    }

    public long getSentCount() {
        return sentCount.get();
    }

    public long getReceivedCount() {
        return receivedCount.get();
    }

    /**
     * If current node is processed, randomly find next one.
     */
    protected synchronized void findNextNode() throws Exception {
        if (!currentActions.isEmpty()) {
            return;
        }

        if (!currentNode.hasChildren()) {
            currentNode = null;
            return;
        }

        int next = random.nextInt(100);
        currentNode = currentNode.getChild(next);
        currentActions = currentNode.getActionsCopy();
    }

    /**
     * Get delay with which next action should be processed.
     * @return
     */
    public synchronized long getNextDelay() {
        if (isEmpty() || isDestroyed() || !currentActions.peek().isSending()) {
            return 0;
        }

        return ((SendScenarioActionEntry) currentActions.peek()).getDelay();
    }

    /**
     * Get timeout with which next action should be received.
     * @return
     */
    public synchronized long getNextTimeout() {
        if (isEmpty() || isDestroyed() || currentActions.peek().isSending()) {
            return 0;
        }

        return ((ReceiveScenarioActionEntry) currentActions.peek()).getTimeout();
    }

    public synchronized boolean isNextSending() {
        if (isEmpty() || isDestroyed()) {
            return false;
        }

        return currentActions.peek().isSending();
    }

    /**
     *
     * @return true if message was sent, false if next action is receive of message
     * @throws java.lang.Exception
     */
    public synchronized boolean sendNext() throws Exception {
        if (isEmpty() || isDestroyed()) {
            return false;
        }

        if (!currentActions.peek().isSending()) {
            return false;
        }

        SendScenarioActionEntry next = (SendScenarioActionEntry) currentActions.peek();
        sentCount.incrementAndGet();
        next.getAction().perform(context, null, null);

        // do not forget to remove successfully sent entry
        currentActions.poll();
        // optionally find next node
        findNextNode();
        return true;
    }

    /**
     *
     * @param request
     * @param answer
     * @throws java.lang.Exception
     */
    public synchronized void receiveNext(AppRequestEvent request, AppAnswerEvent answer, DiameterAppType appType) throws Exception {
        if (isEmpty() || isDestroyed()) {
            return;
        }

        if (currentActions.peek().isSending()) {
            throw new Exception("Next action is sending, but event received");
        }

        ReceiveScenarioActionEntry next = (ReceiveScenarioActionEntry) currentActions.peek();
        receivedCount.incrementAndGet();
        ScenarioAction action;
        if (appType.equals(DiameterAppType.Gx)) {
            action = next.getGxAction();
            next.setGxAction(null);
        } else if (appType.equals(DiameterAppType.Rx)) {
            action = next.getRxAction();
            next.setRxAction(null);
        } else {
            throw new Exception("Unknown application type");
        }

        if (action == null) {
            throw new Exception("Unexpected message received");
        }

        // perform the action
        action.perform(context, request, answer);

        if (next.getGxAction() == null && next.getRxAction() == null) {
            // do not forget to remove entry, if both actions were successful
            currentActions.poll();
            // optionally find next node
            findNextNode();
        }
    }


    /**
     * On every call create new unique scenario state.
     * @return
     */
    protected abstract HashMap<String, Object> createNewScenarioState() throws Exception;

    /**
     * Get root node/entry point for this scenario automaton.
     * @return
     * @throws java.lang.Exception
     */
    protected abstract ScenarioNode getRootNode() throws Exception;

    /**
     * Get type of scenario which will be used in scenario factory.
     * @return
     */
    public abstract String getType();

}
