package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import io.gridgo.bean.exceptions.SerializationPluginException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.BSerializerRegistry;

public class TestBSerializerRegistry {

    @Test
    public void testAvailable() {
        var registry = new BSerializerRegistry(BFactory.DEFAULT);
        var availableSerializers = registry.availableSerializers();
        Assert.assertEquals(Set.of("print", "string"), availableSerializers);
    }

    @Test(expected = SerializationPluginException.class)
    public void testSerializerScanException() {
        var registry = new BSerializerRegistry(BFactory.DEFAULT);
        registry.scan("io.gridgo.bean.test.support.unsupported");
    }
}
