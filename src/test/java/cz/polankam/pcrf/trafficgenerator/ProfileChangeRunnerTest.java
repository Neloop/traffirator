package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.ProfileItem;
import cz.polankam.pcrf.trafficgenerator.config.ScenarioItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileChangeRunnerTest {

    private ProfileChangeRunner.Context context;
    private ScheduledExecutorService executor;
    private Summary summary;
    private Queue<ProfileItem> queue;
    private int burstLimit;
    private Client client;

    @BeforeEach
    void setUp() {
        executor = mock(ScheduledExecutorService.class);
        summary = mock(Summary.class);
        queue = new LinkedList<>();
        burstLimit = 100;
        client = mock(Client.class);

        context = new ProfileChangeRunner.Context(
                executor,
                summary,
                queue,
                burstLimit,
                client
        );
    }

    @Test
    void testRun_1() throws InterruptedException {
        ProfileItem item = new ProfileItem();
        item.setStart(0);
        item.setScenarios(Collections.singletonList(new ScenarioItem().setType("type").setCount(200)));
        queue.add(item);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).controlScenarios("type", 100, 0);
        inOrder.verify(client).controlScenarios("type", 200, 1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_2() throws InterruptedException {
        ProfileItem item = new ProfileItem();
        item.setStart(0);
        item.setScenarios(Collections.singletonList(new ScenarioItem().setType("type").setCount(199)));
        queue.add(item);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).controlScenarios("type", 100, 0);
        inOrder.verify(client).controlScenarios("type", 199, 1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_3() throws InterruptedException {
        ProfileItem item = new ProfileItem();
        item.setStart(0);
        item.setScenarios(Collections.singletonList(new ScenarioItem().setType("type").setCount(201)));
        queue.add(item);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).controlScenarios("type", 100, 0);
        inOrder.verify(client).controlScenarios("type", 200, 1);
        inOrder.verify(client).controlScenarios("type", 201, 2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_4() throws InterruptedException {
        ProfileItem item = new ProfileItem();
        item.setStart(0);
        item.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(201),
                new ScenarioItem().setType("type_2").setCount(99)
        ));
        queue.add(item);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 200, 1);
        inOrder.verify(client).controlScenarios("type_1", 201, 2);
        inOrder.verify(client).controlScenarios("type_2", 99, 2);
        inOrder.verifyNoMoreInteractions();
    }
}