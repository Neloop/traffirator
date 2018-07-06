package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.GxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlRequest;

/**
 * Action which will send CCR request to the Gx interface. This CCR is used in case of disconnection of the user device.
 */
public class GxCcrT_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        GxCreditControlRequest req = GxRequestsFactory.createCcrT(context);
        context.getGxSession().sendCreditControlRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
