package io.gridgo.utils.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.utils.ArrayUtils;

public class ArrayUtilsUnitTest {

    @Test
    public void testIsArray() {
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Byte[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(String[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Object[].class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(Collection.class));
        Assert.assertTrue(ArrayUtils.isArrayOrCollection(new ArrayList<String>().getClass()));
        Assert.assertFalse(ArrayUtils.isArrayOrCollection(ArrayUtils.class));
    }

    @Test
    public void testForEachInteger() {
        var atomic = new AtomicInteger(0);
        var list = new Integer[] { 1, 2, 3, 4 };
        ArrayUtils.<Integer>foreach(list, e -> atomic.addAndGet(e));
        Assert.assertEquals(10, atomic.get());
    }

    @Test
    public void testForEachInt() {
        var atomic = new AtomicInteger(0);
        var list = new int[] { 1, 2, 3, 4 };
        ArrayUtils.<Integer>foreach(list, e -> atomic.addAndGet(e));
        Assert.assertEquals(10, atomic.get());
    }

    @Test
    public void testForEachBool() {
        var trueCounter = new AtomicInteger(0);
        var falseCounter = new AtomicInteger(0);
        var list = new boolean[] { true, false, true, false };
        ArrayUtils.<Boolean>foreach(list, e -> {
            if (e)
                trueCounter.addAndGet(1);
            else
                falseCounter.addAndGet(1);
        });
        Assert.assertEquals(2, trueCounter.get());
        Assert.assertEquals(2, falseCounter.get());
    }

    @Test
    public void testLength() {
        var list = new Integer[] { 1, 2, 3, 4 };
        Assert.assertEquals(4, ArrayUtils.length(list));
        var list2 = Arrays.asList(new Integer[] { 1, 2, 3, 4 });
        Assert.assertEquals(4, ArrayUtils.length(list2));
        var map = Collections.singletonMap("k", 1);
        Assert.assertEquals(-1, ArrayUtils.length(map));
    }

    @Test
    public void testToArray() {
        var arr = new Integer[] { 1, 2, 3, 4 };
        var list2 = Arrays.asList(new Integer[] { 1, 2, 3, 4 });
        Assert.assertArrayEquals(arr, ArrayUtils.toArray(Integer.class, list2));
    }

    @Test
    public void testToString() {
        var arr1 = new Integer[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr1));
        var arr2 = new int[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr2));
        var arr3 = new boolean[] { true, false, true, false };
        Assert.assertEquals("true, false, true, false", ArrayUtils.toString(arr3));
        var arr4 = new byte[] { 0x10, 0x20, 0x30, 0x40 };
        Assert.assertEquals("16, 32, 48, 64", ArrayUtils.toString(arr4));
        var arr5 = new float[] { 1.23f, 2.34f, 3.45f, 4.56f };
        Assert.assertEquals("1.23, 2.34, 3.45, 4.56", ArrayUtils.toString(arr5));
        var arr6 = new long[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr6));
        var arr7 = new char[] { 'a', 'b', 'c', 'd' };
        Assert.assertEquals("a, b, c, d", ArrayUtils.toString(arr7));
        var arr8 = new double[] { 1.23, 2.34, 3.45, 4.56 };
        Assert.assertEquals("1.23, 2.34, 3.45, 4.56", ArrayUtils.toString(arr8));
        var arr9 = new short[] { 1, 2, 3, 4 };
        Assert.assertEquals("1, 2, 3, 4", ArrayUtils.toString(arr9));
    }

    @Test
    public void testEntryAt() {
        Assert.assertEquals(2, ArrayUtils.entryAt(new int[] { 1, 2, 3 }, 1));
        Assert.assertEquals(2, ArrayUtils.entryAt(Arrays.asList(1, 2, 3), 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEntryAtInvalidIndex() {
        Assert.assertEquals(2, ArrayUtils.entryAt(Arrays.asList(1, 2, 3), -1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEntryAtInvalidClass() {
        Assert.assertEquals(2, ArrayUtils.entryAt(new HashSet<>(), 0));
    }

    @Test
    public void testToPrimitiveArray() {
        Assert.assertNull(ArrayUtils.toPrimitiveArray(null, int.class));
        Assert.assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) ArrayUtils.toPrimitiveArray(Arrays.asList(1, 2, 3), int.class));
        Assert.assertArrayEquals(new long[] { 1, 2, 3 }, (long[]) ArrayUtils.toPrimitiveArray(Arrays.asList(1L, 2L, 3L), long.class));
        Assert.assertArrayEquals(new double[] { 1.1, 2.2, 3.3 },
                (double[]) ArrayUtils.toPrimitiveArray(Arrays.asList(1.1, 2.2, 3.3), double.class), 0);
        Assert.assertArrayEquals(new float[] { 1.1f, 2.2f, 3.3f },
                (float[]) ArrayUtils.toPrimitiveArray(Arrays.asList(1.1f, 2.2f, 3.3f), float.class), 0);
        Assert.assertArrayEquals(new byte[] { 1, 2, 3 },
                (byte[]) ArrayUtils.toPrimitiveArray(Arrays.asList((byte) 1, (byte) 2, (byte) 3), byte.class));
        Assert.assertArrayEquals(new short[] { 1, 2, 3 },
                (short[]) ArrayUtils.toPrimitiveArray(Arrays.asList((short) 1, (short) 2, (short) 3), short.class));
        Assert.assertArrayEquals(new char[] { 'a', 'b', 'c' },
                (char[]) ArrayUtils.toPrimitiveArray(Arrays.asList('a', 'b', 'c'), char.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToPrimitiveArrayInvalidClass() {
        ArrayUtils.toPrimitiveArray(Arrays.asList(1, 2, 3), Integer.class);
    }

    @Test
    public void testIncompatibleConversion() {
        Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 },
                (byte[]) ArrayUtils.toPrimitiveArray(Arrays.asList(1, 2.2, 3.3f, 4L), byte.class));
        Assert.assertArrayEquals(new int[] { 1, 2, 3, 4 },
                (int[]) ArrayUtils.toPrimitiveArray(Arrays.asList((byte) 1, 2.2, 3.3f, 4L), int.class));
        Assert.assertArrayEquals(new double[] { 1, 2.2, 3.3, 4 },
                (double[]) ArrayUtils.toPrimitiveArray(Arrays.asList((byte) 1, 2.2, 3.3f, 4L), double.class), 0.0001);
    }
}
