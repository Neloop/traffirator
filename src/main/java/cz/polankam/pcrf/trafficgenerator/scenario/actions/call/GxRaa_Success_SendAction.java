package cz.polankam.pcrf.trafficgenerator.scenario.actions.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.utils.DumpUtils;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxReAuthRequest;
import org.jdiameter.common.impl.app.gx.GxReAuthAnswerImpl;


public class GxRaa_Success_SendAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        // find appropriate request
        AppRequestEvent req = null;
        for (AppRequestEvent received : context.getReceivedEvents()) {
            if (received instanceof GxReAuthRequest) {
                req = received;
                context.getReceivedEvents().remove(req);
                break;
            }
        }

        if (req == null) {
            throw new Exception("Request not found for RAAnswer");
        }

        GxReAuthAnswerImpl ans = new GxReAuthAnswerImpl((Request) req.getMessage(), SUCCESS_RESULT_CODE);
        context.getGxSession().sendGxReAuthAnswer(ans);
        DumpUtils.dumpMessage(ans.getMessage(), true);
    }

}
