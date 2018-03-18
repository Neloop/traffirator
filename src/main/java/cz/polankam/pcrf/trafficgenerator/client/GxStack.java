package cz.polankam.pcrf.trafficgenerator.client;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;


public class GxStack extends AppStack {

    private static final Logger log = Logger.getLogger(GxStack.class);

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
