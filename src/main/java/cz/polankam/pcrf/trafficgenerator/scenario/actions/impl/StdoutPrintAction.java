package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;


public class StdoutPrintAction implements ScenarioAction {

    private final String message;


    public StdoutPrintAction(String message) {
        this.message = message;
    }

    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) {
        System.out.println(message);
    }

}
