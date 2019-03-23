package io.gridgo.extras.typesafe.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.extras.typesafe.TypeSafeConfigurator;

public class TypeSafeUnitTest {

    @Test
    public void testResource() {
        var typeSafe = TypeSafeConfigurator.ofResource("test.conf");
        typeSafe.start();
        var config = typeSafe.get().orElseThrow().asObject();
        Assert.assertEquals("test", config.getString("applicationName"));
        Assert.assertNotNull(config.getObject("gateways"));
        Assert.assertNotNull(config.getObject("gateways").getObject("test"));
        Assert.assertFalse(config.getObject("gateways").getObject("test").getArray("subscribers").isEmpty());
    }

    @Test
    public void testFile() throws URISyntaxException, FileNotFoundException {
        var resource = getClass().getClassLoader().getResource("test.conf").toURI();
        var typeSafe = TypeSafeConfigurator.ofFile(new File(resource));
        typeSafe.start();
        var config = typeSafe.get().orElseThrow().asObject();
        Assert.assertEquals("test", config.getString("applicationName"));
        Assert.assertNotNull(config.getObject("gateways"));
        Assert.assertNotNull(config.getObject("gateways").getObject("test"));
        Assert.assertFalse(config.getObject("gateways").getObject("test").getArray("subscribers").isEmpty());
    }
}
