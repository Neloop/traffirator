package cz.polankam.pcrf.trafficgenerator.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdiameter.api.ApplicationId;

/**
 * Implementation of the <code>AppStack</code> base class which is representing the Rx application stack in
 * the application. The path to the configuration file is hardcoded to resources directory to rx-client-config.xml file.
 */
public class RxStack extends AppStack {

    private static final Logger log = LogManager.getLogger(RxStack.class);

    // configuration files
    private static final String configFile = "rx-client-config.xml";
    
    // definition of codes, IDs
    public static final long applicationID = 16777236;
    public static final ApplicationId authAppId = ApplicationId.createByAuthAppId(applicationID);

    
    @Override
    public void initStack() {
        initStack(configFile, authAppId, "Rx");
    }
    
}
