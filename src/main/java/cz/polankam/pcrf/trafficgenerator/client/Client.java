package cz.polankam.pcrf.trafficgenerator.client;

import cz.polankam.pcrf.trafficgenerator.rx.MyRxSessionFactoryImpl;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DiameterAppType;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.apache.log4j.Logger;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.ClientGxSessionListener;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.api.gx.events.GxCreditControlRequest;
import org.jdiameter.api.gx.events.GxReAuthRequest;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.rx.events.*;
import org.jdiameter.common.impl.app.gx.GxSessionFactoryImpl;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import cz.polankam.pcrf.trafficgenerator.scenario.SessionProvider;

/**
 * Client is the main class which manages the reception and sending of the Diameter messages. It implements
 * listener interfaces provided by the jDiameter and contains instances of Gx and Rx stacks, which are used for sending
 * of messages. All actions taken by the client should be scheduled to the given executor service to avoid
 * synchronization issues.
 * @note By now the client class is very much in need of refactoring.
 */
public class Client implements ClientRxSessionListener, ClientGxSessionListener, SessionProvider {

    private static final Logger logger = Logger.getLogger(Client.class);

    private final GxStack gx;
    private final RxStack rx;
    private final ScenarioFactory scenarioFactory;
    private final ScheduledExecutorService executorService;

    /** Key is type of the scenario, value is number of currently running scenarios with that type. */
    private final Map<String, Integer> scenarioTypesCount;
    /** Key is type of the scenario, value is list of the scenarios belonging to that type. */
    private final Map<String, List<Scenario>> scenariosForTypes;
    /** Key is session identification, value is scenario for that session. */
    private final Map<String, Scenario> scenariosForSessions;
    private final Map<String, List<AppRequestEvent>> scenariosReceivedRequestsMap;

    /** Number of timeouts from the beginning of the execution */
    private final AtomicLong timeoutsCount;
    /** Number of sent messages from the beginning of the execution */
    private final AtomicLong sentCount;
    /** Number of received messages from the beginning of the execution */
    private final AtomicLong receivedCount;
    /** Number of failures from the beginning of the execution */
    private final AtomicLong failuresCount;
    /** Current number of active scenarios */
    private final AtomicLong currentScenariosCount;

    /** Boolean telling if we finished our interaction. */
    private final AtomicBoolean finished;
    private String finishedReason;


    /**
     * Constructor.
     * @param executor scheduler
     * @param factory scenario factory
     */
    public Client(ScheduledExecutorService executor, ScenarioFactory factory) {
        gx = new GxStack();
        rx = new RxStack();
        executorService = executor;
        scenarioFactory = factory;
        scenariosReceivedRequestsMap = new HashMap<>();
        scenariosForTypes = new HashMap<>();
        scenariosForSessions = new HashMap<>();
        finished = new AtomicBoolean(false);
        finishedReason = "OK";
        timeoutsCount = new AtomicLong(0);
        sentCount = new AtomicLong(0);
        receivedCount = new AtomicLong(0);
        failuresCount = new AtomicLong(0);
        currentScenariosCount = new AtomicLong(0);
        scenarioTypesCount = new HashMap<>();
    }

    /**
     * Get and prepare session factories for the Gx and Rx interfaces. Also register them to the jDiameter stacks.
     */
    private void prepareSessionFactories() {
        // Gx session factory
        GxSessionFactoryImpl gxSessionFactory = new GxSessionFactoryImpl(gx.getSessionFactory());
        gxSessionFactory.setClientSessionListener(this);
        gx.getSessionFactory().registerAppFacory(ClientGxSession.class, gxSessionFactory);

        // Rx session factory
        MyRxSessionFactoryImpl rxSessionFactory = new MyRxSessionFactoryImpl(rx.getSessionFactory());
        rxSessionFactory.setClientSessionListener(this);
        rx.getSessionFactory().registerAppFacory(ClientRxSession.class, rxSessionFactory);
    }

    /**
     * Initialize the client by initializing the Diameter stacks and preparing the session factories.
     */
    public synchronized void init() {
        gx.initStack();
        rx.initStack();

        // prepare session factories
        prepareSessionFactories();
    }

    /**
     * Shutdown executor service and destroy all currently active scenarios. Gx and Rx stacks are also properly
     * destroyed.
     * @throws Exception in case of error
     */
    public synchronized void destroy() throws Exception {
        executorService.shutdownNow();
        for (String scenarioType : scenariosForTypes.keySet()) {
            List<Scenario> scenariosList = scenariosForTypes.get(scenarioType);
            for (Scenario scen : scenariosList) {
                scen.destroy();
            }
        }
        gx.destroy();
        rx.destroy();
    }

