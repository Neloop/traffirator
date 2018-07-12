package cz.polankam.pcrf.trafficgenerator.scenario;

import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.rx.ClientRxSession;

/**
 * Classes which implements this interface has to be session providers. That means, they manage the sessions and
 * have to be able to construct session needed by some other parts of the generator.
 */
public interface SessionProvider {

    /**
     * Create Gx session based on given scenario and old session.
     * @param oldSession old session which should be unregistered from the provider and destroyed
     * @param scenario scenario instance
     * @return newly created Gx session
     * @throws Exception in case of creation error
     */
    ClientGxSession createGxSession(ClientGxSession oldSession, Scenario scenario) throws Exception;

    /**
     * Create Rx session based on given scenario and old session.
     * @param oldSession old session which should be unregistered from the provider and destroyed
     * @param scenario scenario instance
     * @return newly created Rx session
     * @throws Exception in case of creation error
     */
    ClientRxSession createRxSession(ClientRxSession oldSession, Scenario scenario) throws Exception;
}
