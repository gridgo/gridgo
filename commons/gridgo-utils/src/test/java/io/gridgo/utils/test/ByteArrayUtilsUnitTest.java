package io.gridgo.utils.test;

import static io.gridgo.utils.ByteArrayUtils.bytesToInt;
import static io.gridgo.utils.ByteArrayUtils.bytesToLong;
import static io.gridgo.utils.ByteArrayUtils.bytesToPrimitive;
import static io.gridgo.utils.ByteArrayUtils.leftTrimZero;
import static io.gridgo.utils.ByteArrayUtils.primitiveToBytes;
import static io.gridgo.utils.ByteArrayUtils.toHex;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.utils.ByteArrayUtils;

public class ByteArrayUtilsUnitTest {

    @Test
    public void testHex() {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        String origin = Arrays.toString(bytes);

        String hex = toHex(bytes, "0x");

        byte[] dehexBytes = ByteArrayUtils.fromHex(hex);
        String dehex = Arrays.toString(dehexBytes);

        assertEquals(origin, dehex);
    }

    @Test
    public void testConcat() {
        byte[] b1 = new byte[] { 1, 2 };
        byte[] b2 = new byte[] { 3, 4 };
        byte[] result = ByteArrayUtils.concat(b1, b2);
        assertEquals(4, result.length);
        assertEquals(1, result[0]);
        assertEquals(2, result[1]);
        assertEquals(3, result[2]);
        assertEquals(4, result[3]);
        result = ByteArrayUtils.concat();
        assertEquals(0, result.length);
    }

    @Test
    public void testPrimitives() {
        assertArrayEquals(new byte[] { 0 }, primitiveToBytes(false));
        assertArrayEquals(new byte[] { 1 }, primitiveToBytes(true));
        boolean bool = bytesToPrimitive(Boolean.class, new byte[] { 1 });
        assertTrue(bool);
        bool = bytesToPrimitive(Boolean.class, new byte[] { 0 });
        assertTrue(!bool);
        bool = bytesToPrimitive(Boolean.class, new byte[] { 0, 1 });
        assertTrue(bool);
        bool = bytesToPrimitive(Boolean.class, new byte[] { 0, 0 });
        assertTrue(!bool);

        assertArrayEquals(new byte[] { 1 }, primitiveToBytes((byte) 1));
        assertArrayEquals(new byte[] { 0 }, primitiveToBytes((byte) 0));
        byte b = bytesToPrimitive(Byte.class, new byte[] { 1 });
        assertEquals(1, b);

        short sh = bytesToPrimitive(Short.class, new byte[] { 1, 1 });
        assertEquals(257, sh);

        int i = bytesToPrimitive(Integer.class, new byte[] { 1, 1, 1, 1 });
        assertEquals(16843009, i);

        float f = bytesToPrimitive(Float.class, new byte[] { 63, -128, 0, 0 });
        assertEquals(1f, f, 0);

        long l = bytesToPrimitive(Long.class, new byte[] { 1, 0, 0, 0, 0, 0, 0, 0 });
        assertEquals(72057594037927936L, l);

        char c = bytesToPrimitive(Character.class, new byte[] { 0, 97 });
        assertEquals('a', c);

        long[] longValues = new long[] { Long.MAX_VALUE, -1, 0l, Long.MIN_VALUE, 52365l };
        for (long longValue : longValues) {
            byte[] bytes = primitiveToBytes(longValue);
            bytes = leftTrimZero(bytes);

            assertEquals(longValue, bytesToPrimitive(Long.class, bytes).longValue());
            assertEquals(longValue, bytesToLong(bytes).longValue());
        }

        int[] intValues = new int[] { Integer.MAX_VALUE, -1, 0, Integer.MIN_VALUE, 52365 };
        for (int intValue : intValues) {
            byte[] bytes = primitiveToBytes(intValue);
            bytes = leftTrimZero(bytes);

            assertEquals(intValue, bytesToPrimitive(Integer.class, bytes).intValue());
            assertEquals(intValue, bytesToInt(bytes).intValue());
        }

        short[] shortValues = new short[] { Short.MAX_VALUE, -1, 0, Short.MIN_VALUE, 31265 };
        for (short shortValue : shortValues) {
            byte[] bytes = primitiveToBytes(shortValue);
            bytes = leftTrimZero(bytes);

            assertEquals(shortValue, bytesToPrimitive(Short.class, bytes).shortValue());
            assertEquals(shortValue, ByteArrayUtils.bytesToShort(bytes).shortValue());
        }
        byte[] byteValues = new byte[] { Byte.MAX_VALUE, -1, 0, Byte.MIN_VALUE, (byte) 200, (byte) -200 };
        for (byte byteValue : byteValues) {
            byte[] bytes = primitiveToBytes(byteValue);
            bytes = leftTrimZero(bytes);

            assertEquals(byteValue, bytesToPrimitive(Byte.class, bytes).byteValue());
            assertEquals(byteValue, ByteArrayUtils.bytesToByte(bytes).byteValue());
        }

        float[] floatValues = new float[] { Float.MAX_VALUE, -1.0f, 0, Float.MIN_VALUE, 0.123f, 12344.12134f };
        for (float floatValue : floatValues) {
            byte[] bytes = primitiveToBytes(floatValue);
            bytes = leftTrimZero(bytes);

            assertEquals(floatValue, bytesToPrimitive(Float.class, bytes).floatValue(), 0.0f);
            assertEquals(floatValue, ByteArrayUtils.bytesToFloat(bytes).floatValue(), 0.0f);
        }

        double[] doubleValues = new double[] { Double.MAX_VALUE, -1.0, 0, Double.MIN_VALUE, 0.123, 12344.12134 };
        for (double doubleValue : doubleValues) {
            byte[] bytes = primitiveToBytes(doubleValue);
            bytes = leftTrimZero(bytes);

            assertEquals(doubleValue, bytesToPrimitive(Double.class, bytes).doubleValue(), 0.0);
            assertEquals(doubleValue, ByteArrayUtils.bytesToDouble(bytes).doubleValue(), 0.0);
        }
    }

    @Test
    public void testPrimitivesWithBuffer() {
        Assert.assertEquals(2.2, ByteArrayUtils.bytesToNumber(primitiveToBytes(2.2), true));
        Assert.assertEquals(2, ByteArrayUtils.bytesToNumber(primitiveToBytes(2), false));
        Assert.assertEquals(2L, ByteArrayUtils.bytesToNumber(primitiveToBytes(2L), false));
        Assert.assertEquals(Character.valueOf('a'), bytesToPrimitive(Character.class, primitiveToBytes('a')));
    }
}
