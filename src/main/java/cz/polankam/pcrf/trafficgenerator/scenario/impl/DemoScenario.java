package cz.polankam.pcrf.trafficgenerator.scenario.impl;

import cz.polankam.pcrf.trafficgenerator.scenario.NodeBuilder;
import cz.polankam.pcrf.trafficgenerator.scenario.Scenario;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioNode;
import cz.polankam.pcrf.trafficgenerator.scenario.actions.StdoutPrintAction;
import java.util.HashMap;


public class DemoScenario extends Scenario {

    public static String TYPE = "Demo";
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
        ScenarioNode BA = new NodeBuilder().addSendAction(new StdoutPrintAction("*** BA ***")).build();
        ScenarioNode BB = new NodeBuilder().addSendAction(new StdoutPrintAction("*** BB ***")).build();
        ScenarioNode BC = new NodeBuilder().addSendAction(new StdoutPrintAction("*** BC ***")).build();
        ScenarioNode BD = new NodeBuilder().addSendAction(new StdoutPrintAction("*** BD ***")).build();
        ScenarioNode END = new NodeBuilder().addSendAction(new StdoutPrintAction("*** END ***")).build();

        /**
         *
         *  START   _______
         *      \  /    ___\____
         *       \/    /    \   \
         *       AA - AB    BA - BB
         *        | \ |      | \ |
         *        |  \|      |  \|
         *       AD - AC    BD - BC
         *       /\    \____/____/
         *      /__\_______/
         *    END
         *
         */

        START.addChild(AA);
        AA.addChild(80, AB).addChild(10, AC).addChild(10, BA).validateProbabilities();
        AB.addChild(50, AC).addChild(50, BB).validateProbabilities();
        AC.addChild(40, AD).addChild(60, BC).validateProbabilities();
        AD.addChild(20, AA).addChild(70, BD).addChild(10, END).validateProbabilities();
        BA.addChild(80, BB).addChild(20, BC).validateProbabilities();
        BB.addChild(BC).validateProbabilities();
        BC.addChild(BD).validateProbabilities();
        BD.addChild(90, BA).addChild(10, END).validateProbabilities();
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
