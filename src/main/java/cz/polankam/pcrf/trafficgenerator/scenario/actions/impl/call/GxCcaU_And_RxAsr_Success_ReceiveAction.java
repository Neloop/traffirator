package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;


public class GxCcaU_And_RxAsr_Success_ReceiveAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (!(answer instanceof GxCreditControlAnswer) && !(request instanceof RxAbortSessionRequest)) {
            throw new Exception("Bad app event type");
        }

        if (answer instanceof GxCreditControlAnswer) {
            if (SUCCESS_RESULT_CODE != answer.getMessage().getAvps().getAvp(Avp.RESULT_CODE).getUnsigned32()) {
                throw new Exception("Received error Gx CCA answer");
            }
        } else if (request instanceof RxAbortSessionRequest) {
            // empty
        }
    }

}
