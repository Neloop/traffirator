package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.common.impl.app.rx.RxAARequestImpl;
import org.jdiameter.common.impl.app.rx.RxSessionTermRequestImpl;

import java.util.concurrent.ConcurrentHashMap;


public class RxRequestsFactory {

    public static RxAARequest createAar(ScenarioContext context, int bandwidth, String[] codecData, String[] firstFlowDesc, String[] secondFlowDesc) throws Exception {
        ClientRxSession session = context.getRxSession();
        ConcurrentHashMap<String, Object> state = context.getState();

        // *** CREATE REQUEST

        RxAARequestImpl req = new RxAARequestImpl(session, context.getRxRealm(), context.getRxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

    public static RxSessionTermRequest createStr(ScenarioContext context) throws Exception {
        ClientRxSession session = context.getRxSession();
        ConcurrentHashMap<String, Object> state = context.getState();

        // *** CREATE REQUEST

        RxSessionTermRequestImpl req = new RxSessionTermRequestImpl(session, context.getRxRealm(), context.getRxServerUri());
        AvpSet avps = req.getMessage().getAvps();
        avps.addAvp(Avp.TERMINATION_CAUSE, 1, true, false);

        // *** RETURN REQUEST

        return req;
    }

}
