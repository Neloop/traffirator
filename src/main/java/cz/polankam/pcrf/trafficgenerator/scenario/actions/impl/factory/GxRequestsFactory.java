package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.events.GxCreditControlRequest;
import org.jdiameter.common.impl.app.gx.GxCreditControlRequestImpl;

import java.util.concurrent.ConcurrentHashMap;


public class GxRequestsFactory {

    public static GxCreditControlRequest createCcrI(ScenarioContext context, boolean isFramedIp, boolean isSubscriptionId) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrT(ScenarioContext context, boolean isSubscriptionId) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrU(ScenarioContext context, boolean isSubscriptionId) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrU_LostConnection(ScenarioContext context) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

}
