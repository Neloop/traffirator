package cz.polankam.pcrf.trafficgenerator.utils.data.media;

/**
 * Class which represents flow description within media sub-component.
 * Can be used for generation of the random parts of the description.
 */
public class FlowDescription {

    private final String in;
    private final String out;

    /**
     * Constructor.
     * @param in input flow description
     * @param out output flow description
     */
    public FlowDescription(String in, String out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Get input flow description in the media component.
     * @return textual flow description
     */
    public String getIn() {
        return in;
    }

    /**
     * Get output flow description in the media component.
     * @return textual flow description
     */
    public String getOut() {
        return out;
    }
}
