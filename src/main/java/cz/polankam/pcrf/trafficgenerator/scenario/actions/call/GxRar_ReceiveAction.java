package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxReAuthRequest;


public class GxRar_ReceiveAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (!(request instanceof GxReAuthRequest)) {
            String answerClassName = answer == null ? "" : answer.getClass().getName();
            String requestClassName = request == null ? "" : request.getClass().getName();
            throw new Exception("Received bad app event, answer '" + answerClassName + "'; request '" + requestClassName + "'");
        }
    }

}
