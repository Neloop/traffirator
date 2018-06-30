package cz.polankam.pcrf.trafficgenerator.utils.data.media;

/**
 * Class which represents media sub-component under the media component.
 * Can be used within generation of random data for the message.
 */
public class MediaSubComponent {

    private final int flowNumber;
    private final FlowDescription flowDescription;

    /**
     * Constructor.
     * @param flowNumber number of the flow in sub-component
     * @param flowDescription flow description in sub-component
     */
    public MediaSubComponent(int flowNumber, FlowDescription flowDescription) {
        this.flowNumber = flowNumber;
        this.flowDescription = flowDescription;
    }

    /**
     * Get the flow number associated with the sub-component.
     * @return identifier of the flow
     */
    public int getFlowNumber() {
        return flowNumber;
    }

    /**
     * Get the flow description corresponding with the sub-component.
     * @return description of the flow
     */
    public FlowDescription getFlowDescription() {
        return flowDescription;
    }
}
