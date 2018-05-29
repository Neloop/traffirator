package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Timeouts;
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

class TimeoutsLoggerTest {

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
        Timeouts config = mock(Timeouts.class);
        TimeoutsLogger logger = new TimeoutsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);

        logger.init();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertEquals("Time\tTimeoutsCount" + System.lineSeparator(), content);
    }

    @Test
    void testLog() throws IOException {
        Client client = mock(Client.class);
        Timeouts config = mock(Timeouts.class);
        TimeoutsLogger logger = new TimeoutsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);
        when(client.getTimeoutsCount()).thenReturn((long) 24568);

        logger.init();
        logger.log();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertTrue(content.startsWith("Time\tTimeoutsCount" + System.lineSeparator()));
        assertTrue(content.endsWith("24568" + System.lineSeparator()));
    }

    @Test
    void testLogDouble() throws IOException {
        Client client = mock(Client.class);
        Timeouts config = mock(Timeouts.class);
        TimeoutsLogger logger = new TimeoutsLogger(client, config);

        String file = temporaryFolder.newFile().toString();
        when(config.getLogFile()).thenReturn(file);
        when(client.getTimeoutsCount()).thenReturn((long) 24568).thenReturn((long) 554763);

        logger.init();
        logger.log();
        logger.log();
        logger.close();

        String content = new String(Files.readAllBytes(Paths.get(file)));
        assertTrue(content.startsWith("Time\tTimeoutsCount" + System.lineSeparator()));
        assertTrue(content.endsWith("530195" + System.lineSeparator()));
    }

}