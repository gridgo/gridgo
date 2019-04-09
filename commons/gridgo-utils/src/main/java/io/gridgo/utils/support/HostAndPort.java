package io.gridgo.utils.support;

import static io.gridgo.utils.hash.BinaryHashCodeCalculator.XXHASH32_JAVA_SAFE;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.gridgo.utils.InetAddressUtils;
import io.gridgo.utils.exception.MalformedHostAndPortException;
import lombok.Getter;
import lombok.NonNull;

public class HostAndPort {

    private static final String ALL_INTERFACE_IP = "0.0.0.0";

    @Getter
    private String host;

    @Getter
    private int port;

    private transient volatile boolean isHostResolved = false;
    private transient volatile String resolvedHost;

    private transient volatile int cachedHashCode = -1;
    private transient volatile boolean isHashCodeCalculated = false;

    public HostAndPort(String host, int port) {
        this.port = port;
        this.setHost(host);
    }

    public HostAndPort(String host) {
        this.setHost(host);
    }

    public HostAndPort(int port) {
        this.port = port;
    }

    public HostAndPort() {
        // do nothing
    }

    public int getPortOrDefault(int defaultPort) {
        if (this.getPort() <= 0) {
            return defaultPort;
        }
        return this.getPort();
    }

    public String getHostOrDefault(String defaultHost) {
        if (this.getHost() == null) {
            return defaultHost;
        }
        return this.getHost();
    }

    public String getResolvedIpOrDefault(String defaultIP) {
        String resolvedIP = this.getResolvedIp();
        return resolvedIP == null ? defaultIP : resolvedIP;
    }

    public boolean isResolvable() {
        try {
            this.getResolvedIp();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getResolvedIp() {
        if (!isHostResolved) {
            synchronized (this) {
                if (!isHostResolved) {
                    if (this.getHost().equalsIgnoreCase("*")) {
                        this.resolvedHost = ALL_INTERFACE_IP;
                    } else {
                        this.resolvedHost = InetAddressUtils.resolve(this.getHost());
                    }
                    this.isHostResolved = true;
                }
            }
        }
        return this.resolvedHost;
    }

    public void setHost(@NonNull String host) {
        if (!host.equals(this.host)) {
            this.host = host.toLowerCase().trim();
            this.isHostResolved = false;
            this.isHashCodeCalculated = false;
        }
    }

    public void setPort(int port) {
        if (this.port != port) {
            this.port = port;
            this.isHashCodeCalculated = false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HostAndPortSet) {
            if (((HostAndPortSet) obj).size() == 1) {
                return this.equals(((HostAndPortSet) obj).getFirst());
            }
            return false;
        }
        HostAndPort other = null;
        if (obj instanceof HostAndPort) {
            other = (HostAndPort) obj;
        } else if (obj instanceof String) {
            other = fromString((String) obj);
        }

        if (other == null || port != other.port || other.host == null || this.host == null)
            return false;
        if (this.host.equalsIgnoreCase(other.host))
            return true;
        try {
            return this.getResolvedIp().equalsIgnoreCase(other.getResolvedIp());
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.isHashCodeCalculated)
            return this.cachedHashCode;
        String representer;
        try {
            representer = this.toIpAndPort();
        } catch (Exception ex) {
            representer = this.toHostAndPort();
        }

        this.cachedHashCode = XXHASH32_JAVA_SAFE.calcHashCode(representer.getBytes());
        this.isHashCodeCalculated = true;
        return this.cachedHashCode;
    }

    @Override
    public String toString() {
        return toHostAndPort();
    }

    public String toHostAndPort() {
        return new StringBuilder().append(this.getHost()).append(":").append(this.getPort()).toString();
    }

    public String toIpAndPort() {
        return new StringBuilder().append(this.getResolvedIp()).append(":").append(this.getPort()).toString();
    }

    public static HostAndPort newInstance(int port) {
        return new HostAndPort(port);
    }

    public static HostAndPort newInstance(String host, int port) {
        return new HostAndPort(host, port);
    }

    public static HostAndPort newInstance(String host) {
        return new HostAndPort(host);
    }

    public static HostAndPort newInstance() {
        return new HostAndPort();
    }

    public HostAndPort makeCopy() {
        return new HostAndPort(this.host, this.port);
    }

    public static HostAndPort fromString(String hostAndPort) {
        if (hostAndPort == null)
            return null;
        HostAndPort result = newInstance();
        String[] arr = hostAndPort.trim().toLowerCase().split(":");
        if (arr.length == 1) {
            result.setHost(arr[0]);
        } else if (arr.length > 1) {
            result.setPort(Integer.parseInt(arr[arr.length - 1]));
            result.setHost(StringUtils.join(arr, ":", 0, arr.length - 1));
        }
        return result;
    }

    public static List<HostAndPort> parse(String value) {
        if (value == null)
            return Collections.emptyList();
        value = value.trim();
        if (value.startsWith("[")) {
            if (value.endsWith("]")) {
                value = value.substring(1, value.length() - 1);
            } else {
                throw new MalformedHostAndPortException("Multi host and port string if start with [ must end with ]");
            }
        }
        String[] arr = value.trim().split(",");
        List<HostAndPort> results = new LinkedList<>();
        for (String str : arr) {
            results.add(fromString(str));
        }
        return results;
    }
}
