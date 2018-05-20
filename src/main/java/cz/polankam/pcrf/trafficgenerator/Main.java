package cz.polankam.pcrf.trafficgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.polankam.pcrf.trafficgenerator.client.Client;
import cz.polankam.pcrf.trafficgenerator.config.Config;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    private final Summary summary;
    private final String[] args;
    private CommandLine cmd;
    private PrintStream summaryOut;
    private ScheduledExecutorService executor;

    protected Main(String[] args) throws ParseException {
        this.args = args;
        summary = new Summary();
        summaryOut = System.out;
    }

    protected void processCmdArguments() throws ParseException {
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

    protected Config getClientConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String filename = cmd.getOptionValue("config");
        try (InputStream configFile = new FileInputStream(filename)) {
            Config config = mapper.readValue(configFile, Config.class);
            summaryOut = new PrintStream(config.getSummary());
            return config;
        }
    }

    protected void waitForConnections() throws Exception {
        //wait for connection to peer
        log.info("Waiting for connection to peer...");
        Thread.sleep(5000);
        log.info("Enough waiting, lets roll");

    }

    protected void start() throws Exception {
        log.info("****************************************");
        log.info("* STARTING TRAFFIRATOR *****************");
        log.info("****************************************");

        Config config = getClientConfig();
        // prepare executor service based on given thread count and pass it to the client
        executor = Executors.newScheduledThreadPool(config.getThreadCount());
        Client client = new Client(config, executor);

        // initialization
        client.init();
        summary.setClientConfig(config);

        //
        waitForConnections();

        // start sending/receiving messages on gx and rx interfaces
        summary.setStart();
        client.start();

        // schedule ending of the execution from the configuration
        executor.schedule(() -> {
            client.finish();
            log.info("End trigger activated");
        }, config.getEnd(), TimeUnit.MILLISECONDS);

        // wait till both is finished
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
