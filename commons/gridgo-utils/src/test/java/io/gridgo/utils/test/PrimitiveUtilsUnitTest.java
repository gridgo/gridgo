package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.PrimitiveUtils;

public class PrimitiveUtilsUnitTest {

    @Test
    public void testIsNumber() {
        Assert.assertTrue(PrimitiveUtils.isNumber((byte) 1));
        Assert.assertTrue(PrimitiveUtils.isNumber(1));
        Assert.assertTrue(PrimitiveUtils.isNumber(1.0));
        Assert.assertTrue(PrimitiveUtils.isNumber(1L));
        Assert.assertFalse(PrimitiveUtils.isNumber(null));
        Assert.assertFalse(PrimitiveUtils.isNumber("abc"));
    }

    @Test
    public void testIsPrimitive() {
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Byte.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(String.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Boolean.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Character.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Integer.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Long.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Double.class));
        Assert.assertFalse(PrimitiveUtils.isPrimitive(Byte[].class));
        Assert.assertFalse(PrimitiveUtils.isPrimitive(String[].class));
        Assert.assertFalse(PrimitiveUtils.isPrimitive(Object[].class));
        Assert.assertFalse(PrimitiveUtils.isPrimitive(PrimitiveUtils.class));
    }

    @Test
    public void testGetBoolean() {
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom(1));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom(0));
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom("true"));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom("false"));
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom('a'));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom('\0'));
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom(Boolean.TRUE));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom(Boolean.FALSE));
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom(new Object()));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom(null));
        Assert.assertTrue(PrimitiveUtils.getBooleanValueFrom(new byte[] { 0, 0, 0, 0, 0, 0, 0, 1 }));
        Assert.assertFalse(PrimitiveUtils.getBooleanValueFrom(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }));
    }

    @Test
    public void testGetByte() {
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getByteValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getByteValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getByteValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getByteValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom(new byte[] { 127 }));
    }

    @Test
    public void testGetShort() {
        Assert.assertEquals(127, PrimitiveUtils.getShortValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getShortValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getShortValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getShortValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getShortValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getShortValueFrom(false));
        Assert.assertEquals(257, PrimitiveUtils.getShortValueFrom(new byte[] { 1, 1 }));
    }

    @Test
    public void testGetChar() {
        Assert.assertEquals('a', PrimitiveUtils.getCharValueFrom(97));
        Assert.assertEquals('a', PrimitiveUtils.getCharValueFrom('a'));
        Assert.assertEquals('a', PrimitiveUtils.getCharValueFrom("abc"));
        Assert.assertEquals('\0', PrimitiveUtils.getCharValueFrom(""));
        Assert.assertEquals('1', PrimitiveUtils.getCharValueFrom(true));
        Assert.assertEquals('0', PrimitiveUtils.getCharValueFrom(false));
        Assert.assertEquals('a', PrimitiveUtils.getCharValueFrom(new byte[] { 0, 97 }));
    }

    @Test
    public void testGetDouble() {
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom(97), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom('a'), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom("97.0"), 0);
        Assert.assertEquals(1.0, PrimitiveUtils.getDoubleValueFrom(true), 0);
        Assert.assertEquals(0.0, PrimitiveUtils.getDoubleValueFrom(false), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom(new byte[] { 64, 88, 64, 0, 0, 0, 0, 0 }), 0);
    }

    @Test
    public void testGetFloat() {
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom(97), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom('a'), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom("97.0"), 0);
        Assert.assertEquals(1.0, PrimitiveUtils.getFloatValueFrom(true), 0);
        Assert.assertEquals(0.0, PrimitiveUtils.getFloatValueFrom(false), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom(new byte[] { 66, -62, 0, 0, 0, 0, 0, 0 }), 0);
    }

    @Test
    public void testGetLong() {
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getLongValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getLongValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getLongValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getLongValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom(new byte[] { 0, 0, 0, 0, 0, 0, 0, 127 }));
    }

    @Test
    public void testGetInteger() {
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getIntegerValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getIntegerValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getIntegerValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getIntegerValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom(new byte[] { 0, 0, 0, 127 }));
    }

    @Test
    public void testGetString() {
        Assert.assertEquals(null, PrimitiveUtils.getStringValueFrom(null));
        Assert.assertEquals("abc", PrimitiveUtils.getStringValueFrom("abc"));
        Assert.assertEquals(new String(new byte[] { 100, 50, 25, 75, 90 }), PrimitiveUtils.getStringValueFrom(new byte[] { 100, 50, 25, 75, 90 }));
        Assert.assertEquals("127", PrimitiveUtils.getStringValueFrom(127));
        Assert.assertEquals("class io.gridgo.utils.PrimitiveUtils", PrimitiveUtils.class.toString());
    }
}
