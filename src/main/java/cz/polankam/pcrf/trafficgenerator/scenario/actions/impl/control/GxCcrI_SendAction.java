package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.GxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlRequest;

/**
 * Action which will send CCR request to the Gx interface. This CCR is used when the user device connects to
 * the network.
 */
public class GxCcrI_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        context.createGxSession(); // initial request, new gx session has to be created
        GxCreditControlRequest req = GxRequestsFactory.createCcrI(context);
        context.getGxSession().sendCreditControlRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
