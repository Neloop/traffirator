package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAAAnswer;


public class RxAaa_Success_ReceiveAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (!(answer instanceof RxAAAnswer)) {
            String answerClassName = answer == null ? "" : answer.getClass().getName();
            String requestClassName = request == null ? "" : request.getClass().getName();
            throw new Exception("Received bad app event, answer '" + answerClassName + "'; request '" + requestClassName + "'");
        }

        if (SUCCESS_RESULT_CODE != answer.getMessage().getAvps().getAvp(Avp.RESULT_CODE).getUnsigned32()) {
            throw new Exception("Received error Rx AAA answer");
        }
    }

}
