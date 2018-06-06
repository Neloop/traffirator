package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory.RxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAARequest;


public class RxAar_SendAction implements ScenarioAction {

    private final String[] codecData;


    public RxAar_SendAction(String[] codecData) {
        this.codecData = codecData;
    }

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        RxAARequest req = RxRequestsFactory.createAar(context, codecData);
        context.getRxSession().sendAARequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
