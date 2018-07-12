package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import cz.polankam.pcrf.trafficgenerator.exceptions.ScenarioException;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ReceiveScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.SendScenarioActionEntry;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.EmptyAction;
import cz.polankam.pcrf.trafficgenerator.utils.DiameterAppType;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * Base class for all scenarios defined within the generator. All scenario has to comply to the interface declared
 * in this class. In addition scenario contains logic for reception and sending of the messages.
 */
public abstract class Scenario {

    protected final Random random = new Random();
    protected ScenarioContext context;
    private long currentNodeDelay = 0;
    private ScenarioNode currentNode;
    private Deque<ScenarioActionEntry> currentNodeActions;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);
    private final AtomicLong sentCount = new AtomicLong(0);
    private final AtomicLong receivedCount = new AtomicLong(0);
    private boolean receivedGx = false;
    private boolean receivedRx = false;


    /**
     * Initializer of the scenario which takes care of all necessary things.
     * The root node is obtained from the actual implementation of scenario.
     * @param sessionProvider provider of the sessions used for their construction
     * @param gx Gx stack instance
     * @param rx Rx stack instance
     * @throws Exception in case of error
     */
    public void init(SessionProvider sessionProvider, GxStack gx, RxStack rx) throws Exception {
        // create scenario state and initialize context
        HashMap<String, Object> state = createNewScenarioState();
        List<AppRequestEvent> receivedRequests = Collections.synchronizedList(new ArrayList<>());
        context = new ScenarioContext(this, sessionProvider, gx, rx, receivedRequests, state);

        // initialize current node
        currentNode = getRootNode();
        currentNodeActions = currentNode.getActionsCopy();
    }

    /**
     * Destroy the scenario, which basically only release the sessions.
     */
    public synchronized void destroy() {
        if (context.getGxSession() != null) {
            context.getGxSession().release();
        }
        if (context.getRxSession() != null) {
            context.getRxSession().release();
        }

        destroyed.set(true);
    }

    /**
     * Get context of this scenario used in actions.
     * @return scenario context
     */
    public ScenarioContext getContext() {
        return context;
    }

    /**
     * Get currently active node in this scenario.
     * @return scenario node
     */
    protected ScenarioNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Determine if this scenario is destroyed or not.
     * @return true if destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return destroyed.get();
    }

    /**
     * Determine if this scenario is empty and do not contain any further actions to be performed.
     * @return true if empty, false otherwise.
     */
    public synchronized boolean isEmpty() {
        return currentNode == null;
    }

    /**
     * Get number of messages sent by this scenario to this moment.
     * @return count of sent messages
     */
    public long getSentCount() {
        return sentCount.get();
    }

    /**
     * Get number of messages received by this scenario to this moment.
     * @return count of received messages
     */
    public long getReceivedCount() {
        return receivedCount.get();
    }

    /**
     * If current node is processed and do not contain any further actions, find next node suitable for processing.
     * The process of finding next node is random and is based on the probabilities of the transitions
     * to children nodes.
     */
    protected synchronized void findNextNode() throws Exception {
        if (!currentNodeActions.isEmpty()) {
            return;
        }

        if (!currentNode.hasChildren()) {
            currentNode = null;
            return;
        }

        int next = random.nextInt(100);
        ScenarioNodeEntry nextNode = currentNode.getChild(next);
        currentNode = nextNode.getNode();
        currentNodeDelay = nextNode.getAverageDelay();
        currentNodeActions = currentNode.getActionsCopy();

        // the very first action of the node is receiving, but there is specified delay on the first action of node,
        // this cannot be done on receiving action...
        // resolve this by adding empty "sending" action as the first action in the current node actions
        if (!currentNodeActions.isEmpty() && !currentNodeActions.peek().isSending() && currentNodeDelay != 0) {
            currentNodeActions.addFirst(new SendScenarioActionEntry(new EmptyAction()));
        }
    }

    /**
     * Get delay with which next action should be processed.
     * @return next delay, zero if there is no delay
     */
    public synchronized long getNextDelay() {
        if (isEmpty() || isDestroyed() || !currentNodeActions.peek().isSending()) {
            return 0;
        }

        int variability = getDelaysVariability();
        long averageDelay = currentNodeDelay + ((SendScenarioActionEntry) currentNodeActions.peek()).getAverageDelay();
        long randomPartDelay = 0;
        if (variability != 0 && averageDelay != 0) {
            randomPartDelay = random.nextLong() % (averageDelay * variability / 100);
        }

        // we used delay of the current node and it is not needed any further
        currentNodeDelay = 0;
        return averageDelay + randomPartDelay;
    }

    /**
     * Get timeout with which next action should be received.
     * @return timeout, if zero, there is no timeouts
     */
    public synchronized long getNextTimeout() {
        if (isEmpty() || isDestroyed() || currentNodeActions.peek().isSending()) {
            return 0;
        }

        return ((ReceiveScenarioActionEntry) currentNodeActions.peek()).getTimeout();
    }

    /**
     * Determine if the next action is sending or not.
     * @return true if next is sending, false otherwise
     */
    public synchronized boolean isNextSending() {
        if (isEmpty() || isDestroyed()) {
            return false;
        }

        return currentNodeActions.peek().isSending();
    }

    /**
     * Perform next sending action in the processing queue. If there is no action or the scenario was destroyed or
     * next action is not sending, than nothing is done, only false returned. After sending of the action next node
     * which should be processed is found.
     * @return true if message was sent, false if next action is receive of message or the scenario is empty
     * @throws Exception in case of any error
     */
    public synchronized boolean sendNext() throws Exception {
        if (isEmpty() || isDestroyed()) {
            return false;
        }

        if (!currentNodeActions.peek().isSending()) {
            return false;
        }

        // set receival flag to false... just to be sure
        receivedGx = false;
        receivedRx = false;

        SendScenarioActionEntry next = (SendScenarioActionEntry) currentNodeActions.peek();
        sentCount.incrementAndGet();
        next.getAction().perform(context, null, null);

        // do not forget to remove successfully sent entry
        currentNodeActions.poll();
        // optionally find next node
        findNextNode();
        return true;
    }

    /**
     * Receive given request on answer with the action which is next in the processing queue.
     * If the next action is sending, it is considered an error and exception is thrown.
     * This method also takes care of reception both of the messages form Gx and Rx interface if needed.
     * After reception of the action, next node which should be processed is found.
     * @param request incoming request, can be null
     * @param answer incoming answer, can be null
     * @param appType application stack identifier which receive the message
     * @throws Exception in case of any error
     */
    public synchronized void receiveNext(AppRequestEvent request, AppAnswerEvent answer, DiameterAppType appType) throws Exception {
        if (isEmpty() || isDestroyed()) {
            return;
        }

        if (currentNodeActions.peek().isSending()) {
            String eventClassName = getEventNames(request, answer);
            throw new ScenarioException("Next action is sending, but event '" + eventClassName + "' received");
        }

        ReceiveScenarioActionEntry next = (ReceiveScenarioActionEntry) currentNodeActions.peek();
        receivedCount.incrementAndGet();
        ScenarioAction action;
        if (appType.equals(DiameterAppType.Gx)) {
            action = next.getGxAction();
            receivedGx = true;
        } else if (appType.equals(DiameterAppType.Rx)) {
            action = next.getRxAction();
            receivedRx = true;
        } else {
            throw new ScenarioException("Unknown application type");
        }

        if (action == null) {
            String eventClassName = getEventNames(request, answer);
            throw new ScenarioException("Unexpected message '" + eventClassName + "' received");
        }

        // perform the action
        action.perform(context, request, answer);

        if (receivedGx && receivedRx ||
                next.getGxAction() == null && receivedRx ||
                next.getRxAction() == null && receivedGx) {
            receivedGx = false;
            receivedRx = false;

            // do not forget to remove entry, if both actions were successful
            currentNodeActions.poll();
            // optionally find next node
            findNextNode();
        }
    }

    /**
     * Debugging function for getting current scenario, node and action name.
     * @return textual representation of current state
     */
    public synchronized String getCurrentStateName() {
        String nodeName = currentNode != null ? currentNode.getName() : "null";
        String actionName = currentNodeActions.peek() != null ? currentNodeActions.peek().getName() : "null";
        return getType() + "_" + nodeName + "_" + actionName;
    }

    /**
     * Internal function which constructs human readable name of the received events.
     * @param request received request, can be null
     * @param answer received answer, can be null
     * @return textual representation of given events
     */
    private String getEventNames(AppRequestEvent request, AppAnswerEvent answer) {
        String eventClassName = "";
        if (request != null) {
            eventClassName = request.getClass().getCanonicalName();
        }
        if (answer != null) {
            eventClassName = (eventClassName.isEmpty() ? "" : ";") + answer.getClass().getCanonicalName();
        }
        return eventClassName;
    }

    /**
     * On every call create new unique initial scenario state. In here some of the attributes which are needed
     * in actions can be constructed and initialized.
     * @return associative array containing scenario state
     */
    protected abstract HashMap<String, Object> createNewScenarioState() throws Exception;

    /**
     * Get root node/entry point for this scenario automaton.
     * The actual implementation of the scenario should contain some sort of caching of the scenario nodes.
     * This function should not create new scenario graph at every call.
     * @return scenario root node
     * @throws Exception in case of any error
     */
    protected abstract ScenarioNode getRootNode() throws Exception;

    /**
     * Get type of this scenario which will be used in scenario factory.
     * @return textual identifier of the scenario
     */
    public abstract String getType();

    /**
     * Delays are hardcoded, so we need some mechanism to have them variable. Returned value is in percents and says
     * how much delay can be lowered or made higher from its original value.
     * @return integer from the range of zero and hundred
     */
    public abstract int getDelaysVariability();

}
