package cz.polankam.pcrf.trafficgenerator.client;

import cz.polankam.pcrf.trafficgenerator.config.Config;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import cz.polankam.pcrf.trafficgenerator.rx.MyRxSessionFactoryImpl;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;

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
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.common.impl.app.gx.GxSessionFactoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Client implements ClientRxSessionListener, ClientGxSessionListener {

    private static final Logger log = Logger.getLogger(Client.class);

    private final GxStack gx;
    private final RxStack rx;
    private final Config config;
    private final ScenarioFactory scenarioFactory;
    private final ScheduledExecutorService executorService;
    private int scenariosCount;
    private final List<Scenario> scenariosList;
    private final Map<String, Scenario> scenariosMap;
    private final Map<String, List<AppRequestEvent>> scenariosReceivedRequestsMap;
    /**
     * Boolean telling if we finished our interaction.
     */
    private final AtomicBoolean finished;
    /**
     * Remaining call count of scenarios.
     */
    private int callCount;


    public Client(Config config) throws Exception {
        this.config = config;
        this.scenarioFactory = new ScenarioFactory();

        gx = new GxStack();
        rx = new RxStack();
        executorService = Executors.newScheduledThreadPool(config.getThreadCount());
        scenariosReceivedRequestsMap = new HashMap<>();
        scenariosList = new ArrayList<>();
        scenariosMap = new HashMap<>();
        finished = new AtomicBoolean(false);

        callCount = config.getCallCount();
        scenariosCount = config.getInitialScenariosCount();
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

    public synchronized void init() throws Exception {
        gx.initStack();
        rx.initStack();

        // prepare session factories
        prepareSessionFactories();

        // create scenarios
        for (int i = 0; i < config.getInitialScenariosCount(); i++) {
            createScenario();
        }
    }

    public synchronized void destroy() throws Exception {
        executorService.shutdownNow();
        for (Scenario scen : scenariosList) {
            scen.destroy();
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

    public synchronized void start() throws Exception {
        for (Scenario scenario : scenariosList) {
            sendNextMessage(scenario);
        }
    }

    public synchronized int getScenariosCount() {
        return scenariosList.size();
    }

    public synchronized void setScenariosCount(int count) {
        if (scenariosCount == count) {
            return;
        }

        if (scenariosCount < count) {
            // add scenarios
            int diff = count - scenariosCount;
            for (int i = 0; i < diff; ++i) {
                try {
                    createAndStartScenario();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        } else {
            // delete scenarios
            int diff = scenariosCount - count;
            for (int i = 0; i < diff; ++i) {
                removeRandomScenario();
            }
        }

        scenariosCount = count;
    }

    private synchronized boolean canCreateScenario() {
        if (callCount == -1) {
            return true;
        }

        if ((callCount - scenariosList.size()) > 0) {
            return true;
        }

        return false;
    }

    private synchronized boolean areScenariosEmpty() {
        return scenariosList.isEmpty();
    }

    private synchronized Scenario createAndStartScenario() throws Exception {
        Scenario scenario = createScenario();
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
            log.error(e);
            finish();
            return null;
        }

        scenariosReceivedRequestsMap.put(scenario.getGxSession().getSessionId(), receivedRequests);
        scenariosReceivedRequestsMap.put(scenario.getRxSession().getSessionId(), receivedRequests);

        scenariosList.add(scenario);
        scenariosMap.put(scenario.getGxSession().getSessionId(), scenario);
        scenariosMap.put(scenario.getRxSession().getSessionId(), scenario);

        log.info("New scenario created. Current active scenarios: " + scenariosList.size());

        if (callCount != -1) {
            callCount--;
        }
        return scenario;
    }

    private synchronized void removeRandomScenario() {
        if (scenariosList.isEmpty()) {
            return;
        }

        removeScenario(scenariosList.get(0));
    }

    private synchronized void removeScenario(Scenario scenario) {
        scenariosList.remove(scenario);
        scenariosMap.remove(scenario.getGxSession().getSessionId());
        scenariosMap.remove(scenario.getRxSession().getSessionId());
        scenariosReceivedRequestsMap.remove(scenario.getGxSession().getSessionId());
        scenariosReceivedRequestsMap.remove(scenario.getRxSession().getSessionId());
        scenario.destroy();
        log.info("Scenario removed and destroyed. Current active scenarios: " + scenariosList.size());
    }

    private synchronized void handleFailure(final Scenario scenario, String errorMessage) {
        if (scenario == null) {
            return;
        }

        log.error(errorMessage);

        removeScenario(scenario);
        if (canCreateScenario()) {
            log.info("Scenario failed, load next");

            // send next message of newly created scenario
            sendNextMessage(createScenario(scenario.getType()));
        } else {
            finish();
            return;
        }
    }

    private synchronized void sendNextMessage(final Scenario scenario) {
        if (scenario == null || finished() || scenario.isDestroyed()) {
            return;
        }

        long delay = scenario.getNextDelay();
        if (delay != 0) {
            log.info("Next send delayed of " + delay + " ms");
        }

        // schedule sending of next message of given scenario with appropriate delay
        executorService.schedule(() -> {
            boolean sent = false;
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

                if (!sent && scenario.isEmpty()) { // message was not send, so check if scenario is not empty
                    // delete scenario from all internal structures
                    removeScenario(scenario);

                    if (canCreateScenario()) {
                        log.info("Scenario empty, load next");

                        // send next message of newly created scenario
                        sendNextMessage(createScenario(scenario.getType()));
                    } else if (areScenariosEmpty()) {
                        // call count reached zero and there are no more scenarios, end whole execution...
                        log.info("Scenarios empty, finishing...");
                        finish();
                        return;
                    }
                } else if (sent) { // message was sent, check if there are others in queue
                    sendNextMessage(scenario);
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

            Scenario scenario = null;
            synchronized (this) {
                if (answer == null) {
                    List<AppRequestEvent> receivedRequests = scenariosReceivedRequestsMap.get(session.getSessionId());
                    if (receivedRequests == null) {
                        // probably because session was already released, just warn the user and continue
                        log.warn("Received requests list not found for given session '" + session.getSessionId() + "'");
                        return;
                    }
                    // request arrived, add it to received requests
                    receivedRequests.add(request);
                }

                scenario = scenariosMap.get(session.getSessionId());
                if (scenario == null) {
                    // probably because session was already released, just warn the user and continue
                    log.warn("Session '" + session.getSessionId() + "' not found in scenarios map.");
                    return;
                }

                if (scenario.isDestroyed()) {
                    return;
                }
            }

            try {
                scenario.receiveNext(request, answer);
                log.info("Incoming message processed");

                sendNextMessage(scenario); // after receiving message, immediatelly send next one
                return;
            } catch (Exception e) {
                handleFailure(scenario, e.getMessage());
                return;
            }
        });
    }


    @Override
    public void doAAAnswer(ClientRxSession session, RxAARequest request, RxAAAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(answer.getMessage(), false);
        processIncoming(session, request, answer);
    }

    @Override
    public void doReAuthRequest(ClientRxSession session, RxReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        DumpUtils.dumpMessage(request.getMessage(), false);
        log.error("Unexpected message");
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
        log.error("Unexpected message");
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
