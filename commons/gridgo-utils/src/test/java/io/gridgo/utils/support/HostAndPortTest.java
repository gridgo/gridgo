package io.gridgo.utils.support;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class HostAndPortTest {

    @Test
    public void parse() {
        assertTrue(HostAndPort.parse(null).isEmpty());
    }

    @Test
    public void makeCopy() {
        var first = HostAndPort.newInstance("localhost", 8080);
        var copy = first.makeCopy();
        assertForBase(copy);
    }

    @Test
    public void newInstance() {
        HostAndPort hostAndPort = HostAndPort.newInstance("localhost", 8080);
        assertForBase(hostAndPort);

        hostAndPort = HostAndPort.newInstance("localhost");
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());

        hostAndPort = HostAndPort.newInstance(8080);
        assertEquals("null:8080", hostAndPort.toString());
    }

    @Test
    public void fromString() {
        var hostAndPort = HostAndPort.fromString("localhost:8080");
        assertForBase(hostAndPort);

        assertNull(HostAndPort.fromString(null));
    }

    private void assertForBase(HostAndPort hostAndPort) {
        assertNotNull(hostAndPort);
        assertEquals("localhost", hostAndPort.getHost());
        assertEquals(8080, hostAndPort.getPort());
        assertEquals("127.0.0.1", hostAndPort.getResolvedIp());
        assertEquals("localhost:8080", hostAndPort.toString());
        assertEquals("localhost:8080", hostAndPort.toHostAndPort());
        assertEquals("127.0.0.1:8080", hostAndPort.toIpAndPort());
    }

    @Test
    public void testHostAndPort() {

        var hostAndPort = HostAndPort.fromString("localhost:8080");


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

    @Test
    public void test_hashCode() {
        HostAndPort x = spy(HostAndPort.newInstance("localhost", 8080));
        Mockito.when(x.toIpAndPort()).thenThrow(new NullPointerException());
        x.hashCode();
    }

    @Test
    public void getPortOrDefault() {
        var in = HostAndPort.newInstance("localhost");
        var ans = in.getPortOrDefault(8080);
        assertEquals(8080, ans);
        assertEquals(8080, HostAndPort.newInstance(8080).getPortOrDefault(9999));

    }

    @Test
    public void getHostOrDefault() {
        var in = HostAndPort.newInstance(8080);
        var ans = in.getHostOrDefault("localhost");
        assertEquals("localhost", ans);
        assertEquals("localhost", HostAndPort.newInstance("localhost").getHostOrDefault("tiki.vn"));

    }

    @Test
    public void getResolvedIpOrDefault() {
        var in = HostAndPort.newInstance("localhost");
        var ans = in.getResolvedIpOrDefault("127.0.0.1");
        assertEquals("127.0.0.1", ans);
//        assertEquals("127.0.0.1", HostAndPort.newInstance("abc.tiki.vn").getResolvedIpOrDefault("tiki.vn"));

    }

    @Test
    public void isResolvable_ShouldSuccess() {
        var in = HostAndPort.newInstance("*");
        assertTrue(in.isResolvable());

    }

    @Test
    public void isResolvable_ShouldFalse() {
        var in = spy(HostAndPort.newInstance("nothing.exist.not-the-name"));
        when(in.getResolvedIp()).thenThrow(new NullPointerException());

        assertFalse(in.isResolvable());
    }
}
