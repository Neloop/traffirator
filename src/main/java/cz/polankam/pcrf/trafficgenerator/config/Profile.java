package cz.polankam.pcrf.trafficgenerator.config;

import java.util.List;

/**
 * Profile class holds the configuration data concerning the execution flow.
 */
public class Profile {

    private int burstLimit;
    private long end;
    private List<ProfileItem> flow;


    /**
     * The burst limit corresponds to the max number of new scenarios that generator can create in a period of one second.
     * @return number of new scenarios
     */
    public int getBurstLimit() {
        return burstLimit;
    }

    /**
     * Set the burst limit of this profile.
     * @param burstLimit number of scenarios
     */
    public void setBurstLimit(int burstLimit) {
        this.burstLimit = burstLimit;
    }

    /**
     * When will the execution ends, in seconds.
     * @return number of seconds for which the generator will run
     */
    public long getEnd() {
        return end;
    }

    /**
     * Set the number of seconds for which the generator will run.
     * @param end number of seconds
     */
    public void setEnd(long end) {
        this.end = end;
    }

    /**
     * Configuration of the profile flow.
     * @return list of profile items
     */
    public List<ProfileItem> getFlow() {
        return flow;
    }

    /**
     * Set the profile flow in the form of profile items.
     * @param flow list of profile items
     */
    public void setFlow(List<ProfileItem> flow) {
        this.flow = flow;
    }
}
