package cz.polankam.pcrf.trafficgenerator.utils.data.media;

public class MediaComponent {
    private final int bandwidth;
    private final int componentNumber;
    private final CodecData codecData;
    private final MediaSubComponent firstSubComponent;
    private final MediaSubComponent secondSubComponent;

    public MediaComponent(int bandwidth, int componentNumber, CodecData codecData, MediaSubComponent firstSubComponent, MediaSubComponent secondSubComponent) {
        this.bandwidth = bandwidth;
        this.componentNumber = componentNumber;
        this.codecData = codecData;
        this.firstSubComponent = firstSubComponent;
        this.secondSubComponent = secondSubComponent;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getComponentNumber() {
        return componentNumber;
    }

    public CodecData getCodecData() {
        return codecData;
    }

    public MediaSubComponent getFirstSubComponent() {
        return firstSubComponent;
    }

    public MediaSubComponent getSecondSubComponent() {
        return secondSubComponent;
    }
}
