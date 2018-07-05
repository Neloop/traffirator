package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Config;
import cz.polankam.pcrf.trafficgenerator.config.ProfileItem;
import cz.polankam.pcrf.trafficgenerator.config.ScenarioItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProfileChangeRunner implements Runnable {

    protected static class Context {
        final ScheduledExecutorService executor;
        final SummaryLogger summaryLogger;
        final Queue<ProfileItem> queue;
        final int burstLimit;
        final Client client;
        final Map<String, Integer> lastCountCache;

        public Context(ScheduledExecutorService executor, SummaryLogger summaryLogger, Queue<ProfileItem> queue, int burstLimit, Client client) {
            this.executor = executor;
            this.summaryLogger = summaryLogger;
            this.queue = queue;
            this.burstLimit = burstLimit;
            this.client = client;
            this.lastCountCache = new HashMap<>();
        }
    }


    public static void start(ScheduledExecutorService executor, SummaryLogger summaryLogger, Config config, Client client) {
        Queue<ProfileItem> queue = new LinkedList<>(config.getProfile().getFlow());
        Context context = new Context(executor, summaryLogger, queue, config.getProfile().getBurstLimit(), client);
        executor.schedule(new ProfileChangeRunner(context), queue.peek().getStart(), TimeUnit.SECONDS);
    }


    private final Context context;

    ProfileChangeRunner(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        ProfileItem current = context.queue.poll();
        int secondsDelay = 0;
        int previousScenarioBurst = 0;
        for (ScenarioItem scenario : current.getScenarios()) {
            int currentCount = context.lastCountCache.getOrDefault(scenario.getType(), 0);
            context.lastCountCache.put(scenario.getType(), scenario.getCount());
            int scenariosIncrementalCount = scenario.getCount() - currentCount;

            if (scenariosIncrementalCount < 0) {
                // number of scenarios has to be lowered, this action can be instant
                context.client.controlScenarios(scenario.getType(), scenario.getCount(), 0);
            } else {
                int baseCountForScenario = currentCount;
                while (scenariosIncrementalCount > 0) {
                    int currentBurstLimit = context.burstLimit - previousScenarioBurst;
                    int burst = scenariosIncrementalCount < currentBurstLimit ? scenariosIncrementalCount : currentBurstLimit;

                    // control the scenario, delays are handled by the client itself
                    context.client.controlScenarios(scenario.getType(), baseCountForScenario + burst, secondsDelay);

                    scenariosIncrementalCount -= burst;
                    previousScenarioBurst = (burst + previousScenarioBurst) % context.burstLimit;
                    baseCountForScenario += burst;
                    secondsDelay += previousScenarioBurst > 0 ? 0 : 1;
                }
            }
        }

        context.summaryLogger.addChange(current);

        if (!context.queue.isEmpty()) {
            long nextStart = context.queue.peek().getStart() - current.getStart();
            context.executor.schedule(new ProfileChangeRunner(context), nextStart, TimeUnit.SECONDS);
        }
    }
}