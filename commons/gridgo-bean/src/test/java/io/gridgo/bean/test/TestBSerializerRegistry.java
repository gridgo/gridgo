package io.gridgo.bean.test;

import org.junit.Test;

import io.gridgo.bean.exceptions.SerializationPluginException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.serialization.BSerializerRegistry;

public class TestBSerializerRegistry {

    @Test(expected = SerializationPluginException.class)
    public void testSerializerScanException() {
        var registry = new BSerializerRegistry(BFactory.DEFAULT);
        registry.scan("io.gridgo.bean.test.support.unsupported");
    }
}
