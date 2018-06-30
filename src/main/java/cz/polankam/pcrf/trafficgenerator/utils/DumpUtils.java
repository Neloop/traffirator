package cz.polankam.pcrf.trafficgenerator.utils;

import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Static utils for dump/print of AVPs and other stuff.
 */
public class DumpUtils {

    private static final Logger logger = Logger.getLogger(DumpUtils.class);

    private static final String dictionaryFile = "dictionary.xml";
    private static final AvpDictionary dictionary = AvpDictionary.INSTANCE;

    static {
        try {
            // Parse dictionary, it is used for user friendly info.
            dictionary.parseDictionary(DumpUtils.class.getClassLoader().getResourceAsStream(dictionaryFile));
            logger.info("AVP Dictionary successfully parsed.");
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * Dump given message object into the logger, basic information are logged in debug mode, more verbose information
     * use trace log level.
     * @param message message structure containing AVPs
     * @param sending if true the message was sent by the generator, otherwise it was received
     */
    public static void dumpMessage(Message message, boolean sending) {
        logger.debug((sending ? "Sending " : "Received ") + (message.isRequest() ? "Request: " : "Answer: ") + commandCodeToString(message.getCommandCode()) + " (E2E:"
                + message.getEndToEndIdentifier() + "; HBH:" + message.getHopByHopIdentifier() + "; AppID:" + message.getApplicationId() + "; SessID:" + message.getSessionId() + ")");

        if (!logger.isTraceEnabled()) {
            return;
        }

        try {
            printAvps(message.getAvps());
        } catch (AvpDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * From the given command code derive the command name.
     * @param cmd command code
     * @return textual interpretation of the command code
     */
    private static String commandCodeToString(int cmd) {
        switch (cmd) {
            case 258:
                return "Re-Auth";
            case 275:
                return "Session-Termination";
            case 272:
                return "Credit-Control";
            case 265:
                return "AA";
            case 274:
                return "Abort-Session";
            default:
                return "";
        }
    }

    /**
     * Prints the AVPs present in an AvpSet.
     * @param avpSet the AvpSet containing the AVPs to be printed
     * @throws AvpDataException in case of error
     */
    private static void printAvps(AvpSet avpSet) throws AvpDataException {
        logger.trace("AVPS[" + avpSet.size() + "]: \n");
        printAvpsAux(avpSet, 0);
    }

    /**
     * Prints the AVPs present in an AvpSet with a specified 'tab' level.
     * @param avpSet the AvpSet containing the AVPs to be printed
     * @param level an int representing the number of 'tabs' to make a pretty
     * print
     * @throws AvpDataException in case of error
     */
    private static void printAvpsAux(AvpSet avpSet, int level) throws AvpDataException {
        String prefix = "                      ".substring(0, level * 2);

        for (Avp avp : avpSet) {
            AvpRepresentation avpRep = dictionary.getAvp(avp.getCode(), avp.getVendorId());

            if (avpRep != null && avpRep.getType().equals("Grouped")) {
                logger.trace(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\">");
                printAvpsAux(avp.getGrouped(), level + 1);
                logger.trace(prefix + "</avp>");
            } else if (avpRep != null) {
                String value;

                if (avpRep.getType().equals("Integer32")) {
                    value = String.valueOf(avp.getInteger32());
                } else if (avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64")) {
                    value = String.valueOf(avp.getInteger64());
                } else if (avpRep.getType().equals("Unsigned32")) {
                    value = String.valueOf(avp.getUnsigned32());
                } else if (avpRep.getType().equals("Float32")) {
                    value = String.valueOf(avp.getFloat32());
                } else {
                    value = new String(avp.getOctetString(), StandardCharsets.UTF_8);
                }

                logger.trace(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId()
                        + "\" value=\"" + value + "\" />");
            }
        }
    }
}
