package cz.polankam.pcrf.trafficgenerator.scenario.actions;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * Represents single scenario action, which are performed in defined order. Can be sending or receiving action, or any
 * other action which somehow process the messages.
 */
@FunctionalInterface
public interface ScenarioAction {

    long SUCCESS_RESULT_CODE = 2001;
    long MISSING_AVP_RESULT_CODE = 5005;

    /**
     * Perform this scenario action with given parameters.
     * @param context scenario context
     * @param request request message which should be processed, null if answer should be processed
     * @param answer answer message which should be processed, null if request should be processed
     * @throws Exception in case of error
     */
    void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception;
}
