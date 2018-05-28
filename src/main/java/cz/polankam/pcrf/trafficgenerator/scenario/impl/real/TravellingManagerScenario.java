package cz.polankam.pcrf.trafficgenerator.scenario.impl.real;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.StdoutPrintAction;

import java.util.HashMap;


public class TravellingManagerScenario extends Scenario {

    public static final String TYPE = "TravellingManager";
    private static ScenarioNode rootNode;

    private static void init() throws Exception {
        if (rootNode != null) {
            return;
        }

        ScenarioNode START = rootNode = new NodeBuilder().addSendAction(new StdoutPrintAction("*** START ***")).build();
        ScenarioNode connect = new NodeBuilder().addSendAction(new StdoutPrintAction("*** Connect ***")).build();
        ScenarioNode update = new NodeBuilder().addSendAction(new StdoutPrintAction("*** Update ***")).build();
        ScenarioNode disconnect = new NodeBuilder().addSendAction(new StdoutPrintAction("*** Disconnect ***")).build();
        ScenarioNode lostConnection = new NodeBuilder().addSendAction(new StdoutPrintAction("*** LostConnection ***")).build();
        ScenarioNode callInit = new NodeBuilder().addSendAction(new StdoutPrintAction("*** CallInit ***")).build();
        ScenarioNode callUpdate = new NodeBuilder().addSendAction(new StdoutPrintAction("*** CallUpdate ***")).build();
        ScenarioNode callUpdateCodec = new NodeBuilder().addSendAction(new StdoutPrintAction("*** CallUpdateCodec ***")).build();
        ScenarioNode callTermination = new NodeBuilder().addSendAction(new StdoutPrintAction("*** CallTermination ***")).build();
        ScenarioNode callLostConnection = new NodeBuilder().addSendAction(new StdoutPrintAction("*** CallLostConnection ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new StdoutPrintAction("*** END ***")).build();

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
        connect.addChild(50, update).addChild(10, disconnect).addChild(10, lostConnection).addChild(30, callInit).validateProbabilities();
        update.addChild(30, update).addChild(10, disconnect).addChild(10, lostConnection).addChild(50, callInit).validateProbabilities();
        disconnect.addChild(50, connect).addChild(50, END).validateProbabilities();
        lostConnection.addChild(50, connect).addChild(50, END).validateProbabilities();
        callInit.addChild(50, callUpdate).addChild(20, callUpdateCodec).addChild(10, callTermination).addChild(20, callLostConnection).validateProbabilities();
        callUpdate.addChild(60, callUpdate).addChild(20, callUpdateCodec).addChild(20, callTermination).validateProbabilities();
        callUpdateCodec.addChild(20, callUpdateCodec).addChild(40, callUpdate).addChild(20, callTermination).addChild(20, callLostConnection).validateProbabilities();
        callTermination.addChild(20, callInit).addChild(60, update).addChild(10, disconnect).addChild(10, lostConnection).validateProbabilities();
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
