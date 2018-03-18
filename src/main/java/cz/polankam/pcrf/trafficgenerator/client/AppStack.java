package cz.polankam.pcrf.trafficgenerator.client;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Network;
import org.jdiameter.api.Request;
import org.jdiameter.api.Stack;
import org.jdiameter.api.StackType;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;


public abstract class AppStack {

    private static final Logger log = Logger.getLogger(AppStack.class);


    protected Stack stack;
    protected ISessionFactory factory;
    protected String realmName;
    protected String serverURI;

    protected void initStack(String configFile, ApplicationId appId, String identifier) {
        log.info("Initializing " + identifier + " Stack...");
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configFile)) {
            stack = new StackImpl();
            // Parse stack configuration
            Configuration config = new XMLConfiguration(is);
            factory = (ISessionFactory) stack.init(config);
            log.info(identifier + " Stack Configuration successfully loaded.");
            // Print info about applicatio
            Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

            log.info("Diameter " + identifier + " Stack  :: Supporting " + appIds.size() + " applications.");
            for (org.jdiameter.api.ApplicationId x : appIds) {
                log.info("Diameter " + identifier + " Stack  :: Common :: " + x);
            }

            // Register network req listener, even though we wont receive requests
            // this has to be done to inform stack that we support application
            Network network = stack.unwrap(Network.class);
            network.addNetworkReqListener((Request request) -> null, appId); //passing our example app id.

        } catch (Exception e) {
            e.printStackTrace();
            if (stack != null) {
                stack.destroy();
            }
            return;
        }

        MetaData metaData = stack.getMetaData();
        //ignore for now.
        if (metaData.getStackType() != StackType.TYPE_SERVER || metaData.getMinorVersion() <= 0) {
            stack.destroy();
            log.error("Incorrect driver");
            return;
        }

        // initialize local peer helper vars
        realmName = metaData.getLocalPeer().getRealmName();
        serverURI = metaData.getConfiguration().getChildren(Parameters.PeerTable.ordinal())[0]
                .getStringValue(Parameters.PeerName.ordinal(), null);
        serverURI = serverURI.substring(6, serverURI.length() - 5);

        try {
            log.info("Starting " + identifier + " Stack");
            stack.start();
            log.info(identifier + " Stack is running.");
        } catch (Exception e) {
            e.printStackTrace();
            stack.destroy();
            return;
        }
        log.info(identifier + " Stack initialization successfully completed.");
    }

    public void destroy() throws Exception {
        stack.stop(0, TimeUnit.SECONDS, DisconnectCause.REBOOTING);
        stack.destroy();
    }

    public ISessionFactory getSessionFactory() {
        return factory;
    }

    public String getRealm() {
        return realmName;
    }

    public String getServerUri() {
        return serverURI;
    }

    public abstract void initStack();

}
