package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.factory.RxRequestsFactory;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAARequest;


public class RxAar_SendAction implements ScenarioAction {

    private final int bandwidth;
    private final String[] codecData;
    private final String[] firstFlowDesc;
    private final String[] secondFlowDesc;


    public RxAar_SendAction(int bandwidth, String[] codecData, String[] firstFlowDesc, String[] secondFlowDesc) {
        this.bandwidth = bandwidth;
        this.codecData = codecData;
        this.firstFlowDesc = firstFlowDesc;
        this.secondFlowDesc = secondFlowDesc;
    }

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        RxAARequest req = RxRequestsFactory.createAar(context, bandwidth, codecData, firstFlowDesc, secondFlowDesc);
        context.getRxSession().sendAARequest(req);
        DumpUtils.dumpMessage(req.getMessage(), true);
    }

}
