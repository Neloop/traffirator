package cz.polankam.pcrf.trafficgenerator.scenario.impl.real;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.LoggerPrintAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call.*;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control.*;
import cz.polankam.pcrf.trafficgenerator.utils.data.DataProvider;

import java.util.HashMap;


public class CallCenterEmployeeScenario extends Scenario {

    public static final String TYPE = "CallCenterEmployee";
    private static ScenarioNode rootNode;

    private static void init() throws Exception {
        if (rootNode != null) {
            return;
        }

        ScenarioNode START = rootNode = new NodeBuilder().addSendAction(new LoggerPrintAction("*** START ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new LoggerPrintAction("*** END ***")).build();

        ScenarioNode connect = new NodeBuilder()
                .setName("Connect")
                .addSendAction("CCR-I", new GxCcrI_SendAction())
                .addReceiveGxAction("CCA-I", 3000, new GxCcaI_Success_ReceiveAction())
                .build();
        ScenarioNode update = new NodeBuilder()
                .setName("Update")
                .addSendAction("CCR-U", new GxCcrU_SendAction())
                .addReceiveGxAction("CCA-U", 3000, new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode disconnect = new NodeBuilder()
                .setName("Disconnect")
                .addSendAction("CCR-T", new GxCcrT_SendAction())
                .addReceiveGxAction("CCA-T", 3000, new GxCcaT_Success_ReceiveAction())
                .build();
        ScenarioNode lostConnection = new NodeBuilder()
                .setName("LostConnection")
                .addSendAction("CCR-LC", new GxCcrU_LostConnection_SendAction())
                .addReceiveGxAction("CCA-LC", 3000, new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode callInit = new NodeBuilder()
                .setName("CallInit")
                .addSendAction("AAR-1", new RxAar_Init_SendAction())
                .addReceiveAction("RAR-AAA-1", 3000, new GxRar_ReceiveAction(), new RxAaa_Success_ReceiveAction())
                .addSendAction("RAA", new GxRaa_Success_SendAction())
                .addSendAction("AAR-2", new RxAar_Noop_SendAction())
                .addReceiveRxAction("AAA-2", 3000, new RxAaa_Success_ReceiveAction())
                .build();
        ScenarioNode callUpdateCodec = new NodeBuilder()
                .setName("CallUpdateCodec")
                .addSendAction("AAR", new RxAar_SendAction())
                .addReceiveAction("RAR-AAA", 3000, new GxRar_ReceiveAction(), new RxAaa_Success_ReceiveAction())
                .addSendAction("RAA", new GxRaa_Success_SendAction())
                .build();
        ScenarioNode callTermination = new NodeBuilder()
                .setName("CallTermination")
                .addSendAction("STR", new RxStr_SendAction())
                .addReceiveAction("RAR-STA", 3000, new GxRar_ReceiveAction(), new RxSta_Success_ReceiveAction())
                .addSendAction("RAA", new GxRaa_Success_SendAction())
                .build();
        ScenarioNode callLostConnection = new NodeBuilder()
                .setName("CallLostConnection")
                .addSendAction("CCR-LC", new GxCcrU_LostConnection_SendAction())
                .addReceiveAction("CCA-LC-ASR", 3000, new GxCcaU_Success_ReceiveAction(), new RxAsr_ReceiveAction())
                .addSendAction("ASA", new RxAsa_Success_SendAction())
                .addSendAction("STR", new RxStr_SendAction())
                .addReceiveAction("RAR-STA", 3000, new GxRar_ReceiveAction(), new RxSta_Success_ReceiveAction())
                .addSendAction("RAA", new GxRaa_Success_SendAction())
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

        START.addChild(100, connect)
                .validateProbabilities();
        connect.addChild(10, 6_000, update)
                .addChild(5, 30_000, disconnect)
                .addChild(5, 30_000, lostConnection)
                .addChild(80, 12_000, callInit)
                .validateProbabilities();
        update.addChild(0, update)
                .addChild(5, 30_000, disconnect)
                .addChild(5, 30_000, lostConnection)
                .addChild(90, 6_000, callInit)
                .validateProbabilities();
        disconnect.addChild(50, 30_000, connect)
                .addChild(50, 3_000, END)
                .validateProbabilities();
        lostConnection.addChild(50, 30_000, connect)
                .addChild(50, 3_000, END)
                .validateProbabilities();
        callInit.addChild(20, 1_000, callUpdateCodec)
                .addChild(70, 30_000, callTermination)
                .addChild(10, 30_000, callLostConnection)
                .validateProbabilities();
        callUpdateCodec.addChild(20, 3_000, callUpdateCodec)
                .addChild(70, 30_000, callTermination)
                .addChild(10, 30_000, callLostConnection)
                .validateProbabilities();
        callTermination.addChild(80, 6_000, callInit)
                .addChild(10, 6_000, update)
                .addChild(5, 6_000, disconnect)
                .addChild(5, 30_000, lostConnection)
                .validateProbabilities();
        callLostConnection.addChild(50, 30_000, connect)
                .addChild(50, 3_000, END)
                .validateProbabilities();
    }

    @Override
    protected HashMap<String, Object> createNewScenarioState() throws Exception {
        HashMap<String, Object> state = new HashMap<>();

        state.put("cc_request_number", 0);
        state.put("framed_ip", DataProvider.randomFramedIp());
        state.put("imei", DataProvider.randomIMEI());
        state.put("msisdn", DataProvider.randomMSISDN());
        state.put("imsi", DataProvider.randomIMSI());
        state.put("an_gw_address", DataProvider.randomAnGwAddress());
        state.put("an_ci_gx", DataProvider.randomAnCiGx());
        state.put("called_station", "ims");
        state.put("af_charging_identifier", DataProvider.randomAfChargingIdentifier());

        return state;
    }

    @Override
    protected ScenarioNode getRootNode() throws Exception {
        init();
        return rootNode;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getDelaysVariability() {
        return 20;
    }

}
