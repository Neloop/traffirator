package cz.polankam.pcrf.trafficgenerator.utils.data.flow;

public class FlowDescription {
    private final String in;
    private final String out;

    public FlowDescription(String in, String out) {
        this.in = in;
        this.out = out;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }
}
