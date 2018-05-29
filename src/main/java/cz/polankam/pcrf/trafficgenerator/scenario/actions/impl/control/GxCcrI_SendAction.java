package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.GxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlRequest;


public class GxCcrI_SendAction implements ScenarioAction {

    private boolean isFramedIp;
    private boolean isSubscriptionId;

    public GxCcrI_SendAction() {
        this(true, false);
    }

    public GxCcrI_SendAction(boolean isFramedIp) {
        this(isFramedIp, false);
    }

    public GxCcrI_SendAction(boolean isFramedIp, boolean isSubscriptionId) {
        this.isFramedIp = isFramedIp;
        this.isSubscriptionId = isSubscriptionId;
    }


    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        GxCreditControlRequest req = GxRequestsFactory.createCcrI(context, isFramedIp, isSubscriptionId);
        context.getGxSession().sendCreditControlRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
