package cz.polankam.pcrf.trafficgenerator.utils.data.media;

/**
 * Represents the Codec-Data AvP inside the Media-Component-Description.
 * Can be used in generation of random data for the message.
 */
public class CodecData {

    private final String uplink;
    private final String downlink;

    /**
     * Constructor.
     * @param uplink definition of uplink description
     * @param downlink definition of downlink description
     */
    public CodecData(String uplink, String downlink) {
        this.uplink = uplink;
        this.downlink = downlink;
    }

    /**
     * Get the textual description of uplink codec information.
     * @return textual representation of uplink
     */
    public String getUplink() {
        return uplink;
    }

    /**
     * Get the textual description of downlink codec information.
     * @return textual representation of downlink
     */
    public String getDownlink() {
        return downlink;
    }
}
