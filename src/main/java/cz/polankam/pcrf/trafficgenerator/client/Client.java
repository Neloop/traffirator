package cz.polankam.pcrf.trafficgenerator.client;

import cz.polankam.pcrf.trafficgenerator.rx.MyRxSessionFactoryImpl;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.apache.log4j.Logger;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
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


public class Client implements ClientRxSessionListener, ClientGxSessionListener {

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

    /** Boolean telling if we finished our interaction. */
    private final AtomicBoolean finished;


    public Client(ScheduledExecutorService executor) {
        this.scenarioFactory = new ScenarioFactory();

        gx = new GxStack();
        rx = new RxStack();
        executorService = executor;
        scenariosReceivedRequestsMap = new HashMap<>();
        scenariosForTypes = new HashMap<>();
        scenariosForSessions = new HashMap<>();
        finished = new AtomicBoolean(false);
        timeoutsCount = new AtomicLong(0);
        scenarioTypesCount = new HashMap<>();
    }

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

    public synchronized void init() {
        gx.initStack();
        rx.initStack();

        // prepare session factories
        prepareSessionFactories();
    }

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

    public void finish() {
        finished.set(true);
    }

    public boolean finished() {
        return finished.get();
    }

    public long getTimeoutsCount() {
        return timeoutsCount.get();
    }

