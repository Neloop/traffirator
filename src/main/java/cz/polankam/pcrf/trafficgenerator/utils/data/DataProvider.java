package cz.polankam.pcrf.trafficgenerator.utils.data;

import cz.polankam.pcrf.trafficgenerator.utils.data.media.CodecData;
import cz.polankam.pcrf.trafficgenerator.utils.data.media.FlowDescription;
import cz.polankam.pcrf.trafficgenerator.utils.data.media.MediaSubComponent;
import cz.polankam.pcrf.trafficgenerator.utils.data.media.MediaComponent;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.IPv4Type;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class DataProvider {

    private static final MockNeat mockNeat = MockNeat.threadLocal();
    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final Random random = new Random();
    private static final int[] ratTypes = {
            1004 // EUTRAN
    };
    private static final int[] ipCanTypes = {
            5 // 3GPP-EPS
    };
    private static final int[] bitrates = {
            64000,
            128000
    };

    private static String randomAlphanumString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
        }
        return sb.toString();
    }

    private static InetAddress randomIpAddressClassA() throws UnknownHostException {
        return InetAddress.getByName(mockNeat.ipv4s().type(IPv4Type.CLASS_A).val());
    }

    private static int randomPort() {
        return random.nextInt(65536);
    }


    public static int randomFramedIp() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static InetAddress randomAnGwAddress() throws UnknownHostException {
        return randomIpAddressClassA();
    }

    public static int randomMSISDN() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static String randomIMEI() {
        int length = 15;
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(random.nextInt(NUMERIC.length())));
        }
        return sb.toString();
    }

    public static String randomAnCiGx() {
        return randomAlphanumString(40);
    }

    public static int randomRatType() {
        return ratTypes[random.nextInt(ratTypes.length)];
    }

    public static int randomIpCanType() {
        return ipCanTypes[random.nextInt(ipCanTypes.length)];
    }

    public static int randomBitrate() {
        return bitrates[random.nextInt(bitrates.length)];
    }

    public static String randomUserLocation() {
        return randomAlphanumString(26);
    }

    public static String randomAfChargingIdentifier() {
        return randomAlphanumString(50);
    }

    public static MediaComponent randomMediaComponent() throws UnknownHostException {
        int bandwidth = randomBitrate();
        int componentNumber = random.nextInt(Integer.MAX_VALUE);
        int flowNumber = random.nextInt(Integer.MAX_VALUE);
        int lowPort = randomPort();
        int highPort = randomPort();

        String uplink = "uplink offer m=audio " + lowPort + " ...";
        String downlink = "downlink answer m=audio " + highPort + " ...";
        CodecData codecData = new CodecData(uplink, downlink);

        String firstIn = "permit in ip from " + randomIpAddressClassA() + " " + lowPort + " to " + randomIpAddressClassA() + " " + highPort;
        String firstOut = "permit out ip from " + randomIpAddressClassA() + " " + highPort + " to " + randomIpAddressClassA() + " " + lowPort;
        MediaSubComponent first = new MediaSubComponent(flowNumber, new FlowDescription(firstIn, firstOut));

        String secondIn = "permit in ip from " + randomIpAddressClassA() + " " + (lowPort + 1) + " to " + randomIpAddressClassA() + " " + (highPort + 1);
        String secondOut = "permit out ip from " + randomIpAddressClassA() + " " + (highPort + 1) + " to " + randomIpAddressClassA() + " " + (lowPort + 1);
        MediaSubComponent second = new MediaSubComponent(flowNumber + 1, new FlowDescription(secondIn, secondOut));

        return new MediaComponent(bandwidth, componentNumber, codecData, first, second);
    }
}