    /**
     * Request the end of the execution.
     */
    public void finish() {
        finished.set(true);
    }

    /**
     * Request the end of the execution with given reason.
     * @param reason reason why the execution was stopped
     */
    public synchronized void finish(String reason) {
        finished.set(true);
        finishedReason = reason;
    }

    /**
     * Determine if the client was finished or not.
     * @return true if finished, false otherwise
     */
    public boolean finished() {
        return finished.get();
    }

    /**
     * Get finished reason, default is "OK".
     * @return textual status
     */
    public String getFinishedReason() {
        return finishedReason;
    }

    /**
     * Get current count of the timeouts from the beginning of the execution.
     * @return count of scenarios which timeout
     */
    public long getTimeoutsCount() {
        return timeoutsCount.get();
    }

    /**
     * Get current count of the sent messages from the beginning of the execution.
     * @return number of sent messages
     */
    public long getSentCount() {
        return sentCount.get();
    }

    /**
     * Get current count of the received messages from the beginning of the execution.
     * @return number of received messages
     */
    public long getReceivedCount() {
        return receivedCount.get();
    }

    /**
     * Get current count of the scenarios, which failed, from the beginning of the execution.
     * @return number of failures
     */
    public long getFailuresCount() {
        return failuresCount.get();
    }

    /**
     * Get current count of the scenarios.
     * @return number of scenarios
     */
    public long getScenariosCount() {
        return currentScenariosCount.get();
    }

    /**
     * Change the number of active scenarios of given type. The count should be applied after given delay.
     * If the number is higher than the current one, new scenarios are spawned, otherwise random active scenarios are
     * ended.
     * @param type type of the scenarios which should be managed
     * @param count number of the scenarios for given type
     * @param delay delay of the change, in seconds
     */
    public void controlScenarios(String type, int count, int delay) {
        executorService.schedule(() -> {
            synchronized (this) {
                int scenariosCount = scenarioTypesCount.getOrDefault(type, 0);
                if (scenariosCount == count) {
                    return;
                }

                if (scenariosCount < count) {
                    // add scenarios
                    int diff = count - scenariosCount;
                    for (int i = 0; i < diff; ++i) {
                        createAndStartScenario(type);
                    }
                } else {
                    // delete scenarios
                    int diff = scenariosCount - count;
                    for (int i = 0; i < diff; ++i) {
                        removeRandomScenario(type);
                    }
                }

                scenarioTypesCount.put(type, count);
            }
        }, delay, TimeUnit.SECONDS);
    }

    /**
     * Create scenario of given type and start it.
     * @param type type of the scenario
     */
    private synchronized void createAndStartScenario(String type) {
        Scenario scenario = createScenario(type);
        sendNextMessage(scenario);
    }

    /**
     * Create scenario of given types using scenario factory, but do not start it.
     * Scenario is added to all needed internal structures.
     * @param type type of the scenario
     * @return newly created scenario.
     */
    private synchronized Scenario createScenario(String type) {
        Scenario scenario;
        try {
            scenario = scenarioFactory.create(type);
            scenario.init(this, gx, rx);
        } catch (Exception e) {
            // should not happen, in case it will, stop whole execution
            logger.error(e);
            finish(e.getMessage());
            return null;
        }

        // if there was no scenario for given type, create an empty list
        if (!scenariosForTypes.containsKey(type)) {
            scenariosForTypes.put(type, new ArrayList<>());
        }

        scenariosForTypes.get(type).add(scenario);
        currentScenariosCount.incrementAndGet();
        logger.info("New scenario created. Current active scenarios for type '" + type + "': " + scenariosForTypes.get(type).size());

        return scenario;
    }

    /**
     * Remove random scenario of given type. For the time being the random means the first one in the scenarios list.
     * @param type type of the scenario
     */
    private synchronized void removeRandomScenario(String type) {
        if (scenariosForTypes.get(type).isEmpty()) {
            return;
        }

        while (!scenariosForTypes.get(type).isEmpty()) {
            if (removeScenario(scenariosForTypes.get(type).get(0))) {
                return;
            }
        }
    }

