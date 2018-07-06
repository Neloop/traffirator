package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.RxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAARequest;

/**
 * Action which will send AAR request to the Rx interface. This AAR is used on changing the call codec.
 */
public class RxAar_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        RxAARequest req = RxRequestsFactory.createAar(context, true);
        context.getRxSession().sendAARequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
