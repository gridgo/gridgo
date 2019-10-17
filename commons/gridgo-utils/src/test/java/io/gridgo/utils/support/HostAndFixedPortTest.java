package io.gridgo.utils.support;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HostAndFixedPortTest {

    @Test(expected = UnsupportedOperationException.class)
    public void setPort_ShouldThrowException_WhenSetPort() {
        var hostAndPort = new HostAndFixedPort("localhost", 8080);
        assertEquals("localhost", hostAndPort.getHost());
        assertEquals(8080, hostAndPort.getPort());
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());
        assertEquals("localhost:8080", hostAndPort.toString());
        assertEquals("localhost:8080", hostAndPort.toHostAndPort());
        assertEquals("127.0.0.1:8080", hostAndPort.toIpAndPort());
        hostAndPort.setPort(9090);
    }

    @Test
    public void constructor() {
        var hostAndPort = new HostAndFixedPort(8080);
        assertEquals(8080, hostAndPort.getPort());
    }
}