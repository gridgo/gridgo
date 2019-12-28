package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.factory.BFactory;

public class TestMockSerializer {

    @BeforeClass
    public static void initClass() {
        BFactory.DEFAULT.getSerializerRegistry().scan("io.gridgo.bean.test.support.supported");
    }

    @Test
    public void testMockJson() throws IOException {
        var element = BElement.ofJson("some_random_string");
        Assert.assertTrue(element != null && element.isValue());
        Assert.assertEquals("test", element.asValue().getString());
        String json = element.toJson();
        Assert.assertEquals("test", json);
        try (var baos = new ByteArrayOutputStream()) {
            element.writeJson(baos);
            Assert.assertArrayEquals("test".getBytes(), baos.toByteArray());
        }
    }

    @Test
    public void testMockRaw() throws IOException {
        var element = BElement.ofBytes("some_random_string".getBytes());
        Assert.assertTrue(element != null && element.isValue());
        Assert.assertEquals("some_random_string", element.asValue().getString());
        var bytes = element.toBytes();
        Assert.assertEquals("some_random_string", new String(bytes));
        try (var baos = new ByteArrayOutputStream()) {
            element.writeBytes(baos);
            Assert.assertArrayEquals("some_random_string".getBytes(), baos.toByteArray());
        }
    }
}
