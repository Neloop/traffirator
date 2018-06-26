package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;


public class Profile {

    private int burstLimit;
    private long end;
    private List<ProfileItem> flow;


    /**
     * The burst limit corresponds to the max number of new calls that generator can create in a period of one second.
     * @return
     */
    public int getBurstLimit() {
        return burstLimit;
    }

    public void setBurstLimit(int burstLimit) {
        this.burstLimit = burstLimit;
    }

    /**
     * When will the execution ends, in seconds.
     * @return
     */
    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    /**
     * Testing profile configuration of items.
     * @return
     */
    public List<ProfileItem> getFlow() {
        return flow;
    }

    public void setFlow(List<ProfileItem> flow) {
        this.flow = flow;
    }

}
