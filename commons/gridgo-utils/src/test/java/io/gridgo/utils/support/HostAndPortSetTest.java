package io.gridgo.utils.support;

import io.gridgo.utils.exception.MalformedHostAndPortException;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("unlikely-arg-type")
public class HostAndPortSetTest {

    @Test(expected = MalformedHostAndPortException.class)
    public void constructor_ShouldThrowExceptionWhenInvalidInput() {
        String hostPorts = "[localhost:8080, localhost:8081";
        new HostAndPortSet(hostPorts);
    }

    @Test
    public void constructor_ShouldSuccess() {
        String hostPorts = "localhost:8080,localhost:8081";
        HostAndPortSet hostAndPorts = new HostAndPortSet(HostAndPort.fromString("localhost:8080"),
                HostAndPort.fromString("localhost:8081"));
        assertEquals(hostPorts, hostAndPorts.toString());

        assertTrue(new HostAndPortSet().toString().isEmpty());
    }

    @Test
    public void convert_ShouldSuccess() {
        HostAndPortSet hostAndPorts = new HostAndPortSet(HostAndPort.fromString("localhost:8080"),
                HostAndPort.fromString("localhost:8081"));
        var ans = hostAndPorts.convert(x -> x.getHost());
        assertEquals("[localhost, localhost]", ans.toString());
    }

    @Test
    public void test_Equals_WhenTheSameInput() {
        String hostPorts = "[localhost:8080, localhost:8081]";
        var hostSet1 = new HostAndPortSet(hostPorts);
        assertFalse(hostSet1.equals(null));
        assertTrue(hostSet1.equals(new HostAndPortSet(hostPorts)));
        assertTrue(hostSet1.equals(new HostAndPortSet("[localhost:8081, localhost:8080]")));
    }

    @Test
    public void test_Equal_WhenInputString() {
        var hostSet1 = new HostAndPortSet("[localhost:8080, localhost:8081]");
        assertFalse(hostSet1.equals("localhost:8080"));
    }

    @Test
    public void test_Equal_WhenInputHostAndPort() {
        var hostSet1 = new HostAndPortSet("[localhost:8080]");
        assertTrue(hostSet1.equals(HostAndPort.fromString("localhost:8080")));
    }

    @Test
    public void test_Equal_WhenInputNotTheSame() {
        var hostSet1 = new HostAndPortSet("[localhost:8080, localhost:8081]");
        assertFalse(hostSet1.equals("[localhost:8080, localhost:8282]"));
        assertFalse(hostSet1.equals(Long.valueOf(1)));

    }

    @Test
    public void test_Equal_ShouldFalse_WhenInputHostAndPort() {
        var hostSet1 = new HostAndPortSet("[localhost:8080, localhost:8181]");
        assertFalse(hostSet1.equals(HostAndPort.fromString("localhost:8080")));
    }

}