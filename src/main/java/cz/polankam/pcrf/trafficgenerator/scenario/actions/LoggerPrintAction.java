package cz.polankam.pcrf.trafficgenerator.scenario.actions;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.apache.log4j.Logger;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;


public class LoggerPrintAction implements ScenarioAction {

    private static final Logger logger = Logger.getLogger(LoggerPrintAction.class);
    
    private final String message;
    
    
    public LoggerPrintAction(String message) {
        this.message = message;
    }
    
    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) throws Exception {
        logger.info(message);
    }
    
}
