package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.utils.AvpCode;
import cz.polankam.pcrf.trafficgenerator.utils.data.DataProvider;
import cz.polankam.pcrf.trafficgenerator.utils.Vendor;
import cz.polankam.pcrf.trafficgenerator.utils.data.media.MediaComponent;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxSessionTermRequest;
import org.jdiameter.common.impl.app.rx.RxAARequestImpl;
import org.jdiameter.common.impl.app.rx.RxSessionTermRequestImpl;

import java.util.concurrent.ConcurrentHashMap;


public class RxRequestsFactory {

    public static RxAARequest createAar(ScenarioContext context, boolean changeMedia) throws Exception {
        ClientRxSession session = context.getRxSession();
        ConcurrentHashMap<String, Object> state = context.getState();

        // *** CREATE REQUEST

        RxAARequestImpl req = new RxAARequestImpl(session, context.getRxRealm(), context.getRxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        avps.addAvp(AvpCode.FRAMED_IP_ADDRESS, (Integer) state.get("framed_ip"), Vendor.COMMON, true, false);
        avps.addAvp(Avp.AF_CHARGING_IDENTIFIER, (String) state.get("af_charging_identifier"), Vendor.TGPP, true, false, true);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 1, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 2, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 3, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 4, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 7, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 9, Vendor.TGPP, true, false);
        avps.addAvp(AvpCode.SPECIFIC_ACTION, 12, Vendor.TGPP, true, false);

        MediaComponent providedMedia;
        if (changeMedia) {
            providedMedia = DataProvider.randomMediaComponent();
            // for further usage, save media component into state
            state.put("media_component_description", providedMedia);
        } else {
            // if media should not be changed, load them from state
            providedMedia = (MediaComponent) state.get("media_component_description");
        }

        AvpSet mediaCompDesc = avps.addGroupedAvp(AvpCode.MEDIA_COMPONENT_DESCRIPTION, Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(AvpCode.MEDIA_COMPONENT_NUMBER, providedMedia.getComponentNumber(), Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(AvpCode.MEDIA_TYPE, 0, Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(Avp.MAX_REQUESTED_BANDWIDTH_UL, providedMedia.getBandwidth(), Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(Avp.MAX_REQUESTED_BANDWIDTH_DL, providedMedia.getBandwidth(), Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(AvpCode.FLOW_STATUS, 2, Vendor.TGPP, true, false);
        mediaCompDesc.addAvp(AvpCode.CODEC_DATA, providedMedia.getCodecData().getUplink(), Vendor.TGPP, true, false, true);
        mediaCompDesc.addAvp(AvpCode.CODEC_DATA, providedMedia.getCodecData().getDownlink(), Vendor.TGPP, true, false, true);
        mediaCompDesc.addAvp(AvpCode.AF_APPLICATION_IDENTIFIER, "sbc", Vendor.TGPP, true, false, true);

        AvpSet mediaSubComp = mediaCompDesc.addGroupedAvp(AvpCode.MEDIA_SUB_COMPONENT, Vendor.TGPP, true, false);
        mediaSubComp.addAvp(AvpCode.FLOW_NUMBER, providedMedia.getFirstSubComponent().getFlowNumber(), Vendor.TGPP, true, false);
        mediaSubComp.addAvp(AvpCode.FLOW_DESCRIPTION, providedMedia.getFirstSubComponent().getFlowDescription().getIn(), Vendor.TGPP, true, false, true);
        mediaSubComp.addAvp(AvpCode.FLOW_DESCRIPTION, providedMedia.getFirstSubComponent().getFlowDescription().getOut(), Vendor.TGPP, true, false, true);
        mediaSubComp.addAvp(AvpCode.FLOW_STATUS, 2, Vendor.TGPP, true, false);

        mediaSubComp = mediaCompDesc.addGroupedAvp(AvpCode.MEDIA_SUB_COMPONENT, Vendor.TGPP, true, false);
        mediaSubComp.addAvp(AvpCode.FLOW_NUMBER, providedMedia.getSecondSubComponent().getFlowNumber(), Vendor.TGPP, true, false);
        mediaSubComp.addAvp(AvpCode.FLOW_DESCRIPTION, providedMedia.getSecondSubComponent().getFlowDescription().getIn(), Vendor.TGPP, true, false, true);
        mediaSubComp.addAvp(AvpCode.FLOW_DESCRIPTION, providedMedia.getSecondSubComponent().getFlowDescription().getOut(), Vendor.TGPP, true, false, true);
        mediaSubComp.addAvp(AvpCode.FLOW_USAGE, 1, Vendor.TGPP, true, false);
        mediaSubComp.addAvp(AvpCode.FLOW_STATUS, 2, Vendor.TGPP, true, false);

        // *** RETURN REQUEST

        return req;
    }

    public static RxSessionTermRequest createStr(ScenarioContext context) throws Exception {
        ClientRxSession session = context.getRxSession();

        // *** CREATE REQUEST

        RxSessionTermRequestImpl req = new RxSessionTermRequestImpl(session, context.getRxRealm(), context.getRxServerUri());
        AvpSet avps = req.getMessage().getAvps();
        avps.addAvp(Avp.TERMINATION_CAUSE, 1, true, false);

        // *** RETURN REQUEST

        return req;
    }

}
