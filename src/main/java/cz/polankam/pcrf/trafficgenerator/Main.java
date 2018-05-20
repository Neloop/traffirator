package cz.polankam.pcrf.trafficgenerator;

import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Config;
import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioFactory;
import cz.polankam.pcrf.trafficgenerator.scenario.factory.SimpleDemoScenarioFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;


public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private Summary summary;
    private String[] args;
    private CommandLine cmd;
    private PrintStream summaryOut;

    protected Main(String[] args) throws ParseException {
        this.args = args;
        summary = new Summary();
        summaryOut = System.out;
    }

    protected void processCmdArguments() throws ParseException {
        Options options = new Options();

        options.addOption(Option.builder("c").longOpt("callCount").argName("count").hasArg()
                .desc("Number of call which will be executed, -1 if there is no limit, default is 1").build());
        options.addOption(Option.builder("i").longOpt("initialScenarios").argName("count").hasArg()
                .desc("Number of scenarios which will be created at the beginning, default is 1").build());
        options.addOption(Option.builder("t").longOpt("threadCount").argName("count").hasArg()
                .desc("Number threads which will handle sending and receiving of messages, default is 1").build());
        options.addOption(Option.builder("s").longOpt("summary").argName("file").hasArg()
                .desc("File to which summary should be written, default is stdout").build());
        options.addOption(new Option("h", "help", false, "Print this message"));

        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("traffirator", "Traffic generator for PCRF server within LTE network", options, "");
            System.exit(0);
        }
    }

    protected Config getClientConfig() throws Exception {
        int callCount = 1;
        int initialScenariosCount = 1;
        int threadCount = 1;

        if (cmd.hasOption("callCount")) {
            callCount = Integer.parseInt(cmd.getOptionValue("callCount"));
        }

        if (cmd.hasOption("initialScenarios")) {
            initialScenariosCount = Integer.parseUnsignedInt(cmd.getOptionValue("initialScenarios"));
        }

        if (cmd.hasOption("threadCount")) {
            threadCount = Integer.parseUnsignedInt(cmd.getOptionValue("threadCount"));
        }

        if (cmd.hasOption("summary")) {
            summaryOut = new PrintStream(cmd.getOptionValue("summary"));
        }

        return new Config(callCount, initialScenariosCount, threadCount);
    }

    protected ScenarioFactory getScenarioFactory() {
        return new SimpleDemoScenarioFactory();
    }

    protected void waitForConnections() throws Exception {
        //wait for connection to peer
        log.info("Waiting for connection to peer...");
        Thread.sleep(5000);
        log.info("Enough waiting, lets roll");

    }

    protected void controlClient(Client client) {
        System.out.println("Write number of scenarios which should be active or 'exit':");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = null;
            Integer number = null;
            try {
                line = br.readLine();
                number = Integer.parseUnsignedInt(line);
            } catch (NumberFormatException | IOException e) {}

            if ("exit".equalsIgnoreCase(line)) {
                client.finish();
                log.info("User requested 'exit' action");
                break;
            } else if (number != null) {
                client.setScenariosCount(number);
                summary.addChange(number);
                System.out.println("Current count of active scenarios: " + client.getScenariosCount());
            }
        }
    }

    protected void start() throws Exception {
        log.info("****************************************");
        log.info("* STARTING TRAFFIRATOR *****************");
        log.info("****************************************");

        Config config = getClientConfig();
        Client client = new Client(config, getScenarioFactory());

        // initialization
        client.init();
        summary.setClientConfig(config);

        //
        waitForConnections();

        // start sending/receiving messages on gx and rx interfaces
        summary.setStart();
        client.start();

        // if this is infinite execution allow user to change number of active scenarios
        if (config.getCallCount() == -1) {
            new Thread(() -> {
                controlClient(client);
            }).start();
        }

        // wait till both client are finished
        while (!client.finished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
        }

        try {
            // do not forget to destroy allocated stacks
            client.destroy();
            log.info("All done... Good bye!");
        } finally {
            summary.setEnd();
            summary.printSummary(summaryOut);
            summaryOut.close();
        }
    }

    public void findDeadLocks()
    {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        long[] ids = tmx.findDeadlockedThreads();
        if (ids != null ) {
            ThreadInfo[] infos = tmx.getThreadInfo(ids,true,true);
            System.out.println("Following Threads are deadlocked");
            for (ThreadInfo info : infos) {
                System.out.println(info);
                System.out.println("Stacktrace:");
                for (StackTraceElement ste : info.getStackTrace()) {
                    System.out.println("  " + ste);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Main main = new Main(args);
            main.processCmdArguments();
            main.start();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            System.exit(1);
        }

        // for some reasons jdiameter keeps some threads started even after destroying stack and such... so kill it
        System.exit(0);
    }

}
