package cz.polankam.pcrf.trafficgenerator.utils.data.media;

public class MediaSubComponent {
    private final int flowNumber;
    private final FlowDescription flowDescription;

    public MediaSubComponent(int flowNumber, FlowDescription flowDescription) {
        this.flowNumber = flowNumber;
        this.flowDescription = flowDescription;
    }

    public int getFlowNumber() {
        return flowNumber;
    }

    public FlowDescription getFlowDescription() {
        return flowDescription;
    }
}
