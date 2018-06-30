package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl;

import cz.polankam.pcrf.trafficgenerator.scenario.actions.ScenarioAction;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import org.apache.log4j.Logger;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * Action which writes to the log on the debug level.
 */
public class LoggerPrintAction implements ScenarioAction {

    private static final Logger logger = Logger.getLogger(LoggerPrintAction.class);
    
    private final String message;


    /**
     * Constructor.
     * @param message message which will be printed in the log
     */
    public LoggerPrintAction(String message) {
        this.message = message;
    }
    
    @Override
    public void perform(ScenarioContext context, AppRequestEvent request, AppAnswerEvent answer) {
        logger.debug(message);
    }
}
