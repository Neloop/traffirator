package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;


public class GxRar_And_RxSta_Success_ReceiveAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (!(answer instanceof RxSessionTermAnswer) && !(request instanceof GxReAuthRequest)) {
            throw new Exception("Bad app event type");
        }

        if (answer instanceof RxSessionTermAnswer) {
            if (SUCCESS_RESULT_CODE != answer.getMessage().getAvps().getAvp(Avp.RESULT_CODE).getUnsigned32()) {
                throw new Exception("Received error Rx STA answer");
            }
        } else if (request instanceof GxReAuthRequest) {
            // empty
        }
    }

}
