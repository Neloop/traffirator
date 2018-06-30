package cz.polankam.pcrf.trafficgenerator.scenario;

import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.rx.ClientRxSession;

public interface SessionProvider {
    ClientGxSession createGxSession(ClientGxSession oldSession, Scenario scenario) throws Exception;
    ClientRxSession createRxSession(ClientRxSession oldSession, Scenario scenario) throws Exception;
}