    /**
     * Remove given scenario from all structures within this class and destroy it properly.
     * @param scenario scenario to be removed
     * @return if the scenario was remove or not
     */
    private synchronized boolean removeScenario(Scenario scenario) {
        String type = scenario.getType();
        boolean result = false;
        if (scenariosForTypes.get(type).remove(scenario)) {
            currentScenariosCount.decrementAndGet();
            result = true;
        }

        ClientGxSession gxSession = scenario.getContext().getGxSession();
        if (gxSession != null) {
            scenariosForSessions.remove(gxSession.getSessionId());
            scenariosReceivedRequestsMap.remove(gxSession.getSessionId());
        }

        ClientRxSession rxSession = scenario.getContext().getRxSession();
        if (rxSession != null) {
            scenariosForSessions.remove(rxSession.getSessionId());
            scenariosReceivedRequestsMap.remove(rxSession.getSessionId());
        }

        scenario.destroy();
        logger.info("Scenario removed and destroyed. Current active scenarios for type '" + type + "': " + scenariosForTypes.get(type).size());
        return result;
    }

    /**
     * Register given session regardless of application into the internal client structures.
     * @param oldSession old session which is replaced by the new one
     * @param newSession newly created session
     * @param scenario scenario connected to the session
     */
    private synchronized void registerNewSession(AppSession oldSession, AppSession newSession, Scenario scenario) {
        if (oldSession != null) {
            scenariosForSessions.remove(oldSession.getSessionId());
            scenariosReceivedRequestsMap.remove(oldSession.getSessionId());
        }

        scenariosReceivedRequestsMap.put(newSession.getSessionId(), scenario.getContext().getReceivedEvents());
        scenariosForSessions.put(newSession.getSessionId(), scenario);
    }

    @Override
    public ClientGxSession createGxSession(ClientGxSession oldSession, Scenario scenario) throws Exception {
        // scheduled in the executor, because if not, the deadlock might be possible,
        // if this method is called from within the scenario action
        ClientGxSession gxSession = gx.getSessionFactory().getNewAppSession(GxStack.authAppId, ClientGxSession.class);
        executorService.submit(() -> {
            registerNewSession(oldSession, gxSession, scenario);
        });
        return gxSession;
    }

    @Override
    public ClientRxSession createRxSession(ClientRxSession oldSession, Scenario scenario) throws Exception {
        // scheduled in the executor, because if not, the deadlock might be possible,
        // if this method is called from within the scenario action
        ClientRxSession rxSession = rx.getSessionFactory().getNewAppSession(RxStack.authAppId, ClientRxSession.class);
        executorService.submit(() -> {
            registerNewSession(oldSession, rxSession, scenario);
        });
        return rxSession;
    }

    /**
     * Handle failure of the given scenario, optionally exception or error message can be provided as a reason of
     * failure. Failure is handled by destroying the scenario and spawning a new one.
     * @param scenario failed scenario
     * @param ex optionally given exception connected to failure
     * @param errorMessage error message which represents failure reason
     */
    private void handleFailure(final Scenario scenario, Exception ex, String errorMessage) {
        if (scenario == null) {
            return;
        }

        logger.error(errorMessage, ex);
        boolean wasDestroyed;
        synchronized (this) {
            wasDestroyed = scenario.isDestroyed();
            removeScenario(scenario);
        }

        if (!wasDestroyed) {
            failuresCount.incrementAndGet();
            // scenario was not yet destroyed, so create the next one
            logger.info("Scenario failed in state '" + scenario.getCurrentStateName() + "', creating next one");
            // send next message of newly created scenario
            sendNextMessage(createScenario(scenario.getType()));
        } else {
            logger.info("Scenario already destroyed, noop");
        }
    }

