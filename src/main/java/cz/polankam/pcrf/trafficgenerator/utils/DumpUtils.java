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
 * Static utils for dump/print of Avps and other stuff.
 */
public class DumpUtils {

    private static final Logger log = Logger.getLogger(DumpUtils.class);

    private static final String dictionaryFile = "dictionary.xml";
    private static final AvpDictionary dictionary = AvpDictionary.INSTANCE;

    static {
        try {
            // Parse dictionary, it is used for user friendly info.
            dictionary.parseDictionary(DumpUtils.class.getClassLoader().getResourceAsStream(dictionaryFile));
            log.info("AVP Dictionary successfully parsed.");
        } catch (Exception e) {
            log.error(e);
        }
    }


    public static void dumpMessage(Message message, boolean sending) {
        log.info((sending ? "Sending " : "Received ") + (message.isRequest() ? "Request: " : "Answer: ") + commandCodeToString(message.getCommandCode()) + " (E2E:"
                + message.getEndToEndIdentifier() + "; HBH:" + message.getHopByHopIdentifier() + "; AppID:" + message.getApplicationId() + ")");

        if (!log.isDebugEnabled()) {
            return;
        }

        try {
            printAvps(message.getAvps());
        } catch (AvpDataException e) {
            e.printStackTrace();
        }
    }

    public static String commandCodeToString(int cmd) {
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

    public static void printAvps(AvpSet avpSet) throws AvpDataException {
        log.debug("AVPS[" + avpSet.size() + "]: \n");
        printAvpsAux(avpSet, 0);
    }

    /**
     * Prints the AVPs present in an AvpSet with a specified 'tab' level
     *
     * @param avpSet the AvpSet containing the AVPs to be printed
     * @param level an int representing the number of 'tabs' to make a pretty
     * print
     * @throws AvpDataException
     */
    private static void printAvpsAux(AvpSet avpSet, int level) throws AvpDataException {
        String prefix = "                      ".substring(0, level * 2);

        for (Avp avp : avpSet) {
            AvpRepresentation avpRep = dictionary.getAvp(avp.getCode(), avp.getVendorId());

            if (avpRep != null && avpRep.getType().equals("Grouped")) {
                log.debug(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\">");
                printAvpsAux(avp.getGrouped(), level + 1);
                log.debug(prefix + "</avp>");
            } else if (avpRep != null) {
                String value = "";

                if (avpRep.getType().equals("Integer32")) {
                    value = String.valueOf(avp.getInteger32());
                } else if (avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64")) {
                    value = String.valueOf(avp.getInteger64());
                } else if (avpRep.getType().equals("Unsigned32")) {
                    value = String.valueOf(avp.getUnsigned32());
                } else if (avpRep.getType().equals("Float32")) {
                    value = String.valueOf(avp.getFloat32());
                } else //value = avp.getOctetString();
                {
                    value = new String(avp.getOctetString(), StandardCharsets.UTF_8);
                }

                log.debug(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId()
                        + "\" value=\"" + value + "\" />");
            }
        }
    }
}
