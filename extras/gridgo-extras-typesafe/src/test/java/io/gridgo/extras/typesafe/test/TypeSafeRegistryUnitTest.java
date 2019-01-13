package io.gridgo.extras.typesafe.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.extras.typesafe.TypeSafeRegistry;

public class TypeSafeRegistryUnitTest {

    @Test
    public void testResource() {
        var typeSafe = new TypeSafeRegistry("test.conf");
        Assert.assertEquals("test", typeSafe.lookup("applicationName"));
        Assert.assertNotNull(typeSafe.lookup("gateways.test"));
        Assert.assertFalse(typeSafe.lookup("gateways.test.subscribers", List.class).isEmpty());
    }

    @Test
    public void testFile() throws URISyntaxException, FileNotFoundException {
        var resource = getClass().getClassLoader().getResource("test.conf").toURI();
        var typeSafe = new TypeSafeRegistry(new File(resource));
        Assert.assertEquals("test", typeSafe.lookup("applicationName"));
        Assert.assertNotNull(typeSafe.lookup("gateways.test"));
        Assert.assertFalse(typeSafe.lookup("gateways.test.subscribers", List.class).isEmpty());
    }
}
