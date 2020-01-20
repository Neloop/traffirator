package cz.polankam.pcrf.trafficgenerator.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdiameter.api.ApplicationId;

/**
 * Implementation of the <code>AppStack</code> base class which is representing the Gx application stack in
 * the application. The path to the configuration file is hardcoded to resources directory to gx-client-config.xml file.
 */
public class GxStack extends AppStack {

    private static final Logger log = LogManager.getLogger(GxStack.class);

    // configuration files
    private static final String configFile = "gx-client-config.xml";
    
    // definition of codes, IDs
    public static final long applicationID = 16777238;
    public static final ApplicationId authAppId = ApplicationId.createByAuthAppId(applicationID);


    @Override
    public void initStack() {
        initStack(configFile, authAppId, "Gx");
    }

}