    public synchronized void controlScenarios(String type, int count) {
        int scenariosCount = scenarioTypesCount.getOrDefault(type, 0);
        if (scenariosCount == count) {
            return;
        }

        if (scenariosCount < count) {
            // add scenarios
            int diff = count - scenariosCount;
            for (int i = 0; i < diff; ++i) {
                try {
                    createAndStartScenario(type);
                } catch (Exception e) {
                    logger.error(e);
                }
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

    private synchronized Scenario createAndStartScenario(String type) throws Exception {
        Scenario scenario = createScenario(type);
        sendNextMessage(scenario);
        return scenario;
    }

    private synchronized Scenario createScenario(String type) {
        Scenario scenario = null;
        List<AppRequestEvent> receivedRequests = Collections.synchronizedList(new ArrayList<>());

        try {
            scenario = scenarioFactory.create(type);
            scenario.init(gx, rx, receivedRequests);
        } catch (Exception e) {
            // should not happen, in case it will, stop whole execution
            logger.error(e);
            finish();
            return null;
        }

        scenariosReceivedRequestsMap.put(scenario.getGxSession().getSessionId(), receivedRequests);
        scenariosReceivedRequestsMap.put(scenario.getRxSession().getSessionId(), receivedRequests);

        // if there was no scenario for given type, create an empty list
        if (!scenariosForTypes.containsKey(type)) {
            scenariosForTypes.put(type, new ArrayList<>());
        }

        scenariosForTypes.get(type).add(scenario);
        scenariosForSessions.put(scenario.getGxSession().getSessionId(), scenario);
        scenariosForSessions.put(scenario.getRxSession().getSessionId(), scenario);

        logger.info("New scenario created. Current active scenarios for type '" + type + "': " + scenariosForTypes.get(type).size());

        return scenario;
    }

    private synchronized void removeRandomScenario(String type) {
        if (scenariosForTypes.get(type).isEmpty()) {
            return;
        }

        removeScenario(scenariosForTypes.get(type).get(0));
    }

    private synchronized void removeScenario(Scenario scenario) {
        String type = scenario.getType();
        scenariosForTypes.get(type).remove(scenario);
        scenariosForSessions.remove(scenario.getGxSession().getSessionId());
        scenariosForSessions.remove(scenario.getRxSession().getSessionId());
        scenariosReceivedRequestsMap.remove(scenario.getGxSession().getSessionId());
        scenariosReceivedRequestsMap.remove(scenario.getRxSession().getSessionId());
        scenario.destroy();
        logger.info("Scenario removed and destroyed. Current active scenarios for type '" + type + "': " + scenariosForTypes.get(type).size());
    }

    private synchronized void handleFailure(final Scenario scenario, String errorMessage) {
        if (scenario == null) {
            return;
        }

        logger.error(errorMessage);
        removeScenario(scenario);
        logger.info("Scenario failed, creating next one");
        // send next message of newly created scenario
        sendNextMessage(createScenario(scenario.getType()));
    }

    private synchronized void sendNextMessage(final Scenario scenario) {
        if (scenario == null || finished() || scenario.isDestroyed()) {
            return;
        }

        long delay = scenario.getNextDelay();
        if (delay != 0) {
            logger.info("Next send delayed of " + delay + " ms");
        }

        // schedule sending of next message of given scenario with appropriate delay
        executorService.schedule(() -> {
            boolean sent;
            try {
                sent = scenario.sendNext();
            } catch (Exception e) {
                handleFailure(scenario, e.getMessage());
                return;
            }

            synchronized (this) {
                if (scenario.isDestroyed()) {
                    return;
                }

                boolean isNextSending = scenario.isNextSending();
                if (!sent && scenario.isEmpty()) { // message was not send, so check if scenario is not empty
                    // delete scenario from all internal structures
                    removeScenario(scenario);

                    logger.info("Scenario empty, loading next");
                    // send next message of newly created scenario
                    sendNextMessage(createScenario(scenario.getType()));
                } else if (sent && isNextSending) {
                    // message was sent, check if there are others in queue
                    sendNextMessage(scenario);
                } else if (!isNextSending) {
                    // next one is receiving, schedule the timeout guard
                    processTimeout(scenario);
                }
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Whole process method cannot be synchronized, because jdiameter is holding its own lock locked during processing
     * of incomings message. Thus locking this method would lead to deadlock in some situations.
     * @param session
     * @param request
     * @param answer
     */
    private void processIncoming(AppSession session, AppRequestEvent request, AppAnswerEvent answer) {
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
                scenario.receiveNext(request, answer);
                logger.info("Incoming message processed");
            } catch (Exception e) {
                handleFailure(scenario, e.getMessage());
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

    private void processTimeout(Scenario scenario) {
        if (scenario == null || finished() || scenario.isDestroyed()) {
            return;
        }

        long receivedCount = scenario.getReceivedCount();
        long timeout = scenario.getNextDelay();
        if (timeout != 0) {
            logger.info("Next receive has timeout of " + timeout + " ms");
        }

        if (timeout == 0) {
            // timeout is not set, ignore it and do not schedule timeout handler
            logger.debug("Timeout not set on scenario receive action");
            return;
        }

        executorService.schedule(() -> {
            if (scenario.getReceivedCount() > receivedCount) {
                // the message was received in the meantime, do not engage
                logger.debug("Timeout action not performed, messages was received before timeout");
                return;
            }

            // message was not received in time, handle it with care
            timeoutsCount.incrementAndGet();
            handleFailure(scenario, "Scenario timed out for '" + timeout + "'");
        }, timeout, TimeUnit.MILLISECONDS);
    }


    @Override
    public void doAAAnswer(ClientRxSession session, RxAARequest request, RxAAAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer);
    }

    @Override
    public void doReAuthRequest(ClientRxSession session, RxReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        logger.error("Unexpected message");
        finish();
    }

    @Override
    public void doSessionTermAnswer(ClientRxSession session, RxSessionTermRequest request, RxSessionTermAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer);
    }

    @Override
    public void doAbortSessionRequest(ClientRxSession session, RxAbortSessionRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        processIncoming(session, request, null);
    }

    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        logger.error("Unexpected message");
        finish();
    }

    @Override
    public void doCreditControlAnswer(ClientGxSession session, GxCreditControlRequest request, GxCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer);
    }

    @Override
    public void doGxReAuthRequest(ClientGxSession session, GxReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        processIncoming(session, request, null);
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
