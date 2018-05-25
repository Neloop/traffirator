package cz.polankam.pcrf.trafficgenerator.scenario;

import cz.polankam.pcrf.trafficgenerator.client.GxStack;
import cz.polankam.pcrf.trafficgenerator.client.RxStack;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.rx.ClientRxSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ScenarioContext {
    
    private final GxStack gx;
    private final RxStack rx;
    
    private final ClientGxSession gxSession;
    private final ClientRxSession rxSession;

    private final List<AppRequestEvent> receivedEvents;
    
    private final ConcurrentHashMap<String, Object> state;
    
    
    public ScenarioContext(GxStack gx, RxStack rx, ClientGxSession gxSession, ClientRxSession rxSession,
            List<AppRequestEvent> receivedEvents, Map<String, Object> state) {
        this.gx = gx;
        this.rx = rx;
        this.gxSession = gxSession;
        this.rxSession = rxSession;
        this.receivedEvents = receivedEvents;
        
        this.state = new ConcurrentHashMap<>();
        this.state.putAll(state);
    }
    
    public GxStack getGxStack() {
        return gx;
    }
    
    public RxStack getRxStack() {
        return rx;
    }
    
    public ClientGxSession getGxSession() {
        return gxSession;
    }
    
    public ClientRxSession getRxSession() {
        return rxSession;
    }
    
    public String getGxRealm() {
        return gx.getRealm();
    }
    
    public String getRxRealm() {
        return rx.getRealm();
    }
    
    public String getGxServerUri() {
        return gx.getServerUri();
    }
    
    public String getRxServerUri() {
        return rx.getServerUri();
    }
    
    public List<AppRequestEvent> getReceivedEvents() {
        return receivedEvents;
    }
    
    public ConcurrentHashMap<String, Object> getState() {
        return state;
    }
    
}
