package cz.polankam.pcrf.trafficgenerator.scenario;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;


@FunctionalInterface
public interface ScenarioAction {

    long SUCCESS_RESULT_CODE = 2001;
    long MISSING_AVP_RESULT_CODE = 5005;

    /**
     * Perform this scenario action with given parameters.
     * @param context
     * @param request
     * @param answer
     * @throws Exception
     */
    void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception;
}
