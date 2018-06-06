package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.utils.AvpUtils;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
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

        AvpSet ruleNameAvp = request.getMessage().getAvps().getAvps(AvpUtils.CHARGING_RULE_NAME);
        if (ruleNameAvp != null && ruleNameAvp.size() == 2) {
            Avp[] ruleNameArr = ruleNameAvp.asArray();
            String firstChargingRuleName = new String(ruleNameArr[0].getOctetString());
            String secondChargingRuleName = new String(ruleNameArr[1].getOctetString());
            context.getState().put("first_charging_rule_name", firstChargingRuleName);
            context.getState().put("second_charging_rule_name", secondChargingRuleName);
        }
    }

}
