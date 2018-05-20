package cz.polankam.pcrf.trafficgenerator.scenario.impl;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.StdoutPrintAction;
import java.util.HashMap;


public class SimpleDemoScenario extends Scenario {

    public static String TYPE = "SimpleDemo";
    private static ScenarioNode rootNode;

    private static void init() throws Exception {
        if (rootNode != null) {
            return;
        }

        ScenarioNode START = rootNode = new NodeBuilder().addSendAction(new StdoutPrintAction("*** START ***")).build();
        ScenarioNode AA = new NodeBuilder().addSendAction(new StdoutPrintAction("*** AA ***")).build();
        ScenarioNode AB = new NodeBuilder().addSendAction(new StdoutPrintAction("*** AB ***")).build();
        ScenarioNode AC = new NodeBuilder().addSendAction(new StdoutPrintAction("*** AC ***")).build();
        ScenarioNode AD = new NodeBuilder().addSendAction(new StdoutPrintAction("*** AD ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new StdoutPrintAction("*** END ***")).build();

        /**
         *
         *   START
         *       \
         *       AA - AB
         *        | \ |
         *        |  \|
         *       AD - AC
         *       /
         *     END
         *
         */

        START.addChild(AA);
        AA.addChild(80, AB).addChild(20, AC).validateProbabilities();
        AB.addChild(AC).validateProbabilities();
        AC.addChild(50, AD).addChild(50, AA).validateProbabilities();
        AD.addChild(80, AA).addChild(20, END).validateProbabilities();
    }

    @Override
    public HashMap<String, Object> createNewScenarioState() throws Exception {
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
