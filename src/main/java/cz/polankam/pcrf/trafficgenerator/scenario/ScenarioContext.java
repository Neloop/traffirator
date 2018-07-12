package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.rx.ClientRxSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scenario context is context which is handed over to every action performed within a scenario. It contains useful
 * structures or helpers, which might be used by the action. Most important ones are helpers for creation of sessions
 * and scenario states to which actions can store various data.
 */
public class ScenarioContext {

    private final Scenario scenario;
    private final GxStack gx;
    private final RxStack rx;

    private final SessionProvider sessionProvider;
    private ClientGxSession gxSession;
    private ClientRxSession rxSession;

    private final List<AppRequestEvent> receivedEvents;
    private final ConcurrentHashMap<String, Object> state;


    /**
     * Constructor.
     * @param scenario scenario associated with this context
     * @param sessionProvider session creation provider
     * @param gx gx stack instance
     * @param rx rx stack instance
     * @param receivedEvents events received by this scenario
     * @param state scenario state
     */
    public ScenarioContext(Scenario scenario, SessionProvider sessionProvider, GxStack gx, RxStack rx,
                           List<AppRequestEvent> receivedEvents, Map<String, Object> state) {
        this.gx = gx;
        this.rx = rx;
        this.scenario = scenario;
        this.sessionProvider = sessionProvider;
        this.receivedEvents = receivedEvents;

        this.state = new ConcurrentHashMap<>();
        this.state.putAll(state);
    }

    /**
     * Get Gx stack instance.
     * @return stack
     */
    public GxStack getGxStack() {
        return gx;
    }

    /**
     * Get Rx stack instance.
     * @return stack
     */
    public RxStack getRxStack() {
        return rx;
    }

    /**
     * Get session associated with Gx interface.
     * @return session object
     */
    public ClientGxSession getGxSession() {
        return gxSession;
    }

    /**
     * Create new Gx session, store it in context and return it.
     * @return newly created session
     * @throws Exception in case of session creation problems
     */
    public ClientGxSession createGxSession() throws Exception {
        return gxSession = sessionProvider.createGxSession(gxSession, scenario);
    }

    /**
     * Get session associated with Rx interface.
     * @return session object
     */
    public ClientRxSession getRxSession() {
        return rxSession;
    }

    /**
     * Create new Rx session, store it in context and return it.
     * @return newly created session
     * @throws Exception in case of session creation problems
     */
    public ClientRxSession createRxSession() throws Exception {
        return rxSession = sessionProvider.createRxSession(rxSession, scenario);
    }

    /**
     * Get realm associated with Gx interface.
     * @return textual realm
     */
    public String getGxRealm() {
        return gx.getRealm();
    }

    /**
     * Get realm associated with Rx interface.
     * @return textual realm
     */
    public String getRxRealm() {
        return rx.getRealm();
    }

    /**
     * Get Gx server address identification.
     * @return textual URI
     */
    public String getGxServerUri() {
        return gx.getServerUri();
    }

    /**
     * Get Rx server address identification.
     * @return textual URI
     */
    public String getRxServerUri() {
        return rx.getServerUri();
    }

    /**
     * Get all received and not processed events, which might be used to construct the answers.
     * @return list of received events
     */
    public List<AppRequestEvent> getReceivedEvents() {
        return receivedEvents;
    }

    /**
     * Get scenario state, it is simply an associative array which contains textual identifiers and various objects
     * associated with them.
     * @return map with textual identifier and object as a value
     */
    public ConcurrentHashMap<String, Object> getState() {
        return state;
    }

}
