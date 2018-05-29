package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.GxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlRequest;


public class GxCcrT_SendAction implements ScenarioAction {

    private boolean isWrongSession;
    private boolean isSubscriptionId;

    public GxCcrT_SendAction() {
        this(false, false);
    }

    public GxCcrT_SendAction(boolean isWrongSession) {
        this(isWrongSession, false);
    }

    public GxCcrT_SendAction(boolean isWrongSession, boolean isSubscriptionId) {
        this.isWrongSession = isWrongSession;
        this.isSubscriptionId = isSubscriptionId;
    }


    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        GxCreditControlRequest req = GxRequestsFactory.createCcrT(context, isSubscriptionId);
        context.getGxSession().sendCreditControlRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
