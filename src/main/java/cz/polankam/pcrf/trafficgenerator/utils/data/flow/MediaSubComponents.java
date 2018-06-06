package cz.polankam.pcrf.trafficgenerator.utils.data.flow;

public class MediaSubComponents {
    private final MediaSubComponent first;
    private final MediaSubComponent second;

    public MediaSubComponents(MediaSubComponent first, MediaSubComponent second) {
        this.first = first;
        this.second = second;
    }

    public MediaSubComponent getFirst() {
        return first;
    }

    public MediaSubComponent getSecond() {
        return second;
    }
}
