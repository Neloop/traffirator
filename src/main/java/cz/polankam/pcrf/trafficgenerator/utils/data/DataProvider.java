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

/**
 * Data providers for the generation of random or semi-random data for Diameter messages.
 * Internally the <code>Random</code> class is used for the random data generation.
 */
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

    /**
     * Generation of random alpha-numeric string with given length.
     * @param length resulting length of generated string
     * @return randomly generated alphanum string
     */
    private static String randomAlphanumString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * Generate random class A IPv4 address.
     * @return class A IPv4 address
     * @throws UnknownHostException in case of parsing error
     */
    private static InetAddress randomIpAddressClassA() throws UnknownHostException {
        return InetAddress.getByName(mockNeat.ipv4s().type(IPv4Type.CLASS_A).val());
    }

    /**
     * Get the integer representation of the given IP address.
     * @param ip IP address
     * @return integer representing IP address
     */
    private static int ipAddressToInt(InetAddress ip) {
        byte[] bytes = ip.getAddress();
        int val = 0;
        for (byte aByte : bytes) {
            val <<= 8;
            val |= aByte & 0xff;
        }
        return val;
    }

    /**
     * Generate random network port.
     * @return port number
     */
    private static int randomPort() {
        return random.nextInt(65536);
    }


    /**
     * Generate random value for Framed-IP-Address AVP.
     * @return integer representation of IP address
     * @throws UnknownHostException in case of parsing error
     */
    public static int randomFramedIp() throws UnknownHostException {
        return ipAddressToInt(randomIpAddressClassA());
    }

    /**
     * Generate random value for AN-GW-Address AVP.
     * @return IPv4 address
     * @throws UnknownHostException in case of parsing error
     */
    public static InetAddress randomAnGwAddress() throws UnknownHostException {
        return randomIpAddressClassA();
    }

    /**
     * Generate random MSISDN identifier of the caller.
     * @return integer representation of the MSISDN
     */
    public static int randomMSISDN() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Generate random IMSI identifier of the device.
     * @return integer representation of the IMSI
     */
    public static int randomIMSI() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Generate random IMEI identifier of the device.
     * @return 15-character long textual identifier of the IMEI
     */
    public static String randomIMEI() {
        int length = 15;
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(random.nextInt(NUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * Generate random value for Access-Network-Charging-Identifier-Value AVP.
     * @return 40-character long alphanum string
     */
    public static String randomAnCiGx() {
        return randomAlphanumString(40);
    }

    /**
     * Generate random value for RAT-Type AVP.
     * @return random RAT type
     */
    public static int randomRatType() {
        return ratTypes[random.nextInt(ratTypes.length)];
    }

    /**
     * Generate random value for IP-CAN-Type AVP.
     * @return random IP CAN type
     */
    public static int randomIpCanType() {
        return ipCanTypes[random.nextInt(ipCanTypes.length)];
    }

    /**
     * Generate random bitrate value, 64kB or 128kB.
     * @return random bitrate value
     */
    public static int randomBitrate() {
        return bitrates[random.nextInt(bitrates.length)];
    }

    /**
     * Generate random user location textual description value.
     * @return 26-character long alphanum string
     */
    public static String randomUserLocation() {
        return randomAlphanumString(26);
    }

    /**
     * Generate random value for AF-Charging-Identifier AVP.
     * @return 50-character long random alphanum string
     */
    public static String randomAfChargingIdentifier() {
        return randomAlphanumString(50);
    }

    /**
     * Generate whole random structure of Media-Component-Description AVP.
     * @return structured random data for call updates
     * @throws UnknownHostException in case of error
     */
    public static MediaComponent randomMediaComponent() throws UnknownHostException {
        int bandwidth = randomBitrate();
        int componentNumber = random.nextInt(Integer.MAX_VALUE);
        int flowNumber = random.nextInt(Integer.MAX_VALUE);
        int lowPort = randomPort();
        int highPort = randomPort();

        String uplink = "uplink offer m=audio " + lowPort + " " + randomAlphanumString(100);
        String downlink = "downlink answer m=audio " + highPort + " " + randomAlphanumString(100);
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
