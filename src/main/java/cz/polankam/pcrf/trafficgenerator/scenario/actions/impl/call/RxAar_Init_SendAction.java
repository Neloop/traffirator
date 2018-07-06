package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.RxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAARequest;

/**
 * Action which will send AAR request to the Rx interface. This AAR is used on the call initiation and requests base
 * bearer for the call.
 */
public class RxAar_Init_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        context.createRxSession(); // initial request, new rx session has to be created
        RxAARequest req = RxRequestsFactory.createAar(context, true);
        context.getRxSession().sendAARequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
