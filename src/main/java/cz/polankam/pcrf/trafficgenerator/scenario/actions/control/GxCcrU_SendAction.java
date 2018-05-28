package cz.polankam.pcrf.trafficgenerator.scenario.actions.control;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.factory.GxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlRequest;


public class GxCcrU_SendAction implements ScenarioAction {

    private final boolean isSubscriptionId;

    public GxCcrU_SendAction() {
        this(false);
    }

    public GxCcrU_SendAction(boolean isSubscriptionId) {
        this.isSubscriptionId = isSubscriptionId;
    }


    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        GxCreditControlRequest req = GxRequestsFactory.createCcrU(context, isSubscriptionId);
        context.getGxSession().sendCreditControlRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
