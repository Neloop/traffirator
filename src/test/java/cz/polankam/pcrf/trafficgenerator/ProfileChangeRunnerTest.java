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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileChangeRunnerTest {

    private ProfileChangeRunner.Context context;
    private ScheduledExecutorService executor;
    private SummaryLogger summaryLogger;
    private Queue<ProfileItem> queue;
    private int burstLimit;
    private Client client;

    @BeforeEach
    void setUp() {
        executor = mock(ScheduledExecutorService.class);
        summaryLogger = mock(SummaryLogger.class);
        queue = new LinkedList<>();
        burstLimit = 100;
        client = mock(Client.class);

        context = new ProfileChangeRunner.Context(
                executor,
                summaryLogger,
                queue,
                burstLimit,
                client
        );
    }

    @Test
    void testRun_1() {
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
    void testRun_2() {
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
    void testRun_3() {
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
    void testRun_4() {
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

    @Test
    void testRun_5() {
        ProfileItem item = new ProfileItem();
        item.setStart(0);
        item.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(120),
                new ScenarioItem().setType("type_2").setCount(60),
                new ScenarioItem().setType("type_3").setCount(10),
                new ScenarioItem().setType("type_4").setCount(20)
        ));
        queue.add(item);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();

        InOrder inOrder = inOrder(client);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 120, 1);
        inOrder.verify(client).controlScenarios("type_2", 60, 1);
        inOrder.verify(client).controlScenarios("type_3", 10, 1);
        inOrder.verify(client).controlScenarios("type_4", 10, 1);
        inOrder.verify(client).controlScenarios("type_4", 20, 2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_6() {
        ProfileItem item1 = new ProfileItem();
        item1.setStart(0);
        item1.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(210),
                new ScenarioItem().setType("type_2").setCount(60)
        ));
        ProfileItem item2 = new ProfileItem();
        item2.setStart(100);
        item2.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(150),
                new ScenarioItem().setType("type_2").setCount(50)
        ));
        queue.add(item1);
        queue.add(item2);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();
        runner.run();

        InOrder inOrder = inOrder(client, executor);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 200, 1);
        inOrder.verify(client).controlScenarios("type_1", 210, 2);
        inOrder.verify(client).controlScenarios("type_2", 60, 2);
        inOrder.verify(executor).schedule((Runnable) any(), eq((long) 100), eq(TimeUnit.SECONDS));
        inOrder.verify(client).controlScenarios("type_1", 150, 0);
        inOrder.verify(client).controlScenarios("type_2", 50, 0);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_7() {
        ProfileItem item1 = new ProfileItem();
        item1.setStart(0);
        item1.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(210),
                new ScenarioItem().setType("type_2").setCount(60)
        ));
        ProfileItem item2 = new ProfileItem();
        item2.setStart(1000);
        item2.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(360),
                new ScenarioItem().setType("type_2").setCount(60)
        ));
        queue.add(item1);
        queue.add(item2);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();
        runner.run();

        InOrder inOrder = inOrder(client, executor);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 200, 1);
        inOrder.verify(client).controlScenarios("type_1", 210, 2);
        inOrder.verify(client).controlScenarios("type_2", 60, 2);
        inOrder.verify(executor).schedule((Runnable) any(), eq((long) 1000), eq(TimeUnit.SECONDS));
        inOrder.verify(client).controlScenarios("type_1", 310, 0);
        inOrder.verify(client).controlScenarios("type_1", 360, 1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_8() {
        ProfileItem item1 = new ProfileItem();
        item1.setStart(0);
        item1.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(210),
                new ScenarioItem().setType("type_2").setCount(210)
        ));
        ProfileItem item2 = new ProfileItem();
        item2.setStart(1450);
        item2.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(360),
                new ScenarioItem().setType("type_2").setCount(70)
        ));
        queue.add(item1);
        queue.add(item2);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();
        runner.run();

        InOrder inOrder = inOrder(client, executor);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 200, 1);
        inOrder.verify(client).controlScenarios("type_1", 210, 2);
        inOrder.verify(client).controlScenarios("type_2", 90, 2);
        inOrder.verify(client).controlScenarios("type_2", 190, 3);
        inOrder.verify(client).controlScenarios("type_2", 210, 4);
        inOrder.verify(executor).schedule((Runnable) any(), eq((long) 1450), eq(TimeUnit.SECONDS));
        inOrder.verify(client).controlScenarios("type_1", 310, 0);
        inOrder.verify(client).controlScenarios("type_1", 360, 1);
        inOrder.verify(client).controlScenarios("type_2", 70, 0);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRun_9() {
        ProfileItem item1 = new ProfileItem();
        item1.setStart(0);
        item1.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(210),
                new ScenarioItem().setType("type_2").setCount(60)
        ));
        ProfileItem item2 = new ProfileItem();
        item2.setStart(1000);
        item2.setScenarios(Arrays.asList(
                new ScenarioItem().setType("type_1").setCount(360),
                new ScenarioItem().setType("type_2").setCount(500)
        ));
        queue.add(item1);
        queue.add(item2);

        ProfileChangeRunner runner = new ProfileChangeRunner(context);
        runner.run();
        runner.run();

        InOrder inOrder = inOrder(client, executor);
        inOrder.verify(client).controlScenarios("type_1", 100, 0);
        inOrder.verify(client).controlScenarios("type_1", 200, 1);
        inOrder.verify(client).controlScenarios("type_1", 210, 2);
        inOrder.verify(client).controlScenarios("type_2", 60, 2);
        inOrder.verify(executor).schedule((Runnable) any(), eq((long) 1000), eq(TimeUnit.SECONDS));
        inOrder.verify(client).controlScenarios("type_1", 310, 0);
        inOrder.verify(client).controlScenarios("type_1", 360, 1);
        inOrder.verify(client).controlScenarios("type_2", 110, 1);
        inOrder.verify(client).controlScenarios("type_2", 210, 2);
        inOrder.verify(client).controlScenarios("type_2", 310, 3);
        inOrder.verify(client).controlScenarios("type_2", 410, 4);
        inOrder.verify(client).controlScenarios("type_2", 500, 5);
        inOrder.verifyNoMoreInteractions();
    }
}