package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Statistics;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsLoggerTest {

    @Rule
    private TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeEach
    void setup() throws IOException {
        temporaryFolder.create();
    }

    @AfterEach
    void teardown() {
        temporaryFolder.delete();
    }


    @Test
    void testInit() throws IOException {
        Client client = mock(Client.class);
        Statistics config = mock(Statistics.class);
        StatisticsLogger logger = new StatisticsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);

        logger.init();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertEquals("Time\tScenariosCount\tTimeoutsCount\tSentCount\tReceivedCount\tProcessLoad [%]" + System.lineSeparator(), content);
    }

    @Test
    void testLog() throws IOException {
        Client client = mock(Client.class);
        Statistics config = mock(Statistics.class);
        StatisticsLogger logger = new StatisticsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);
        when(client.getTimeoutsCount()).thenReturn((long) 24568);
        when(client.getScenariosCount()).thenReturn((long) 66857);
        when(client.getSentCount()).thenReturn((long) 82462);
        when(client.getReceivedCount()).thenReturn((long) 55842);

        logger.init();
        logger.log();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertTrue(content.startsWith("Time\tScenariosCount\tTimeoutsCount\tSentCount\tReceivedCount\tProcessLoad [%]" + System.lineSeparator()));
        assertTrue(content.contains("\t66857\t24568\t82462\t55842\t"));
    }

    @Test
    void testLogDouble() throws IOException {
        Client client = mock(Client.class);
        Statistics config = mock(Statistics.class);
        StatisticsLogger logger = new StatisticsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);
        when(client.getTimeoutsCount()).thenReturn((long) 24568).thenReturn((long) 554763);
        when(client.getScenariosCount()).thenReturn((long) 8541).thenReturn((long) 5874);
        when(client.getSentCount()).thenReturn((long) 85412).thenReturn((long) 542536);
        when(client.getReceivedCount()).thenReturn((long) 96842).thenReturn((long) 854236);

        logger.init();
        logger.log();
        logger.log();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertTrue(content.startsWith("Time\tScenariosCount\tTimeoutsCount\tSentCount\tReceivedCount\tProcessLoad [%]" + System.lineSeparator()));
        assertTrue(content.contains("\t5874\t530195\t457124\t757394\t"));
    }

}