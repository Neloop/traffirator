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

    private static class Context {
        final ScheduledExecutorService executor;
        final Summary summary;
        final Queue<ProfileItem> queue;
        final int burstLimit;
        final Client client;
        final Map<String, Integer> lastCountCache;

        public Context(ScheduledExecutorService executor, Summary summary, Queue<ProfileItem> queue, int burstLimit, Client client) {
            this.executor = executor;
            this.summary = summary;
            this.queue = queue;
            this.burstLimit = burstLimit;
            this.client = client;
            this.lastCountCache = new HashMap<>();
        }
    }


    public static void start(ScheduledExecutorService executor, Summary summary, Config config, Client client) {
        Queue<ProfileItem> queue = new LinkedList<>(config.getProfile().getFlow());
        Context context = new Context(executor, summary, queue, config.getProfile().getBurstLimit(), client);
        executor.schedule(new ProfileChangeRunner(context), queue.peek().getStart(), TimeUnit.SECONDS);
    }


    private final Context context;

    private ProfileChangeRunner(Context context) {
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
                // Number of scenarios has to be lowered, this action can be instant
                context.client.controlScenarios(scenario.getType(), scenario.getCount());
            } else {
                int baseCountForScenario = currentCount;
                while (scenariosIncrementalCount > 0) {
                    int currentBurstLimit = context.burstLimit - previousScenarioBurst;
                    int burst = currentBurstLimit;
                    if (scenariosIncrementalCount < currentBurstLimit) {
                        burst = scenariosIncrementalCount;
                    } else {
                        ++secondsDelay;
                    }

                    final int countFinal = baseCountForScenario + burst;
                    context.executor.schedule(() -> {
                        context.client.controlScenarios(scenario.getType(), countFinal);
                    }, secondsDelay, TimeUnit.SECONDS);

                    scenariosIncrementalCount -= burst;
                    previousScenarioBurst = (burst + previousScenarioBurst) % context.burstLimit;
                    baseCountForScenario += burst;
                }
            }
        }

        context.summary.addChange(current);

        if (!context.queue.isEmpty()) {
            long nextStart = context.queue.peek().getStart() - current.getStart();
            context.executor.schedule(new ProfileChangeRunner(context), nextStart, TimeUnit.SECONDS);
        }
    }
}