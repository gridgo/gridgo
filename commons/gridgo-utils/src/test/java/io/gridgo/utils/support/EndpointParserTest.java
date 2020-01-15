package io.gridgo.utils.support;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointParserTest {

    @Test
    public void parse_ShouldSuccess_WhenHaveNic() {
        String endpoint = "tcp://eth0;localhost:8080";
        var ans = EndpointParser.parse(endpoint);
        assertEquals(endpoint, ans.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_ShouldThrowExcepton_WhenInputTrimIsEmpty() {
        EndpointParser.parse("  ");
    }

    @Test
    public void parse_ShouldSuccess_WhenNoNic() {
        String endpoint = "tcp://localhost:8080";
        var ans = EndpointParser.parse(endpoint);
        assertEquals(endpoint, ans.toString());
    }

    @Test
    public void parse_ShouldSuccess_WhenLongHost() {
        String endpoint = "tcp://localhost:tiki:8880";
        var ans = EndpointParser.parse(endpoint);
        assertEquals(endpoint, ans.toString());
    }

    @Test
    public void parse_ShouldSuccess_WhenNoPort() {
        String endpoint = "tcp://localhost";
        var ans = EndpointParser.parse(endpoint);
        assertEquals(endpoint, ans.toString());
    }

    @Test
    public void testFineCase() {
        String host = "localhost";
        int port = 1000;
        String scheme = "tcp";
        String address = scheme + "://" + host + ":" + port;

        Endpoint endpoint = EndpointParser.parse(address);

        assertEquals(scheme, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(port, endpoint.getPort());
    }

    @Test
    public void testMissingPort() {
        String host = "localhost";
        String scheme = "tcp";
        String address = scheme + "://" + host;

        Endpoint endpoint = EndpointParser.parse(address);

        assertEquals(scheme, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(-1, endpoint.getPort());
    }

    @Test
    public void testMissingHost() {
        String host = "";
        String scheme = "tcp";
        int port = 1000;
        String address = scheme + "://" + host + ":" + port;

        Endpoint endpoint = EndpointParser.parse(address);

        assertEquals(scheme, endpoint.getProtocol());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(port, endpoint.getPort());
    }
}