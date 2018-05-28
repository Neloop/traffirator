package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.common.impl.app.rx.RxAbortSessionAnswerImpl;


public class RxAsa_Success_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        // find appropriate request
        AppRequestEvent req = null;
        for (AppRequestEvent received : context.getReceivedEvents()) {
            if (received instanceof RxAbortSessionRequest) {
                req = received;
                context.getReceivedEvents().remove(req);
                break;
            }
        }

        if (req == null) {
            throw new Exception("Request not found for ASAnswer");
        }

        RxAbortSessionAnswerImpl ans = new RxAbortSessionAnswerImpl((Request) req.getMessage(), SUCCESS_RESULT_CODE);
        context.getRxSession().sendAbortSessionAnswer(ans);
        DumpUtils.dumpMessage(ans.getMessage(), true);
    }

}
