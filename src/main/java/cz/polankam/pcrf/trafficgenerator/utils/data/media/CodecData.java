package cz.polankam.pcrf.trafficgenerator.utils.data.media;

public class CodecData {
    private final String uplink;
    private final String downlink;

    public CodecData(String uplink, String downlink) {
        this.uplink = uplink;
        this.downlink = downlink;
    }

    public String getUplink() {
        return uplink;
    }

    public String getDownlink() {
        return downlink;
    }
}
