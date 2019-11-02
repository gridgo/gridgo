package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;

public class TextStringSerializer {

    @Test
    public void testStringSerializer() {
        var str = BValue.of("hello").toBytes("string");
        Assert.assertEquals("hello", new String(str));
        str = BValue.of(123).toBytes("string");
        Assert.assertArrayEquals(new byte[] {0, 0, 0, 123}, str);
    }

    @Test
    public void testStringDeserializer() {
        var str = BValue.of("hello").toBytes("string");
        var e = BElement.ofBytes(str, "string");
        Assert.assertTrue(e.isValue());
        Assert.assertEquals("hello", e.asValue().getString());
    }
}
