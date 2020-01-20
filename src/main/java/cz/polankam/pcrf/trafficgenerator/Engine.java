package cz.polankam.pcrf.trafficgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Config;
import cz.polankam.pcrf.trafficgenerator.config.ProfileValidator;
import cz.polankam.pcrf.trafficgenerator.exceptions.ValidationException;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the whole application. Engine start the execution by loading the configuration given by the command
 * line arguments, creating the base Diameter Client class and providing it the base dependencies. Also profile changes
 * are scheduled, statistics logger invoked and summary log created and after the execution written to.
 */
public class Engine {

    private static final Logger logger = LogManager.getLogger(Engine.class);

    private final SummaryLogger summaryLogger;
    private final String[] args;
    private CommandLine cmd;
    private PrintStream summaryOut;
    private ScheduledExecutorService executor;
    private final ScenarioFactory scenarioFactory;
    private final ProfileValidator profileValidator;

    /**
     * Constructor.
     * @param args command line arguments
     */
    private Engine(String[] args) {
        this.args = args;
        summaryLogger = new SummaryLogger();
        summaryOut = System.out;
        scenarioFactory = new ScenarioFactory();
        profileValidator = new ProfileValidator(scenarioFactory);
    }


    /**
     * Process command line arguments given by the user upon execution.
     * @throws ParseException in case of parsing error
     */
    private void processCmdArguments() throws ParseException {
        Options options = new Options();

        options.addOption(Option.builder("c").longOpt("config").argName("file").hasArg()
                .desc("YAML configuration file for the generator").build());
        options.addOption(new Option("h", "help", false, "Print this message"));

        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("traffirator", "Traffic generator for PCRF server within LTE network", options, "");
            System.exit(0);
        }

        if (!cmd.hasOption("config")) {
            throw new ParseException("Missing required option 'config'");
        }
    }

    /**
     * Gets the path to the configuration file from the cmd arguments, parses it and returns it.
     * There is also the validation of the values provided in the configuration.
     * @return configuration of the application
     * @throws IOException in case of file error
     * @throws ValidationException validation of config failed
     */
    private Config getConfig() throws IOException, ValidationException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String filename = cmd.getOptionValue("config");
        try (InputStream configFile = new FileInputStream(filename)) {
            Config config = mapper.readValue(configFile, Config.class);
            profileValidator.validate(config);
            summaryOut = new PrintStream(config.getSummary());
            return config;
        }
    }

    /**
     * There is no way how to tell if the jDiameter successfully connected to the server. The least thing we can do is
     * to wait a bit.
     * @throws Exception in case of error
     */
    private void waitForConnections() throws Exception {
        //wait for connection to peer
        logger.info("Waiting for connection to peer...");
        Thread.sleep(5000);
        logger.info("Enough waiting, lets roll");
    }

    /**
     * Start the whole execution, load configuration, create loggers, start the change runner and start
     * the Client class, which is the main sender/receiver.
     * @throws Exception in case of any error
     */
    private void start() throws Exception {
        logger.info("****************************************");
        logger.info("* STARTING TRAFFIRATOR *****************");
        logger.info("****************************************");

        Config config = getConfig();
        // prepare executor service based on given thread count and pass it to the client
        executor = Executors.newScheduledThreadPool(config.getThreadCount());
        Client client = new Client(executor, scenarioFactory);
        StatisticsLogger statistics = new StatisticsLogger(client, config.getStatistics());

        // initialization
        client.init();
        statistics.init();
        summaryLogger.setConfig(config);

        //
        waitForConnections();

        // start sending/receiving messages on gx and rx interfaces
        summaryLogger.setStart();
        // schedule execution of test profile
        ProfileChangeRunner.start(executor, summaryLogger, config, client);

        // schedule ending of the execution from the configuration
        // has its own executor in case of deadlocks in the client
        Executors.newScheduledThreadPool(1).schedule(() -> {
            client.finish();
            logger.info("End trigger activated");
        }, config.getProfile().getEnd(), TimeUnit.SECONDS);

        // schedule statistics logging
        executor.scheduleAtFixedRate(statistics::log, config.getStatistics().getSamplingPeriod(),
                config.getStatistics().getSamplingPeriod(), TimeUnit.MILLISECONDS);

        // wait till client is finished
        while (!client.finished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
        }

        try {
            // do not forget to destroy allocated stacks
            client.destroy();
            logger.info("All done... Good bye!");
        } finally {
            summaryLogger.setEnd();
            summaryLogger.setStatus(client.getFinishedReason());
            summaryLogger.printSummary(summaryOut);
            summaryOut.close();
            statistics.close();
        }
    }


    /**
     * Helper method for finding deadlocks if needed it has to be used.
     */
    public static void findDeadLocks()
    {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        long[] ids = tmx.findDeadlockedThreads();
        if (ids != null) {
            ThreadInfo[] infos = tmx.getThreadInfo(ids,true,true);
            System.out.println("Following Threads are deadlocked:");
            for (ThreadInfo info : infos) {
                System.out.println(info);
                System.out.println("Stacktrace:");
                for (StackTraceElement ste : info.getStackTrace()) {
                    System.out.println("  " + ste);
                }
                System.out.println();
            }
        }
    }

    /**
     * Entry point of the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            Engine engine = new Engine(args);
            engine.processCmdArguments();
            engine.start();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(1);
        }

        // for some reasons jDiameter keeps some threads started even after destroying stack and such... so kill it
        System.exit(0);
    }

}
