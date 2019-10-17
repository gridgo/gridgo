package io.gridgo.utils.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EndpointTest {

    Endpoint endpoint;
    String address = "tcp://eth0;127.0.0.1:8080";
    @Before
    public void setUp() {
        this.endpoint = Endpoint.builder()
                .address(address)
                .protocol("tcp")
                .host("127.0.0.1")
                .nic("eth0")
                .port(8080)
                .build();
    }

    @Test
    public void getResolvedAddress() {
        var ans = endpoint.getResolvedAddress();
        Assert.assertEquals(address, ans);
    }

    @Test
    public void getAddress() {
        Assert.assertEquals(address, endpoint.getAddress());
        Assert.assertEquals(address, endpoint.toString());

    }

    @Test
    public void testGetters() {
        Assert.assertEquals("tcp", endpoint.getProtocol());
        Assert.assertEquals("127.0.0.1", endpoint.getHost());
        Assert.assertEquals("eth0", endpoint.getNic());
        Assert.assertEquals(8080, endpoint.getPort());
    }
}