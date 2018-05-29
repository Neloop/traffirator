package cz.polankam.pcrf.trafficgenerator.scenario.impl.real;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.LoggerPrintAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call.*;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control.*;

import java.util.HashMap;


public class CallPerformanceScenario extends Scenario {

    public static final String TYPE = "CallPerformance";
    private static ScenarioNode rootNode;

    private static void init() throws Exception {
        if (rootNode != null) {
            return;
        }

        ScenarioNode START = rootNode = new NodeBuilder().addSendAction(new LoggerPrintAction("*** START ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new LoggerPrintAction("*** END ***")).build();

        ScenarioNode connect = new NodeBuilder()
                .addSendAction(new GxCcrI_SendAction())
                .addReceiveAction(new GxCcaI_Success_ReceiveAction())
                .build();
        ScenarioNode update = new NodeBuilder()
                .addSendAction(new GxCcrU_SendAction())
                .addReceiveAction(new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode disconnect = new NodeBuilder()
                .addSendAction(new GxCcrT_SendAction())
                .addReceiveAction(new GxCcaT_Success_ReceiveAction())
                .build();
        ScenarioNode lostConnection = new NodeBuilder()
                .addSendAction(new GxCcrU_LostConnection_SendAction())
                .addReceiveAction(new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode callInit = new NodeBuilder()
                .addSendAction(new RxAar_SendAction(0, new String[]{}, new String[]{}, new String[]{})) // TODO
                .addReceiveAction(new GxRar_And_RxAaa_Success_ReceiveAction())
                .addReceiveAction(new GxRar_And_RxAaa_Success_ReceiveAction())
                .addSendAction(new GxRaa_Success_SendAction())
                .addSendAction(new RxAar_SendAction(0, new String[]{}, new String[]{}, new String[]{})) // TODO
                .addReceiveAction(new RxAaa_Success_ReceiveAction())
                .build();
        ScenarioNode callUpdateCodec = new NodeBuilder() // TODO
                .build();
        ScenarioNode callTermination = new NodeBuilder()
                .addSendAction(new RxStr_SendAction())
                .addReceiveAction(new GxRar_And_RxSta_Success_ReceiveAction())
                .addReceiveAction(new GxRar_And_RxSta_Success_ReceiveAction())
                .addSendAction(new GxRaa_Success_SendAction())
                .build();
        ScenarioNode callLostConnection = new NodeBuilder()
                .addSendAction(new GxCcrU_LostConnection_SendAction())
                .addReceiveAction(new GxCcaU_And_RxAsr_Success_ReceiveAction())
                .addReceiveAction(new GxCcaU_And_RxAsr_Success_ReceiveAction())
                .addSendAction(new RxAsa_Success_SendAction())
                .build();

        /*
         *  Simplified image, some relations are missing
         *
         *
         *                         END
         *                         /
         *           LOST CONNECTION ------------------------------------ CALL TERMINATION
         * START      /            \                                     /                \
         *     \     /              \                                   /                  \
         *     CONNECT --------- UPDATE ------------------------ CALL INIT ---------- CALL UPDATE CODEC
         *          \             /                                    \                  /
         *           \           /                                      \                /
         *            DISCONNECT ---------------- END ---------------- CALL LOST CONNECTION
         *
         */

        START.addChild(100, connect).validateProbabilities();
        connect.addChild(10, update).addChild(10, disconnect).addChild(10, lostConnection).addChild(70, callInit).validateProbabilities();
        update.addChild(5, update).addChild(40, disconnect).addChild(40, lostConnection).addChild(15, callInit).validateProbabilities();
        disconnect.addChild(50, connect).addChild(50, END).validateProbabilities();
        lostConnection.addChild(50, connect).addChild(50, END).validateProbabilities();
        callInit.addChild(40, callUpdateCodec).addChild(50, callTermination).addChild(10, callLostConnection).validateProbabilities();
        callUpdateCodec.addChild(20, callUpdateCodec).addChild(70, callTermination).addChild(10, callLostConnection).validateProbabilities();
        callTermination.addChild(10, callInit).addChild(10, update).addChild(70, disconnect).addChild(10, lostConnection).validateProbabilities();
        callLostConnection.addChild(50, connect).addChild(50, END).validateProbabilities();
    }

    @Override
    public HashMap<String, Object> createNewScenarioState() {
        // TODO
        return new HashMap<>();
    }

    @Override
    public ScenarioNode getRootNode() throws Exception {
        init();
        return rootNode;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
