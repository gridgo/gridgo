package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.InetAddressUtils;

public class InetAddressUtilsUnitTest {

    @Test
    public void testResolveIP() {
        System.out.println(InetAddressUtils.resolveIPFromDomain("localhost", true));
        System.out.println(InetAddressUtils.resolveIPFromInterface("eth0", true));
        System.out.println(InetAddressUtils.resolveIPFromInterface("lo0", true));
        System.out.println(InetAddressUtils.resolveIPv4("localhost", true));
        System.out.println(InetAddressUtils.resolveIPv4("eth0", true));
        System.out.println(InetAddressUtils.resolveIPv6("localhost", true));
        System.out.println(InetAddressUtils.resolveIPv6("eth0", true));
        Assert.assertEquals(2130706433L, InetAddressUtils.ipv4ToLong("127.0.0.1"));
        Assert.assertEquals("127.0.0.1", InetAddressUtils.longToIpv4(2130706433L));
        Assert.assertEquals(1, InetAddressUtils.ipv6ToLong("::1").longValue());
    }
}
