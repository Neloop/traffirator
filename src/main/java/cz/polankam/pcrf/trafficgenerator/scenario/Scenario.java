package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
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


    public void init(GxStack gx, RxStack rx, List<AppRequestEvent> receivedRequests) throws Exception {
        // create sessions
        ClientGxSession gxSession = gx.getSessionFactory().getNewAppSession(GxStack.authAppId, ClientGxSession.class);
        ClientRxSession rxSession = rx.getSessionFactory().getNewAppSession(RxStack.authAppId, ClientRxSession.class);

        // create scenario state and initalize context
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

    /**
     * If current node is processed, randomly find next one.
     */
    private synchronized void findNextNode() throws Exception {
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
        if (isEmpty() || isDestroyed()) {
            return 0;
        }

        ScenarioActionEntry next = currentActions.peek();
        if (!next.isSending()) {
            return 0;
        }

        return next.getDelay();
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

        ScenarioActionEntry next = currentActions.peek();
        if (!next.isSending()) {
            return false;
        }

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
    public synchronized void receiveNext(AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (isEmpty() || isDestroyed()) {
            return;
        }

        ScenarioActionEntry next = currentActions.peek();
        if (next.isSending()) {
            throw new Exception("Next action is sending, but event received");
        }

        next.getAction().perform(context, request, answer);

        // do not forget to remove successfully received entry
        currentActions.poll();
        // optionally find next node
        findNextNode();
    }


    /**
     * On every call create new unique scenario state.
     * @return
     * @throws java.lang.Exception
     */
    public abstract HashMap<String, Object> createNewScenarioState() throws Exception;

    /**
     * Get root node/entry point for this scenario automaton.
     * @return
     * @throws java.lang.Exception
     */
    public abstract ScenarioNode getRootNode() throws Exception;

    /**
     * Get type of scenario which will be used in scenario factory.
     * @return
     */
    public abstract String getType();

}
