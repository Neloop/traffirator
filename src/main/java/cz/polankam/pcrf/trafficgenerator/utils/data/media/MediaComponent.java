package cz.polankam.pcrf.trafficgenerator.utils.data.media;

/**
 * Represents a Media-Component-Description AVP and its sub components.
 * Can be used for generation of the random parts of the description.
 */
public class MediaComponent {

    private final int bandwidth;
    private final int componentNumber;
    private final CodecData codecData;
    private final MediaSubComponent firstSubComponent;
    private final MediaSubComponent secondSubComponent;

    /**
     * Constructor.
     * @param bandwidth width of a band
     * @param componentNumber number of the component
     * @param codecData codec data corresponding with the component
     * @param firstSubComponent definition of the first sub-component
     * @param secondSubComponent definition of the second sub-component
     */
    public MediaComponent(int bandwidth, int componentNumber, CodecData codecData, MediaSubComponent firstSubComponent, MediaSubComponent secondSubComponent) {
        this.bandwidth = bandwidth;
        this.componentNumber = componentNumber;
        this.codecData = codecData;
        this.firstSubComponent = firstSubComponent;
        this.secondSubComponent = secondSubComponent;
    }

    /**
     * Get the bandwidth associated with the component.
     * @return bandwidth number
     */
    public int getBandwidth() {
        return bandwidth;
    }

    /**
     * Get the component number associated with the component.
     * @return identifier of this component
     */
    public int getComponentNumber() {
        return componentNumber;
    }

    /**
     * Get the codec data corresponding with the component.
     * @return structured codec data
     */
    public CodecData getCodecData() {
        return codecData;
    }

    /**
     * Get the first sub-component of this component.
     * @return structured sub-component
     */
    public MediaSubComponent getFirstSubComponent() {
        return firstSubComponent;
    }

    /**
     * Get the second sub-component of this component.
     * @return structured sub-component
     */
    public MediaSubComponent getSecondSubComponent() {
        return secondSubComponent;
    }
}
