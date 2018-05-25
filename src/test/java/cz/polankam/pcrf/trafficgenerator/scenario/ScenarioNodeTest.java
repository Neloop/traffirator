package cz.polankam.pcrf.trafficgenerator.scenario;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ScenarioNodeTest {

    @Test
    void testHasChildren_noChildren() {
        ScenarioNode node = new ScenarioNode();
        assertFalse(node.hasChildren());
    }

    @Test
    void testHasChildren_childWithoutProbability() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.addChild(new ScenarioNode());
        assertTrue(node.hasChildren());
    }

    @Test
    void testHasChildren_childWithProbability() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.addChild(100, new ScenarioNode());
        assertTrue(node.hasChildren());
    }

    @Test
    void testAddChild_childWithoutProbability() throws Exception {
        ScenarioNode node = new ScenarioNode();
        ScenarioNode a = new ScenarioNode(), b = new ScenarioNode(), c = new ScenarioNode();
        node.addChild(a);
        node.addChild(b);
        node.addChild(c);

        assertTrue(node.hasChildren());
        assertEquals(3, node.getChildren().size());

        assertEquals(0, node.getChildren().get(0).getProbability());
        assertEquals(0, node.getChildren().get(1).getProbability());
        assertEquals(0, node.getChildren().get(2).getProbability());

        assertEquals(a, node.getChildren().get(0).getNode());
        assertEquals(b, node.getChildren().get(1).getNode());
        assertEquals(c, node.getChildren().get(2).getNode());
    }

    @Test
    void testAddChild_childWithProbability() throws Exception {
        ScenarioNode node = new ScenarioNode();
        ScenarioNode a = new ScenarioNode(), b = new ScenarioNode(), c = new ScenarioNode();
        node.addChild(50, a);
        node.addChild(50, b);
        node.addChild(0, c);

        assertTrue(node.hasChildren());
        assertEquals(3, node.getChildren().size());

        assertEquals(50, node.getChildren().get(0).getProbability());
        assertEquals(50, node.getChildren().get(1).getProbability());
        assertEquals(0, node.getChildren().get(2).getProbability());

        assertEquals(a, node.getChildren().get(0).getNode());
        assertEquals(b, node.getChildren().get(1).getNode());
        assertEquals(c, node.getChildren().get(2).getNode());
    }

    @Test
    void testValidateProbabilities_emptyChildren() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.validateProbabilities();
        assertFalse(node.hasChildren());
    }

    @Test
    void testValidateProbabilities_balanceZeroes() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.addChild(new ScenarioNode());
        node.addChild(new ScenarioNode());
        node.addChild(new ScenarioNode());
        node.addChild(new ScenarioNode());

        node.validateProbabilities();

        assertEquals(25, node.getChildren().get(0).getProbability());
        assertEquals(25, node.getChildren().get(1).getProbability());
        assertEquals(25, node.getChildren().get(2).getProbability());
        assertEquals(25, node.getChildren().get(3).getProbability());
    }

    @Test
    void testValidateProbabilities_notCorrect() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.addChild(20, new ScenarioNode());
        node.addChild(20, new ScenarioNode());
        node.addChild(20, new ScenarioNode());
        node.addChild(20, new ScenarioNode());

        Throwable exception = assertThrows(Exception.class, () -> {
            node.validateProbabilities();
        });
        assertEquals("Sum of the probabilites of children is not equal to 100", exception.getMessage());
    }

    @Test
    void testValidateProbabilities_correct() throws Exception {
        ScenarioNode node = new ScenarioNode();
        node.addChild(25, new ScenarioNode());
        node.addChild(25, new ScenarioNode());
        node.addChild(25, new ScenarioNode());
        node.addChild(25, new ScenarioNode());

        node.validateProbabilities();
        assertEquals(4, node.getChildren().size());
    }

    @Test
    void testGetChild_badLowerProbability() {
        ScenarioNode node = new ScenarioNode();
        Throwable exception = assertThrows(Exception.class, () -> {
            node.getChild(-30);
        });
        assertEquals("Bad probability value", exception.getMessage());
    }

    @Test
    void testGetChild_badHigherProbability() {
        ScenarioNode node = new ScenarioNode();
        Throwable exception = assertThrows(Exception.class, () -> {
            node.getChild(102);
        });
        assertEquals("Bad probability value", exception.getMessage());
    }

    @Test
    void testGetChild_emptyChildren() throws Exception {
        ScenarioNode node = new ScenarioNode();
        assertNull(node.getChild(50));
    }

    @Test
    void testGetChild_middle() throws Exception {
        ScenarioNode node = new ScenarioNode();
        ScenarioNode a = new ScenarioNode(), b = new ScenarioNode(), c = new ScenarioNode(), d = new ScenarioNode();
        node.addChild(25, a);
        node.addChild(25, b);
        node.addChild(25, c);
        node.addChild(25, d);

        assertEquals(a, node.getChild(25));
        assertEquals(b, node.getChild(44));
        assertEquals(b, node.getChild(50));
    }

    @Test
    void testGetChild_last() throws Exception {
        ScenarioNode node = new ScenarioNode();
        ScenarioNode a = new ScenarioNode(), b = new ScenarioNode(), c = new ScenarioNode();
        node.addChild(33, a);
        node.addChild(33, b);
        node.addChild(33, c);

        assertEquals(b, node.getChild(60));
        assertEquals(c, node.getChild(100));
    }

}
