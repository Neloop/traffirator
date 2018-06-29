package cz.polankam.pcrf.trafficgenerator.rx;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.client.impl.app.rx.ClientRxSessionImpl;
import org.jdiameter.client.impl.app.rx.IClientRxSessionData;
import org.jdiameter.common.impl.app.rx.RxSessionFactoryImpl;
import org.jdiameter.server.impl.app.rx.IServerRxSessionData;
import org.jdiameter.server.impl.app.rx.ServerRxSessionImpl;

/**
 * Copied from the jDiameter sources. Bugfix for not setting the application identification applied.
 * Bugfix was submitted as an issue on the jDiameter GitHub pages and then successfully merged as a pull request.
 * But fixed version was not yet released in the maven repository.
 */
public class MyRxSessionFactoryImpl extends RxSessionFactoryImpl {

  public MyRxSessionFactoryImpl(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public MyRxSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultAAFailureHandling,
      long defaultValidityTime, long defaultTxTimerValue) {
    super(sessionFactory, defaultDirectDebitingFailureHandling, defaultAAFailureHandling, defaultValidityTime, defaultTxTimerValue);
  }

  
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      // FIXME:
      if (aClass == ClientRxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientRxSessionData sessionData =  (IClientRxSessionData) this.sessionDataFactory.getAppSessionData(ClientRxSession.class, sessionId);
        sessionData.setApplicationId(applicationId);
        ClientRxSessionImpl clientSession = new ClientRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(),
            this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerRxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerRxSessionData sessionData =  (IServerRxSessionData) this.sessionDataFactory.getAppSessionData(ServerRxSession.class, sessionId);
        ServerRxSessionImpl serverSession = new ServerRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(),
            this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRxSession.class + "," + ServerRxSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Rx Session.", e);
    }

    return appSession;
  }

}
