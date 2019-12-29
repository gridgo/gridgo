package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.bean.BReference;

public class BReferenceUnitTest {

    @Test
    public void testIsReference() {
        Assert.assertTrue(BReference.ofEmpty().asOptional().isEmpty());
        Assert.assertTrue(BReference.of("hello").asOptional().isPresent());
        Assert.assertNull(BReference.ofEmpty().getReferenceClass());
        Assert.assertFalse(BReference.ofEmpty().referenceInstanceOf(String.class));
        Assert.assertEquals(String.class, BReference.of("hello").getReferenceClass());
        Assert.assertTrue(BReference.of("hello").referenceInstanceOf(String.class));
        Assert.assertTrue(BReference.of(new int[] { 1, 2, 3 }).referenceInstanceOf(int[].class));
        Assert.assertTrue(BReference.of(new int[] { 1, 2, 3 }).referenceInstanceOf(int[].class));
        Assert.assertEquals("hello", BReference.of("hello").getInnerValue());

        var counter = new AtomicInteger();
        BReference.of(new int[] { 1, 2, 3 }).ifReferenceInstanceOf(int[].class, arr -> {
            counter.addAndGet(Arrays.stream(arr).sum());
        });
        BReference.of(new int[] { 1, 2, 3 }).ifReferenceInstanceOf(double[].class, arr -> {
            counter.addAndGet((int) Arrays.stream(arr).sum());
        });
        Assert.assertEquals(6, counter.get());
    }
}
