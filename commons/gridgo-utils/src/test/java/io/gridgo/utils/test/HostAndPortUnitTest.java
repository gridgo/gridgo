package io.gridgo.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import io.gridgo.utils.support.HostAndFixedPort;
import io.gridgo.utils.support.HostAndPort;
import io.gridgo.utils.support.HostAndPortSet;

public class HostAndPortUnitTest {

    @Test
    public void testHostAndPort() {
        HostAndPort hostAndPort = HostAndPort.newInstance("localhost", 8080);
        assertNotNull(hostAndPort);
        assertEquals("localhost", hostAndPort.getHost());
        assertEquals(8080, hostAndPort.getPort());
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());
        assertEquals("localhost:8080", hostAndPort.toString());
        assertEquals("localhost:8080", hostAndPort.toHostAndPort());
        assertEquals("127.0.0.1:8080", hostAndPort.toIpAndPort());

        hostAndPort = HostAndPort.fromString("localhost:8080");
        assertNotNull(hostAndPort);
        assertEquals("localhost", hostAndPort.getHost());
        assertEquals(8080, hostAndPort.getPort());
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());
        assertEquals("localhost:8080", hostAndPort.toString());
        assertEquals("localhost:8080", hostAndPort.toHostAndPort());
        assertEquals("127.0.0.1:8080", hostAndPort.toIpAndPort());

        hostAndPort = new HostAndFixedPort("localhost", 8080);
        assertEquals("localhost", hostAndPort.getHost());
        assertEquals(8080, hostAndPort.getPort());
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());
        assertEquals("localhost:8080", hostAndPort.toString());
        assertEquals("localhost:8080", hostAndPort.toHostAndPort());
        assertEquals("127.0.0.1:8080", hostAndPort.toIpAndPort());

        Exception ex = null;
        try {
            hostAndPort.setPort(9090);
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex instanceof UnsupportedOperationException);

        HostAndPort otherHostAndPort = HostAndPort.fromString("127.0.0.1:8080");
        assertEquals(hostAndPort, otherHostAndPort);

        otherHostAndPort = HostAndPort.fromString("127.0.0.1:9999");
        assertNotEquals(hostAndPort, otherHostAndPort);

        otherHostAndPort = HostAndPort.fromString("this-is-invalid-host:9999");
        assertNotEquals(hostAndPort, otherHostAndPort);

        List<HostAndPort> list = HostAndPort.parse("localhost:8080, 127.0.0.1:8080,localhost,* :9999,0.0.0.0:9999");
        assertNotNull(list);
        assertEquals(5, list.size());
        assertEquals(list.get(0), list.get(1));
        assertEquals(list.get(3), list.get(4));
        assertNotEquals(list.get(0), list.get(2));

        list = HostAndPort.parse("[localhost:8080, 127.0.0.1:8080,localhost,* :9999,0.0.0.0:9999]");
        assertNotNull(list);
        assertEquals(5, list.size());
        assertEquals(list.get(0), list.get(1));
        assertEquals(list.get(3), list.get(4));
        assertNotEquals(list.get(0), list.get(2));
        System.out.println("list: " + list);
    }

    @Test
    public void testHostAndPortSet() {
        HostAndPortSet set = new HostAndPortSet("localhost:8080, 127.0.0.1:8080,localhost,* :9999,0.0.0.0:9999");
        assertNotNull(set);
        assertEquals(3, set.size());

        set = new HostAndPortSet("localhost:8080");
        assertNotNull(set);
        assertEquals(1, set.size());

        HostAndPort hostAndPort = HostAndPort.newInstance("localhost", 8080);
        assertEquals(hostAndPort, set);
    }
}
