package cz.polankam.pcrf.trafficgenerator.scenario.impl.real;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.LoggerPrintAction;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.call.*;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.control.*;
import cz.polankam.pcrf.trafficgenerator.utils.data.DataProvider;

import java.util.HashMap;


public class MalfunctioningCellPhoneScenario extends Scenario {

    public static final String TYPE = "MalfunctioningCellPhone";
    private static ScenarioNode rootNode;

    private static void init() throws Exception {
        if (rootNode != null) {
            return;
        }

        ScenarioNode START = rootNode = new NodeBuilder().addSendAction(new LoggerPrintAction("*** START ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new LoggerPrintAction("*** END ***")).build();

        ScenarioNode connect = new NodeBuilder()
                .addSendAction(new GxCcrI_SendAction())
                .addReceiveGxAction(3000, new GxCcaI_Success_ReceiveAction())
                .build();
        ScenarioNode update = new NodeBuilder()
                .addSendAction(new GxCcrU_SendAction())
                .addReceiveGxAction(3000, new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode disconnect = new NodeBuilder()
                .addSendAction(new GxCcrT_SendAction())
                .addReceiveGxAction(3000, new GxCcaT_Success_ReceiveAction())
                .build();
        ScenarioNode lostConnection = new NodeBuilder()
                .addSendAction(new GxCcrU_LostConnection_SendAction())
                .addReceiveGxAction(3000, new GxCcaU_Success_ReceiveAction())
                .build();
        ScenarioNode callInit = new NodeBuilder()
                .addSendAction(new RxAar_SendAction())
                .addReceiveAction(3000, new GxRar_ReceiveAction(), new RxAaa_Success_ReceiveAction())
                .addSendAction(new GxRaa_Success_SendAction())
                .addSendAction(new RxAar_SendAction())
                .addReceiveRxAction(3000, new RxAaa_Success_ReceiveAction())
                .build();
        ScenarioNode callUpdateCodec = new NodeBuilder()
                .addSendAction(new RxAar_SendAction())
                .addReceiveAction(3000, new GxRar_ReceiveAction(), new RxAaa_Success_ReceiveAction())
                .addSendAction(new GxRaa_Success_SendAction())
                .build();
        ScenarioNode callTermination = new NodeBuilder()
                .addSendAction(new RxStr_SendAction())
                .addReceiveAction(3000, new GxRar_ReceiveAction(), new RxSta_Success_ReceiveAction())
                .addSendAction(new GxRaa_Success_SendAction())
                .build();
        ScenarioNode callLostConnection = new NodeBuilder()
                .addSendAction(new GxCcrU_LostConnection_SendAction())
                .addReceiveAction(3000, new GxCcaU_Success_ReceiveAction(), new RxAsr_ReceiveAction())
                .addSendAction(new RxAsa_Success_SendAction())
                .addSendAction(new RxStr_SendAction())
                .addReceiveRxAction(3000, new RxSta_Success_ReceiveAction())
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
        connect.addChild(5, update).addChild(80, disconnect).addChild(5, lostConnection).addChild(10, callInit).validateProbabilities();
        update.addChild(5, update).addChild(80, disconnect).addChild(5, lostConnection).addChild(10, callInit).validateProbabilities();
        disconnect.addChild(90, connect).addChild(10, END).validateProbabilities();
        lostConnection.addChild(90, connect).addChild(10, END).validateProbabilities();
        callInit.addChild(10, callUpdateCodec).addChild(10, callTermination).addChild(80, callLostConnection).validateProbabilities();
        callUpdateCodec.addChild(10, callUpdateCodec).addChild(10, callTermination).addChild(80, callLostConnection).validateProbabilities();
        callTermination.addChild(5, callInit).addChild(5, update).addChild(80, disconnect).addChild(10, lostConnection).validateProbabilities();
        callLostConnection.addChild(90, connect).addChild(10, END).validateProbabilities();
    }

    @Override
    protected HashMap<String, Object> createNewScenarioState() throws Exception {
        HashMap<String, Object> state = new HashMap<>();

        state.put("cc_request_number", 0);
        state.put("framed_ip", DataProvider.randomFramedIp());
        state.put("imei", DataProvider.randomIMEI());
        state.put("msisdn", DataProvider.randomMSISDN());
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
