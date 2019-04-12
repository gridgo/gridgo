package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;

public class BValueUnitTest {

    @Test
    public void testNull() {
        Assert.assertNull(BValue.of(null).toJsonElement());
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(1, (int) BValue.of(1).toJsonElement());
    }

    @Test
    public void testEncodeDecode() {
        var val = BValue.of(new byte[] { 1, 2, 4, 8, 16, 32, 64 });
        val.encodeHex();
        Assert.assertEquals("0x01020408102040", val.getData());
        val.decodeHex();
        Assert.assertArrayEquals(new byte[] { 1, 2, 4, 8, 16, 32, 64 }, (byte[]) val.getData());
        val.encodeBase64();
        Assert.assertEquals("AQIECBAgQA==", val.getData());
        val.decodeBase64();
        Assert.assertArrayEquals(new byte[] { 1, 2, 4, 8, 16, 32, 64 }, (byte[]) val.getData());

        val = BElement.ofJson(val.toJson()).asValue();
        val.decodeHex();
        Assert.assertArrayEquals(new byte[] { 1, 2, 4, 8, 16, 32, 64 }, (byte[]) val.getData());

        val = BElement.ofXml(val.toXml()).asValue();
        Assert.assertArrayEquals(new byte[] { 1, 2, 4, 8, 16, 32, 64 }, (byte[]) val.getData());
    }

    @Test
    public void testSerialization() {
        var val = BValue.of(0);
        var after = BElement.ofBytes(new String(val.toBytes()).getBytes());
        System.out.println(after);
    }
}