    /**
     * Send next message from the given scenario if the scenario is not destroyed. The sending is scheduled with the
     * delay from the sending message. After successful send, next message is sent if there is one, otherwise
     * the scenario waits for incoming messages or if the scenario is empty, then the new scenario is spawned.
     * @param scenario scenario which will be processed
     */
    private void sendNextMessage(final Scenario scenario) {
        if (scenario == null || finished() || scenario.isDestroyed()) {
            return;
        }

        long delay = scenario.getNextDelay();
        if (delay != 0) {
            logger.debug("Next send delayed of " + delay + " ms");
        }

        // schedule sending of next message of given scenario with appropriate delay
        executorService.schedule(() -> {
            boolean sent;
            try {
                sent = scenario.sendNext();
                sentCount.incrementAndGet();
            } catch (Exception e) {
                handleFailure(scenario, e, e.getMessage());
                return;
            }

            boolean isNextSending;
            boolean isScenarioEmpty;
            synchronized (this) {
                if (scenario.isDestroyed()) {
                    return;
                }

                isNextSending = scenario.isNextSending();
                isScenarioEmpty = scenario.isEmpty();
                if (isScenarioEmpty) {
                    removeScenario(scenario);
                }
            }

            if (isScenarioEmpty) {
                logger.debug("Scenario '" + scenario.getCurrentStateName() + "' empty, loading next");
                // send next message of newly created scenario
                sendNextMessage(createScenario(scenario.getType()));
            } else if (sent && isNextSending) {
                // message was sent, check if there are others in queue
                sendNextMessage(scenario);
            } else if (!isNextSending) {
                // next one is receiving, schedule the timeout guard
                processTimeout(scenario);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Process incoming message, processing is scheduled in scheduler and synchronized within.
     * Whole process method cannot be synchronized, because jdiameter is holding its own lock locked during processing
     * of incoming message. Thus locking this method would lead to deadlock in some situations.
     * @param session session connected to the received message
     * @param request if the request was received
     * @param answer if the answer was received
     */
    private void processIncoming(AppSession session, AppRequestEvent request, AppAnswerEvent answer, DiameterAppType appType) {
        executorService.execute(() -> {
            if (finished()) {
                return;
            }

            Scenario scenario;
            synchronized (this) {
                if (answer == null) {
                    List<AppRequestEvent> receivedRequests = scenariosReceivedRequestsMap.get(session.getSessionId());
                    if (receivedRequests == null) {
                        // probably because session was already released, just warn the user and continue
                        logger.warn("Received requests list not found for given session '" + session.getSessionId() + "'");
                        return;
                    }
                    // request arrived, add it to received requests
                    receivedRequests.add(request);
                }

                scenario = scenariosForSessions.get(session.getSessionId());
                if (scenario == null) {
                    // probably because session was already released, just warn the user and continue
                    logger.warn("Session '" + session.getSessionId() + "' not found in scenarios map.");
                    return;
                }

                if (scenario.isDestroyed()) {
                    return;
                }
            }

            try {
                scenario.receiveNext(request, answer, appType);
                receivedCount.incrementAndGet();
            } catch (Exception e) {
                handleFailure(scenario, e, e.getMessage());
                return;
            }

            if (scenario.isNextSending()) {
                // after receiving message, immediately send next one
                sendNextMessage(scenario);
            } else {
                // next one is receiving, schedule the timeout guard
                processTimeout(scenario);
            }
        });
    }

    /**
     * Schedule the processing of the timeout for the given scenario. If message was received in the timeout period,
     * the timeout is not processed at all, if the message was not received, the scenario is marked as timeouted and
     * handled as a failed one.
     * @param scenario scenario to be processed
     */
    private void processTimeout(Scenario scenario) {
        if (scenario == null || finished() || scenario.isDestroyed()) {
            return;
        }

        long receivedCount = scenario.getReceivedCount();
        long timeout = scenario.getNextTimeout();
        if (timeout == 0) {
            // timeout is not set, ignore it and do not schedule timeout handler
            logger.debug("Timeout not set on scenario receive action");
            return;
        }

        logger.debug("Next receive has timeout of " + timeout + " ms");
        executorService.schedule(() -> {
            if (scenario.getReceivedCount() > receivedCount) {
                // the message was received in the meantime, do not engage
                logger.debug("Timeout action not performed, message was received before timeout");
                return;
            }

            // message was not received in time, handle it with care
            timeoutsCount.incrementAndGet();
            handleFailure(scenario, null, "Scenario timed out in " + timeout + " ms");
        }, timeout, TimeUnit.MILLISECONDS);
    }


    @Override
    public void doAAAnswer(ClientRxSession session, RxAARequest request, RxAAAnswer answer) throws InternalException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer, DiameterAppType.Rx);
    }

    @Override
    public void doReAuthRequest(ClientRxSession session, RxReAuthRequest request) throws InternalException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        logger.error("Unexpected message");
        finish("Unexpected message");
    }

    @Override
    public void doSessionTermAnswer(ClientRxSession session, RxSessionTermRequest request, RxSessionTermAnswer answer) throws InternalException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer, DiameterAppType.Rx);
    }

    @Override
    public void doAbortSessionRequest(ClientRxSession session, RxAbortSessionRequest request) throws InternalException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        processIncoming(session, request, null, DiameterAppType.Rx);
    }

    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) {
        logger.error("Unexpected message");
        finish("Unexpected message");
    }

    @Override
    public void doCreditControlAnswer(ClientGxSession session, GxCreditControlRequest request, GxCreditControlAnswer answer) throws InternalException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer, DiameterAppType.Gx);
    }

    @Override
    public void doGxReAuthRequest(ClientGxSession session, GxReAuthRequest request) throws InternalException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        processIncoming(session, request, null, DiameterAppType.Gx);
    }

    @Override
    public int getDefaultDDFHValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDefaultCCFHValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
