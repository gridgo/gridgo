package io.gridgo.utils.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

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
    public void testForEach() {
        var atomic = new AtomicInteger(0);
        var list = new Integer[] { 1, 2, 3, 4 };
        ArrayUtils.<Integer>foreach(list, e -> atomic.addAndGet(e));
        Assert.assertEquals(10, atomic.get());
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
}
