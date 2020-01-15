package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;

public class PrimitiveUtilsUnitTest {

    @Test
    public void testGetValueFrom() {
        Assert.assertNull(PrimitiveUtils.getValueFrom(int.class, null));
        Assert.assertEquals(1, (int) PrimitiveUtils.getValueFrom(int.class, 1));
        Assert.assertEquals("1", PrimitiveUtils.getValueFrom(String.class, 1));
        Assert.assertEquals(1, (int) PrimitiveUtils.getValueFrom(Integer.class, 1));
        Assert.assertEquals(1, (int) PrimitiveUtils.getValueFrom(Integer.class, 1.1));
        Assert.assertEquals(1, (int) PrimitiveUtils.getValueFrom(int.class, Integer.valueOf(1)));
        Assert.assertTrue(PrimitiveUtils.getValueFrom(Boolean.class, 1));
        Assert.assertTrue(PrimitiveUtils.getValueFrom(boolean.class, 1));
        Assert.assertTrue(PrimitiveUtils.getValueFrom(boolean.class, "true"));
        Assert.assertFalse(PrimitiveUtils.getValueFrom(boolean.class, 0));
        Assert.assertFalse(PrimitiveUtils.getValueFrom(boolean.class, "false"));
        Assert.assertEquals(BigInteger.valueOf(1), PrimitiveUtils.getValueFrom(BigInteger.class, new byte[] {0, 0, 0, 1}));
        Assert.assertEquals(BigInteger.valueOf(1), PrimitiveUtils.getValueFrom(BigInteger.class, Integer.valueOf(1)));
        Assert.assertEquals(BigInteger.valueOf(1), PrimitiveUtils.getValueFrom(BigInteger.class, "1"));
        Assert.assertEquals(BigDecimal.valueOf(1), PrimitiveUtils.getValueFrom(BigDecimal.class, new byte[] {0, 0, 0, 1}));
        Assert.assertEquals(BigDecimal.valueOf(1.1), PrimitiveUtils.getValueFrom(BigDecimal.class, Double.valueOf(1.1)));
        Assert.assertEquals(BigDecimal.valueOf(1.1), PrimitiveUtils.getValueFrom(BigDecimal.class, "1.1"));
        Assert.assertEquals(Double.valueOf(1.1), PrimitiveUtils.getValueFrom(double.class, Double.valueOf(1.1)));
        Assert.assertEquals(Double.valueOf(1.1), PrimitiveUtils.getValueFrom(Double.class, Float.valueOf(1.1f)), 0.001);
        Assert.assertEquals('A', (char) PrimitiveUtils.getValueFrom(char.class, 65));
        Assert.assertEquals(Character.valueOf('A'), PrimitiveUtils.getValueFrom(Character.class, 65));
        Assert.assertEquals(Float.valueOf(1.1f), PrimitiveUtils.getValueFrom(Float.class, Double.valueOf(1.1)));
        Assert.assertEquals(Float.valueOf(1.1f), PrimitiveUtils.getValueFrom(float.class, Double.valueOf(1.1)));
        Assert.assertEquals(Long.valueOf((long) Math.pow(2, 33)), PrimitiveUtils.getValueFrom(Long.class, Math.pow(2, 33)));
        Assert.assertEquals(Long.valueOf((long) Math.pow(2, 33)), PrimitiveUtils.getValueFrom(long.class, Math.pow(2, 33)));
        Assert.assertEquals(Byte.valueOf((byte) 1), PrimitiveUtils.getValueFrom(byte.class, Integer.valueOf(1)));
        Assert.assertEquals(Byte.valueOf((byte) 1), PrimitiveUtils.getValueFrom(Byte.class, Integer.valueOf(1)));
        Assert.assertEquals(Short.valueOf((short) 1), PrimitiveUtils.getValueFrom(short.class, Integer.valueOf(1)));
        Assert.assertEquals(Short.valueOf((short) 1), PrimitiveUtils.getValueFrom(Short.class, Integer.valueOf(1)));
    }

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
    public void testIsNumberClass() {
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Integer.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Float.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Double.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Long.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Byte.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(Short.class));

