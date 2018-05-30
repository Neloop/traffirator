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
    private final String[] firstFlowDesc;
    private final String[] secondFlowDesc;


    public RxAar_SendAction(String[] codecData, String[] firstFlowDesc, String[] secondFlowDesc) {
        this.codecData = codecData;
        this.firstFlowDesc = firstFlowDesc;
        this.secondFlowDesc = secondFlowDesc;
    }

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        RxAARequest req = RxRequestsFactory.createAar(context, codecData, firstFlowDesc, secondFlowDesc);
        context.getRxSession().sendAARequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
