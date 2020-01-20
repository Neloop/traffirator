package cz.polankam.pcrf.trafficgenerator.client;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Base class for the Diameter application stacks. It features some basic getters of the stack capabilities and
 * characteristics. <code>AppStack</code> base class holds also the commong initialization routine of the stack which
 * might be called from the children.
 */
public abstract class AppStack {

    private static final Logger logger = LogManager.getLogger(AppStack.class);


    protected Stack stack;
    protected ISessionFactory factory;
    protected String realmName;
    protected String serverURI;

    /**
     * Initialize the stack with the given information.
     * @param configFile XML configuration file of the stack
     * @param appId application identification of the stack
     * @param identifier stack identifier, for debugging purposes
     */
    protected void initStack(String configFile, ApplicationId appId, String identifier) {
        logger.info("Initializing " + identifier + " Stack...");
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configFile)) {
            stack = new StackImpl();
            // Parse stack configuration
            Configuration config = new XMLConfiguration(is);
            factory = (ISessionFactory) stack.init(config);
            logger.info(identifier + " Stack Configuration successfully loaded.");
            // Print info about application
            Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

            logger.info("Diameter " + identifier + " Stack  :: Supporting " + appIds.size() + " applications.");
            for (org.jdiameter.api.ApplicationId x : appIds) {
                logger.info("Diameter " + identifier + " Stack  :: Common :: " + x);
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
            logger.error("Incorrect driver");
            return;
        }

        // initialize local peer helper vars
        realmName = metaData.getLocalPeer().getRealmName();
        serverURI = metaData.getConfiguration().getChildren(Parameters.PeerTable.ordinal())[0]
                .getStringValue(Parameters.PeerName.ordinal(), null);
        serverURI = serverURI.substring(6, serverURI.length() - 5);

        try {
            logger.info("Starting " + identifier + " Stack");
            stack.start();
            logger.info(identifier + " Stack is running.");
        } catch (Exception e) {
            e.printStackTrace();
            stack.destroy();
            return;
        }
        logger.info(identifier + " Stack initialization successfully completed.");
    }

    /**
     * Shutdown the Diameter stack gracefully and then destroy it.
     * @throws Exception in case of error
     */
    public void destroy() throws Exception {
        stack.stop(0, TimeUnit.SECONDS, DisconnectCause.REBOOTING);
        stack.destroy();
    }

    /**
     * Get the special application session factory for the stack.
     * @return session factory interface
     */
    public ISessionFactory getSessionFactory() {
        return factory;
    }

    /**
     * Get the realm of this application stack.
     * @return textual representation of realm
     */
    public String getRealm() {
        return realmName;
    }

    /**
     * Get the URI of the server to which this stack is connected.
     * @return textual uri
     */
    public String getServerUri() {
        return serverURI;
    }

    /**
     * To be implemented in the children. It should initialize current stack by calling the
     * <code>initStack(String, ApplicationId, String)</code> base method, which takes care of most of the things.
     */
    public abstract void initStack();

}
