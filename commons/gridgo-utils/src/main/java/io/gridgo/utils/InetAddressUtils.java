package io.gridgo.utils;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InetAddressUtils {

    public static final Pattern IPV4_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    public static final Pattern IPV6_HEX_PATTERN = Pattern.compile(
            "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    /**
     * Validate ipv4 address with regular expression
     * 
     * @param ip ipv4 address for validation
     * @return true if valid ipv4 address
     */
    public static boolean isIPv4(String ip) {
        return ip == null ? false : IPV4_PATTERN.matcher(ip.trim()).matches();
    }

    public static boolean isIPv6STD(String ip) {
        return ip == null ? false : IPV6_STD_PATTERN.matcher(ip.trim()).matches();
    }

    public static boolean isIPv6HEX(String ip) {
        return ip == null ? false : IPV6_HEX_PATTERN.matcher(ip.trim()).matches();
    }

    public static boolean isIPv6(String ip) {
        return ip == null ? false : (isIPv6STD(ip) || isIPv6HEX(ip));
    }

    public static String resolveIPv4FromDomain(String domain) {
        if (domain == null)
            return null;
        if (isIPv4(domain))
            return domain;
        try {
            String address = Inet4Address.getByName(domain).getHostAddress();
            return isIPv4(address) ? address : null;
        } catch (UnknownHostException e) {
            log.debug("cannot resolve IPv4 address, return null", e);
        }
        return null;
    }

    public static String resolveIPv6FromDomain(String domain) {
        if (domain == null)
            return null;
        if (isIPv6(domain)) {
            return domain;
        }
        try {
            String address = Inet6Address.getByName(domain).getHostAddress();
            return isIPv6(address) ? address : null;
        } catch (UnknownHostException e) {
            log.debug("cannot resolve IPv6 address, return null", e);
        }
        return null;
    }

    public static String resolveIPFromDomain(String domain, boolean preferIPv6) {
        if (domain == null)
            return null;
        String ip = preferIPv6 ? resolveIPv6FromDomain(domain) : resolveIPv4FromDomain(domain);
        if (ip == null) {
            ip = preferIPv6 ? resolveIPv4FromDomain(domain) : resolveIPv6FromDomain(domain);
        }
        return ip;
    }

    public static String resolveIPv4FromInterface(String nifName) {
        return resolveIPFromInterface(nifName, addr -> addr instanceof Inet4Address);
    }

    public static String resolveIPv6FromInterface(String nifName) {
        return resolveIPFromInterface(nifName, addr -> addr instanceof Inet6Address);
    }

    private static String resolveIPFromInterface(String nifName, Predicate<InetAddress> predicate) {
        if (nifName == null)
            return null;
        try {
            var nif = NetworkInterface.getByName(nifName);
            if (nif != null) {
                var addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (predicate.test(addr))
                        return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            log.debug("cannot resolve address from interface, return null", e);
        }
        return null;
    }

    public static String resolveIPFromInterface(String nifName, boolean preferIPv6) {
        if (nifName == null)
            return null;
        String ip = preferIPv6 ? resolveIPv6FromInterface(nifName) : resolveIPv4FromInterface(nifName);
        if (ip == null) {
            ip = preferIPv6 ? resolveIPv4FromInterface(nifName) : resolveIPv6FromInterface(nifName);
        }
        return ip;
    }

    public static String resolveIPFromInterface(String nifName) {
        return resolveIPFromInterface(nifName, false);
    }

    public static String resolveIPv4(String input, boolean preferInterface) {
        if (input == null)
            return null;
        String ip = preferInterface ? resolveIPv4FromInterface(input) : resolveIPv4FromDomain(input);
        if (ip == null) {
            ip = preferInterface ? resolveIPv4FromDomain(input) : resolveIPv4FromInterface(input);
        }
        return ip;
    }

    public static String resolveIPv6(String input, boolean preferInterface) {
        if (input == null)
            return null;
        String ip = preferInterface ? resolveIPv6FromInterface(input) : resolveIPv6FromDomain(input);
        if (ip == null) {
            ip = preferInterface ? resolveIPv6FromDomain(input) : resolveIPv6FromInterface(input);
        }
        return ip;
    }

    public static String resolve(String input, boolean preferIPv6, boolean preferInterface) {
        if (input == null)
            return null;
        String ip = preferInterface ? resolveIPFromInterface(input, preferIPv6) : resolveIPFromDomain(input, preferIPv6);
        if (ip == null) {
            ip = preferInterface ? resolveIPFromDomain(input, preferIPv6) : resolveIPFromInterface(input, preferIPv6);
        }
        return ip;
    }

    public static String resolve(String input) {
        return resolve(input, false, false);
    }

    public static long ipv4ToLong(String ipv4) {
        if (!isIPv4(ipv4)) {
            throw new AssertionError("Invalid ip address");
        }

        long result = 0;
        String[] ipAddressInArray = ipv4.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return result;
    }

    public static String longToIpv4(long ip) {
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    public static BigInteger ipv6ToLong(String ipv6) {
        try {
            return new BigInteger(InetAddress.getByName(ipv6).getAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid ipv6", e);
        }
    }
}
