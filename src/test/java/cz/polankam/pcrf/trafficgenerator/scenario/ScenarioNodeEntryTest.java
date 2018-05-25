package cz.polankam.pcrf.trafficgenerator.scenario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioNodeEntryTest {

    @Test
    void test_withoutProbability() {
        ScenarioNode node = new ScenarioNode();
        ScenarioNodeEntry entry = new ScenarioNodeEntry(node);

        assertEquals(node, entry.getNode());
        assertEquals(0, entry.getProbability());
    }

    @Test
    void test_withProbability() {
        int probability = 75;
        ScenarioNode node = new ScenarioNode();
        ScenarioNodeEntry entry = new ScenarioNodeEntry(probability, node);

        assertEquals(node, entry.getNode());
        assertEquals(probability, entry.getProbability());
    }

    @Test
    void testSetProbability() {
        int probability = 45;
        ScenarioNodeEntry entry = new ScenarioNodeEntry(probability, new ScenarioNode());

        assertEquals(probability, entry.getProbability());
        probability = 86;
        entry.setProbability(probability);
        assertEquals(probability, entry.getProbability());
    }
}