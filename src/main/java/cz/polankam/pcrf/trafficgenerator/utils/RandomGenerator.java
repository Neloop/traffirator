package cz.polankam.pcrf.trafficgenerator.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class RandomGenerator {

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


    public static int randomFramedIp() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static InetAddress randomAnGwAddress() throws UnknownHostException {
        return InetAddress.getByName("10.1.80.140");
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
}
