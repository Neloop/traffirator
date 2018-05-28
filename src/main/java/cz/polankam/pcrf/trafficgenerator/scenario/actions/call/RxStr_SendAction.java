package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.factory.RxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxSessionTermRequest;


public class RxStr_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        RxSessionTermRequest req = RxRequestsFactory.createStr(context);
        context.getRxSession().sendSessionTermRequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
