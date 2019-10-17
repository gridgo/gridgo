package io.gridgo.utils.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gridgo.utils.InetAddressUtils;
import lombok.NonNull;

public class EndpointParser {

    // match 1->5 digits (1 or 22 or 333 or 4444 or 55555)
    private static final Pattern PORT_PATTERN = Pattern.compile("\\d{1,5}");

    private static final Pattern ENDPOINT_PATTERN = Pattern.compile("(?i)^(.+):\\/\\/(.+)$");

    public static Endpoint parse(@NonNull String address) {
        String[] segments = extractToSegments(address.trim());
        if (segments.length == 0) {
            throw new IllegalArgumentException("Invalid address: " + address);
        }

        String protocol = segments[0].toLowerCase();
        String host = segments[1];

        if (!host.equals("*")) {
            String resolvedHost = InetAddressUtils.resolve(host);
            host = resolvedHost == null ? host : resolvedHost;
        }

        int port = segments[2] == null ? -1 : Integer.valueOf(segments[2]);
        String nic = segments[3];

        return Endpoint.builder() //
                       .nic(nic) //
                       .address(address) //
                       .protocol(protocol) //
                       .host(host) //
                       .port(port) //
                       .build();
    }

    private static String[] extractToSegments(String address) {
        Matcher matcher = ENDPOINT_PATTERN.matcher(address.trim());
        if (!matcher.find())
            return new String[0];
        String protocol = matcher.group(1);
        String hostAndPort = matcher.group(2).trim();
        String[] arrMayContainsInterface = hostAndPort.split(";");
        String nic = null;
        String[] arr;
        if (arrMayContainsInterface.length == 1) {
            arr = hostAndPort.split(":");
        } else {
            nic = arrMayContainsInterface[0];
            arr = arrMayContainsInterface[1].split(":");
        }

        if (arr.length > 1) {
            String maybePort = arr[arr.length - 1];
            if (PORT_PATTERN.matcher(maybePort).find()) {
                StringBuilder host = new StringBuilder();
                for (int i = 0; i < arr.length - 1; i++) {
                    if (host.length() > 0) {
                        host.append(":");
                    }
                    host.append(arr[i]);
                }
                return new String[] { protocol, host.toString(), maybePort, nic };
            }
        }
        return new String[] { protocol, hostAndPort, null, nic };
    }
}
