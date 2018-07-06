package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.utils.AvpCode;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.gx.events.GxReAuthRequest;

/**
 * Action which will received RAR request and saves the charging rules sent back by the PCRF.
 */
public class GxRar_ReceiveAction implements ScenarioAction {

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        if (!(request instanceof GxReAuthRequest)) {
            String answerClassName = answer == null ? "" : answer.getClass().getName();
            String requestClassName = request == null ? "" : request.getClass().getName();
            throw new Exception("Received bad app event, answer '" + answerClassName + "'; request '" + requestClassName + "'");
        }

        Avp ruleInstall = request.getMessage().getAvps().getAvp(AvpCode.CHARGING_RULE_INSTALL);
        if (ruleInstall != null) {
            AvpSet rulesDefinition = ruleInstall.getGrouped();
            if (rulesDefinition != null && rulesDefinition.size() == 2) {
                Avp[] rulesDefinitionArr = rulesDefinition.asArray();
                String firstChargingRuleName = new String(rulesDefinitionArr[0].getGrouped().getAvp(AvpCode.CHARGING_RULE_NAME).getOctetString());
                String secondChargingRuleName = new String(rulesDefinitionArr[1].getGrouped().getAvp(AvpCode.CHARGING_RULE_NAME).getOctetString());
                context.getState().put("first_charging_rule_name", firstChargingRuleName);
                context.getState().put("second_charging_rule_name", secondChargingRuleName);
            }
        }
    }

}
