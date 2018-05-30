package cz.polankam.pcrf.trafficgenerator.scenario.actions.impl.factory;

import cz.polankam.pcrf.trafficgenerator.scenario.ScenarioContext;
import cz.polankam.pcrf.trafficgenerator.utils.AvpUtils;
import cz.polankam.pcrf.trafficgenerator.utils.RandomGenerator;
import cz.polankam.pcrf.trafficgenerator.utils.Vendor;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.api.gx.events.GxCreditControlRequest;
import org.jdiameter.common.impl.app.gx.GxCreditControlRequestImpl;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;


public class GxRequestsFactory {

    public static GxCreditControlRequest createCcrI(ScenarioContext context) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        avps.addAvp(Avp.CC_REQUEST_TYPE, 1, true, false);
        avps.addAvp(Avp.CC_REQUEST_NUMBER, requestNumber, true, false);
        avps.addAvp(AvpUtils.FRAMED_IP_ADDRESS, (Integer) state.get("framed_ip"), Vendor.COMMON, true, false);
        avps.addAvp(AvpUtils.IP_CAN_TYPE, RandomGenerator.randomIpCanType(), true, false);
        avps.addAvp(AvpUtils.RAT_TYPE, RandomGenerator.randomRatType(), true, false);
        avps.addAvp(AvpUtils.AN_GW_ADDRESS, (InetAddress) state.get("an_gw_address"), Vendor.TGPP, false, false);
        avps.addAvp(AvpUtils.GPP_MS_TIMEZONE, 4000, true, false);
        avps.addAvp(AvpUtils.CALLED_STATION_ID, (String) state.get("called_station"), Vendor.COMMON, true, false, true);
        avps.addAvp(AvpUtils.BEARER_USAGE, 0, Vendor.TGPP, true, false);
        avps.addAvp(AvpUtils.ONLINE, 0, Vendor.TGPP, true, false);
        avps.addAvp(AvpUtils.OFFLINE, 0, Vendor.TGPP, true, false);
        avps.addAvp(AvpUtils.ACCESS_NETWORK_CHARGING_ADDRESS, (InetAddress) state.get("an_gw_address"), Vendor.TGPP, true, false);

        AvpSet subscrMSISDN = avps.addGroupedAvp(Avp.SUBSCRIPTION_ID, true, false);
        subscrMSISDN.addAvp(Avp.SUBSCRIPTION_ID_TYPE, 0, true, false);
        subscrMSISDN.addAvp(Avp.SUBSCRIPTION_ID_DATA, (Integer) state.get("msisdn"), true, false, true);

        AvpSet suppFeatures = avps.addGroupedAvp(Avp.SUPPORTED_FEATURES, Vendor.TGPP, true, false);
        suppFeatures.addAvp(Avp.FEATURE_LIST_ID, 1, Vendor.TGPP, true, false);
        suppFeatures.addAvp(Avp.FEATURE_LIST, 2, Vendor.TGPP, true, false);

        AvpSet userEquipment = avps.addGroupedAvp(Avp.USER_EQUIPMENT_INFO, Vendor.TGPP, false, false);
        userEquipment.addAvp(Avp.USER_EQUIPMENT_INFO_TYPE, 0, Vendor.TGPP, false, false);
        userEquipment.addAvp(Avp.USER_EQUIPMENT_INFO_VALUE, (String) state.get("imei"), Vendor.TGPP, false, false, true);

        AvpSet epsBearer = avps.addGroupedAvp(AvpUtils.DEFAULT_EPS_BEARER_QOS, Vendor.TGPP, true, false);
        epsBearer.addAvp(Avp.QOS_CLASS_IDENTIFIER, 5, Vendor.TGPP, true, false);
        AvpSet allocRetPrior = epsBearer.addGroupedAvp(Avp.ALLOCATION_RETENTION_PRIORITY, Vendor.TGPP, true, false);
        allocRetPrior.addAvp(Avp.PRIORITY_LEVEL, 11, Vendor.TGPP, true, false);
        allocRetPrior.addAvp(AvpUtils.PRE_EMPTION_CAPABILITY, 1, Vendor.TGPP, true, false);
        allocRetPrior.addAvp(AvpUtils.PRE_EMPTION_VULNERABILITY, 1, Vendor.TGPP, true, false);

        AvpSet accessNetCharg = avps.addGroupedAvp(AvpUtils.ACCESS_NETWORK_CHARGING_IDENTIFIER_GX, Vendor.TGPP, true, false);
        accessNetCharg.addAvp(Avp.ACCESS_NETWORK_CHARGING_IDENTIFIER_VALUE, (String) state.get("an_ci_gw"), Vendor.TGPP, true, false, true);

        int bitrate = RandomGenerator.randomBitrate();
        AvpSet qosInfo = avps.addGroupedAvp(Avp.QOS_INFORMATION, Vendor.TGPP, true, false);
        qosInfo.addAvp(Avp.APN_AGGREGATE_MAX_BITRATE_UL, bitrate, Vendor.TGPP, true, false);
        qosInfo.addAvp(Avp.APN_AGGREGATE_MAX_BITRATE_DL, bitrate, Vendor.TGPP, true, false);
        qosInfo.addAvp(Avp.GPP_USER_LOCATION_INFO, RandomGenerator.randomUserLocation(), Vendor.TGPP, true, false, true);

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrT(ScenarioContext context) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        avps.addAvp(Avp.CC_REQUEST_TYPE, 3, true, false);
        avps.addAvp(Avp.CC_REQUEST_NUMBER, requestNumber, true, false);
        avps.addAvp(AvpUtils.FRAMED_IP_ADDRESS, (Integer) state.get("framed_ip"), Vendor.COMMON, true, false);
        avps.addAvp(AvpUtils.CALLED_STATION_ID, (String) state.get("called_station"), true, false, true);
        avps.addAvp(AvpUtils.ACCESS_NETWORK_CHARGING_ADDRESS, (InetAddress) state.get("an_gw_address"), true, false);
        avps.addAvp(Avp.TERMINATION_CAUSE, 1, true, false);

        AvpSet subscrMSISDN = avps.addGroupedAvp(Avp.SUBSCRIPTION_ID, true, false);
        subscrMSISDN.addAvp(Avp.SUBSCRIPTION_ID_TYPE, 0, true, false);
        subscrMSISDN.addAvp(Avp.SUBSCRIPTION_ID_DATA, (Integer) state.get("msisdn"), true, false, true);

        AvpSet userEquipment = avps.addGroupedAvp(Avp.USER_EQUIPMENT_INFO, Vendor.TGPP, false, false);
        userEquipment.addAvp(Avp.USER_EQUIPMENT_INFO_TYPE, 0, Vendor.TGPP, false, false);
        userEquipment.addAvp(Avp.USER_EQUIPMENT_INFO_VALUE, (String) state.get("imei"), Vendor.TGPP, false, false, true);

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrU(ScenarioContext context, boolean isSubscriptionId) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

    public static GxCreditControlRequest createCcrU_LostConnection(ScenarioContext context) throws Exception {
        ClientGxSession session = context.getGxSession();
        ConcurrentHashMap<String, Object> state = context.getState();
        int requestNumber = (int) state.get("cc_request_number");
        state.put("cc_request_number", requestNumber + 1);

        // *** CREATE REQUEST

        GxCreditControlRequestImpl req = new GxCreditControlRequestImpl(session, context.getGxRealm(), context.getGxServerUri());
        AvpSet avps = req.getMessage().getAvps();

        // TODO

        // *** RETURN REQUEST

        return req;
    }

}
