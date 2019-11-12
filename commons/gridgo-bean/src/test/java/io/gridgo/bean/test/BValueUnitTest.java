package io.gridgo.bean.test;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialization.json.writer.CompositeJsonWriter;

public class BValueUnitTest {

    private static final CompositeJsonWriter JSON_WRITER = CompositeJsonWriter.getNoCompactInstance();

    @Test
    public void testNull() {
        Assert.assertNull(JSON_WRITER.toJsonElement(BValue.of(null)));
        Assert.assertNull(JSON_WRITER.toJsonElement(BValue.ofEmpty()));
        Assert.assertNull(BValue.ofEmpty().getData());
        Assert.assertNull(BValue.ofEmpty().getBoolean());
        Assert.assertNull(BValue.ofEmpty().getChar());
        Assert.assertNull(BValue.ofEmpty().getByte());
        Assert.assertNull(BValue.ofEmpty().getShort());
        Assert.assertNull(BValue.ofEmpty().getInteger());
        Assert.assertNull(BValue.ofEmpty().getLong());
        Assert.assertNull(BValue.ofEmpty().getDouble());
        Assert.assertNull(BValue.ofEmpty().getFloat());
        Assert.assertNull(BValue.ofEmpty().getString());
        Assert.assertNull(BValue.ofEmpty().getRaw());
    }

    @Test(expected = InvalidTypeException.class)
    public void testEncodeHexUnsupported() {
        BValue.ofEmpty().encodeHex();
    }

    @Test(expected = InvalidTypeException.class)
    public void testDecodeHexUnsupported() {
        BValue.ofEmpty().decodeHex();
    }

    @Test(expected = InvalidTypeException.class)
    public void testEncodeBase64Unsupported() {
        BValue.ofEmpty().encodeBase64();
    }

    @Test(expected = InvalidTypeException.class)
    public void testDecodeBase64Unsupported() {
        BValue.ofEmpty().decodeBase64();
    }

    @Test
    public void testBigInteger() {
        var val = BValue.of(BigInteger.valueOf((long) Math.pow(2, 33)));
        Assert.assertEquals((long) Math.pow(2, 33), ((BigInteger) val.getData()).longValue());
        Assert.assertEquals((long) Math.pow(2, 33), (long) val.getLong());
        Assert.assertEquals(BType.GENERIC_NUMBER, val.getType());
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(1, (int) JSON_WRITER.toJsonElement(BValue.of(1)));
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
    }

    @Test
    public void testSerialization() {
        var val = BValue.of(0);
        var after = BElement.ofBytes(new String(val.toBytes()).getBytes());
        Assert.assertEquals(0, (int) after.getInnerValue());
    }

    @Test
    public void testConvert() {
        Assert.assertEquals(true, BValue.of(1).convertToBoolean().getData());
        Assert.assertEquals('A', BValue.of(65).convertToChar().getData());
        Assert.assertEquals((byte) 1, BValue.of(1).convertToByte().getData());
        Assert.assertEquals((short) 1, BValue.of(1).convertToShort().getData());
        Assert.assertEquals(1, BValue.of(true).convertToInteger().getData());
        Assert.assertEquals((long) Math.pow(2, 32), BValue.of(Math.pow(2, 32) + 0.1).convertToLong().getData());
        Assert.assertEquals(Math.pow(2, 32), BValue.of((long) Math.pow(2, 32)).convertToDouble().getData());
        Assert.assertEquals((float) Math.pow(2, 32), BValue.of((long) Math.pow(2, 32)).convertToFloat().getData());
        Assert.assertEquals("1", BValue.of(1).convertToString().getData());
        Assert.assertArrayEquals(new byte[] { 65, 66, 67 }, (byte[]) BValue.of("ABC").convertToRaw().getData());
    }
}
