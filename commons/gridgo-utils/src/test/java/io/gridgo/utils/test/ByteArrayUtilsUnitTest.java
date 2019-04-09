package io.gridgo.utils.test;

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

        String hex = ByteArrayUtils.toHex(bytes, "0x");

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
        assertArrayEquals(new byte[] { 0 }, ByteArrayUtils.primitiveToBytes(false));
        assertArrayEquals(new byte[] { 1 }, ByteArrayUtils.primitiveToBytes(true));
        boolean bool = ByteArrayUtils.bytesToPrimitive(Boolean.class, new byte[] { 1 });
        assertTrue(bool);
        bool = ByteArrayUtils.bytesToPrimitive(Boolean.class, new byte[] { 0 });
        assertTrue(!bool);
        bool = ByteArrayUtils.bytesToPrimitive(Boolean.class, new byte[] { 0, 1 });
        assertTrue(bool);
        bool = ByteArrayUtils.bytesToPrimitive(Boolean.class, new byte[] { 0, 0 });
        assertTrue(!bool);

        assertArrayEquals(new byte[] { 1 }, ByteArrayUtils.primitiveToBytes((byte) 1));
        assertArrayEquals(new byte[] { 0 }, ByteArrayUtils.primitiveToBytes((byte) 0));
        byte b = ByteArrayUtils.bytesToPrimitive(Byte.class, new byte[] { 1 });
        assertEquals(1, b);

        short sh = ByteArrayUtils.bytesToPrimitive(Short.class, new byte[] { 1, 1 });
        assertEquals(257, sh);

        int i = ByteArrayUtils.bytesToPrimitive(Integer.class, new byte[] { 1, 1, 1, 1 });
        assertEquals(16843009, i);

        float f = ByteArrayUtils.bytesToPrimitive(Float.class, new byte[] { 63, -128, 0, 0 });
        assertEquals(1f, f, 0);

        long l = ByteArrayUtils.bytesToPrimitive(Long.class, new byte[] { 1, 0, 0, 0, 0, 0, 0, 0 });
        assertEquals(72057594037927936L, l);

        char c = ByteArrayUtils.bytesToPrimitive(Character.class, new byte[] { 0, 97 });
        assertEquals('a', c);
    }

    @Test
    public void testPrimitivesWithBuffer() {
        Assert.assertEquals(2.2, ByteArrayUtils.bytesToNumber(ByteArrayUtils.primitiveToBytes(2.2), true));
        Assert.assertEquals(2, ByteArrayUtils.bytesToNumber(ByteArrayUtils.primitiveToBytes(2), false));
        Assert.assertEquals(2L, ByteArrayUtils.bytesToNumber(ByteArrayUtils.primitiveToBytes(2L), false));
        Assert.assertEquals(Character.valueOf('a'),
                ByteArrayUtils.bytesToPrimitive(Character.class, ByteArrayUtils.primitiveToBytes('a')));
    }
}
