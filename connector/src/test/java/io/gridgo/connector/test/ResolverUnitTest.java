package io.gridgo.connector.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.connector.support.exceptions.UnsupportedSchemeException;
import io.gridgo.connector.test.support.TestConnector;
import io.gridgo.connector.test.support.TestUriResolver;
import io.gridgo.dummy.test.DummyConnector;

public class ResolverUnitTest {

    @Test
    public void testFactory() {
        try {
            new DefaultConnectorFactory().createConnector("dummy:pull:tcp://127.0.0.1:8080?p1=v1&p2=v2");
            Assert.fail("Must throw exception");
        } catch (UnsupportedSchemeException ex) {

        }
        var connector = new DefaultConnectorFactory().createConnector("dummy:pull:tcp://127.0.0.1:8080?p1=v1&p2=v2",
                new ClasspathConnectorResolver("io.gridgo.dummy"));
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getConnectorConfig());
        Assert.assertNotNull(connector.getConnectorConfig().getRemaining());
        Assert.assertNotNull(connector.getConnectorConfig().getParameters());
        Assert.assertTrue(connector instanceof DummyConnector);
        Assert.assertEquals("pull:tcp://127.0.0.1:8080", connector.getConnectorConfig().getRemaining());
        Assert.assertEquals("v1", connector.getConnectorConfig().getParameters().get("p1"));
        Assert.assertEquals("v2", connector.getConnectorConfig().getParameters().get("p2"));
        Assert.assertEquals("pull", connector.getConnectorConfig().getPlaceholders().get("type"));
        Assert.assertEquals("tcp", connector.getConnectorConfig().getPlaceholders().get("transport"));
        Assert.assertEquals("127.0.0.1", connector.getConnectorConfig().getPlaceholders().get("host"));
        Assert.assertEquals("8080", connector.getConnectorConfig().getPlaceholders().get("port"));
    }

    @Test
    public void testIPv6() {
        var resolver = new ClasspathConnectorResolver();
        var connector = resolver.resolve("test:pull:tcp://[2001:db8:1f70::999:de8:7648:6e8]:7778?p1=v1&p2=v2");
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getConnectorConfig());
        Assert.assertNotNull(connector.getConnectorConfig().getRemaining());
        Assert.assertNotNull(connector.getConnectorConfig().getParameters());
        Assert.assertTrue(connector instanceof TestConnector);
        Assert.assertEquals("pull:tcp://[2001:db8:1f70::999:de8:7648:6e8]:7778", connector.getConnectorConfig().getRemaining());
        Assert.assertEquals("v1", connector.getConnectorConfig().getParameters().get("p1"));
        Assert.assertEquals("v2", connector.getConnectorConfig().getParameters().get("p2"));
        Assert.assertEquals("pull", connector.getConnectorConfig().getPlaceholders().get("type"));
        Assert.assertEquals("tcp", connector.getConnectorConfig().getPlaceholders().get("transport"));
        Assert.assertEquals("2001:db8:1f70::999:de8:7648:6e8", connector.getConnectorConfig().getPlaceholders().get("host"));
        Assert.assertEquals("7778", connector.getConnectorConfig().getPlaceholders().get("port"));
    }

    @Test
    public void testResolver() {
        var resolver = new TestUriResolver(TestConnector.class);
        var props = resolver.testResolver("test://127.0.0.1:80/api", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertEquals("80", props.get("port"));
        Assert.assertEquals("api", props.get("path"));

        props = resolver.testResolver("test://127.0.0.1:80", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertEquals("80", props.get("port"));
        Assert.assertNull(props.get("path"));

        props = resolver.testResolver("test://127.0.0.1", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertNull(props.get("port"));
        Assert.assertNull(props.get("path"));

        props = resolver.testResolver("test://127.0.0.1/api", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertNull(props.get("port"));
        Assert.assertEquals("api", props.get("path"));

        props = resolver.testResolver("test://127.0.0.1:/api", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertNull(props.get("port"));
        Assert.assertEquals("api", props.get("path"));

        props = resolver.testResolver("test://127.0.0.1:/", "test://{host}[:{port}][/{path}]");
        Assert.assertEquals("127.0.0.1", props.get("host"));
        Assert.assertNull(props.get("port"));
        Assert.assertNull(props.get("path"));

        props = resolver.testResolver("test://[127.0.0.1:5672,23.45.56.67:4567]/path", "test://{address}[/{path}]");
        assertEquals("127.0.0.1:5672,23.45.56.67:4567", props.get("address"));
        assertEquals("path", props.get("path"));

        props = resolver.testResolver("pull:tcp://eth0;127.0.0.1:5555", "{type}:{transport}:[{role}:]//[{nic};]{host}[:{port}]");
        assertEquals(null, props.get("role"));
        assertEquals("eth0", props.get("nic"));
        assertEquals("127.0.0.1", props.get("host"));
        assertEquals("5555", props.get("port"));

        props = resolver.testResolver("pull:tcp://127.0.0.1:5555", "{type}:{transport}:[{role}:]//[{nic};]{host}[:{port}]");
        assertEquals(null, props.get("role"));
        assertEquals(null, props.get("nic"));
        assertEquals("127.0.0.1", props.get("host"));
        assertEquals("5555", props.get("port"));

        props = resolver.testResolver("pull:tcp:connect://127.0.0.1:5555", "{type}:{transport}:[{role}:]//[{nic};]{host}[:{port}]");
        assertEquals("connect", props.get("role"));
        assertEquals(null, props.get("nic"));
        assertEquals("127.0.0.1", props.get("host"));
        assertEquals("5555", props.get("port"));

        props = resolver.testResolver("http://127.0.0.1:8080/[api/v1/products]", "http://{host}:{port}/{path}");
        assertEquals("127.0.0.1", props.get("host"));
        assertEquals("8080", props.get("port"));
        assertEquals("api/v1/products", props.get("path"));

        props = resolver.testResolver("http://127.0.0.1:8080/*", "http://{host}:{port}/{path}");
        assertEquals("127.0.0.1", props.get("host"));
        assertEquals("8080", props.get("port"));
        assertEquals("*", props.get("path"));

        props = resolver.testResolver("push:ipc://client_to_game", "{type}:{transport}:[{role}:]//[{interface};]{host}[:{port}]");
        assertEquals("push", props.get("type"));
        assertEquals("ipc", props.get("transport"));
        assertEquals("client_to_game", props.get("host"));
        assertNull(props.get("port"));
        assertNull(props.get("interface"));
    }

    @Test
    public void testSimple() {
        var resolver = new ClasspathConnectorResolver();
        var connector = resolver.resolve("test:pull:tcp://127.0.0.1:7777?p1=v1&p2=v2");
        Assert.assertNotNull(connector);
        Assert.assertNotNull(connector.getConnectorConfig());
        Assert.assertNotNull(connector.getConnectorConfig().getRemaining());
        Assert.assertNotNull(connector.getConnectorConfig().getParameters());
        Assert.assertTrue(connector instanceof TestConnector);
        Assert.assertEquals("pull:tcp://127.0.0.1:7777", connector.getConnectorConfig().getRemaining());
        Assert.assertEquals("v1", connector.getConnectorConfig().getParameters().get("p1"));
        Assert.assertEquals("v2", connector.getConnectorConfig().getParameters().get("p2"));
        Assert.assertEquals("pull", connector.getConnectorConfig().getPlaceholders().get("type"));
        Assert.assertEquals("tcp", connector.getConnectorConfig().getPlaceholders().get("transport"));
        Assert.assertEquals("127.0.0.1", connector.getConnectorConfig().getPlaceholders().get("host"));
        Assert.assertEquals("7777", connector.getConnectorConfig().getPlaceholders().get("port"));
    }
}