        Assert.assertTrue(PrimitiveUtils.isNumberClass(int.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(float.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(double.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(long.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(byte.class));
        Assert.assertTrue(PrimitiveUtils.isNumberClass(short.class));
    }

    @Test
    public void testIsPrimitive() {
        Assert.assertTrue(PrimitiveUtils.isPrimitive(int.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Byte.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(String.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Boolean.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Character.TYPE));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Boolean.TYPE));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Character.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Integer.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Long.class));
        Assert.assertTrue(PrimitiveUtils.isPrimitive(Double.class));
        Assert.assertFalse(PrimitiveUtils.isPrimitive(int[].class));
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
        Assert.assertEquals((byte) 0, PrimitiveUtils.getByteValueFrom(""));
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getByteValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getByteValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getByteValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getByteValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getByteValueFrom(new byte[] { 127 }));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetByteUnsupported() {
        PrimitiveUtils.getByteValueFrom(new Object());
    }

    @Test
    public void testGetShort() {
        Assert.assertEquals((short) 0, PrimitiveUtils.getShortValueFrom(""));
        Assert.assertEquals(127, PrimitiveUtils.getShortValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getShortValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getShortValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getShortValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getShortValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getShortValueFrom(false));
        Assert.assertEquals(257, PrimitiveUtils.getShortValueFrom(new byte[] { 1, 1 }));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetShortUnsupported() {
        PrimitiveUtils.getShortValueFrom(new Object());
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

    @Test(expected = UnsupportedTypeException.class)
    public void testGetCharUnsupported() {
        PrimitiveUtils.getCharValueFrom(new Object());
    }

    @Test
    public void testGetDouble() {
        Assert.assertEquals(0.0, PrimitiveUtils.getDoubleValueFrom(""), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom(97), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom('a'), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom("97.0"), 0);
        Assert.assertEquals(1.0, PrimitiveUtils.getDoubleValueFrom(true), 0);
        Assert.assertEquals(0.0, PrimitiveUtils.getDoubleValueFrom(false), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getDoubleValueFrom(new byte[] { 64, 88, 64, 0, 0, 0, 0, 0 }), 0);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetDoubleUnsupported() {
        PrimitiveUtils.getDoubleValueFrom(new Object());
    }

    @Test
    public void testGetFloat() {
        Assert.assertEquals(0.0f, PrimitiveUtils.getFloatValueFrom(""), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom(97), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom('a'), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom("97.0"), 0);
        Assert.assertEquals(1.0, PrimitiveUtils.getFloatValueFrom(true), 0);
        Assert.assertEquals(0.0, PrimitiveUtils.getFloatValueFrom(false), 0);
        Assert.assertEquals(97.0, PrimitiveUtils.getFloatValueFrom(new byte[] { 66, -62, 0, 0, 0, 0, 0, 0 }), 0);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetFloatUnsupported() {
        PrimitiveUtils.getFloatValueFrom(new Object());
    }

    @Test
    public void testGetLong() {
        Assert.assertEquals(0, PrimitiveUtils.getLongValueFrom(""));
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getLongValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getLongValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getLongValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getLongValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getLongValueFrom(new byte[] { 0, 0, 0, 0, 0, 0, 0, 127 }));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetLongUnsupported() {
        PrimitiveUtils.getLongValueFrom(new Object());
    }

    @Test
    public void testGetInteger() {
        Assert.assertEquals(0, PrimitiveUtils.getIntegerValueFrom(""));
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom(127));
        Assert.assertEquals(-128, PrimitiveUtils.getIntegerValueFrom(-128));
        Assert.assertEquals(97, PrimitiveUtils.getIntegerValueFrom('a'));
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom("127"));
        Assert.assertEquals(1, PrimitiveUtils.getIntegerValueFrom(true));
        Assert.assertEquals(0, PrimitiveUtils.getIntegerValueFrom(false));
        Assert.assertEquals(127, PrimitiveUtils.getIntegerValueFrom(new byte[] { 0, 0, 0, 127 }));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetIntegerUnsupported() {
        PrimitiveUtils.getIntegerValueFrom(new Object());
    }

    @Test
    public void testGetString() {
        Assert.assertEquals(null, PrimitiveUtils.getStringValueFrom(null));
        Assert.assertEquals("abc", PrimitiveUtils.getStringValueFrom("abc"));
        Assert.assertEquals(new String(new byte[] { 100, 50, 25, 75, 90 }),
                PrimitiveUtils.getStringValueFrom(new byte[] { 100, 50, 25, 75, 90 }));
        Assert.assertEquals("127", PrimitiveUtils.getStringValueFrom(127));
        Assert.assertEquals("class io.gridgo.utils.PrimitiveUtils", PrimitiveUtils.class.toString());
    }

    @Test
    public void testWrapperType() {
        Assert.assertEquals(Void.class, PrimitiveUtils.getWrapperType(void.class));
        Assert.assertEquals(Boolean.class, PrimitiveUtils.getWrapperType(boolean.class));
        Assert.assertEquals(Character.class, PrimitiveUtils.getWrapperType(char.class));
        Assert.assertEquals(Byte.class, PrimitiveUtils.getWrapperType(byte.class));
        Assert.assertEquals(Short.class, PrimitiveUtils.getWrapperType(short.class));
        Assert.assertEquals(Integer.class, PrimitiveUtils.getWrapperType(int.class));
        Assert.assertEquals(Long.class, PrimitiveUtils.getWrapperType(long.class));
        Assert.assertEquals(Float.class, PrimitiveUtils.getWrapperType(float.class));
        Assert.assertEquals(Double.class, PrimitiveUtils.getWrapperType(double.class));
        Assert.assertNull(PrimitiveUtils.getWrapperType(String.class));

        Assert.assertTrue(PrimitiveUtils.isWrapperType(Void.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Boolean.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Character.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Byte.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Short.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Integer.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Long.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Float.class));
        Assert.assertTrue(PrimitiveUtils.isWrapperType(Double.class));
        Assert.assertFalse(PrimitiveUtils.isWrapperType(String.class));

        Assert.assertEquals(void.class, PrimitiveUtils.getPrimitiveFromWrapperType(Void.class));
        Assert.assertEquals(boolean.class, PrimitiveUtils.getPrimitiveFromWrapperType(Boolean.class));
        Assert.assertEquals(char.class, PrimitiveUtils.getPrimitiveFromWrapperType(Character.class));
        Assert.assertEquals(byte.class, PrimitiveUtils.getPrimitiveFromWrapperType(Byte.class));
        Assert.assertEquals(short.class, PrimitiveUtils.getPrimitiveFromWrapperType(Short.class));
        Assert.assertEquals(int.class, PrimitiveUtils.getPrimitiveFromWrapperType(Integer.class));
        Assert.assertEquals(long.class, PrimitiveUtils.getPrimitiveFromWrapperType(Long.class));
        Assert.assertEquals(float.class, PrimitiveUtils.getPrimitiveFromWrapperType(Float.class));
        Assert.assertEquals(double.class, PrimitiveUtils.getPrimitiveFromWrapperType(Double.class));
        Assert.assertNull(PrimitiveUtils.getPrimitiveFromWrapperType(String.class));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testGetValueUnsupported() {
        PrimitiveUtils.getValueFrom(Map.class, new Object());
    }

    @Test
    public void testGetBigDecimalFrom() {
        Assert.assertEquals(BigDecimal.valueOf(1.1), PrimitiveUtils.getBigDecimalFrom(BigDecimal.valueOf(1.1)));
        Assert.assertEquals(BigDecimal.valueOf(1), PrimitiveUtils.getBigDecimalFrom(BigInteger.valueOf(1)));
        Assert.assertEquals(BigDecimal.valueOf(1.1f), PrimitiveUtils.getBigDecimalFrom(1.1f));
    }

    @Test
    public void testGetBigIntegerFrom() {
        Assert.assertEquals(BigInteger.valueOf(1), PrimitiveUtils.getBigIntegerFrom(BigInteger.valueOf(1)));
        Assert.assertEquals(BigInteger.valueOf(1), PrimitiveUtils.getBigIntegerFrom(BigDecimal.valueOf(1.1)));
    }
}
