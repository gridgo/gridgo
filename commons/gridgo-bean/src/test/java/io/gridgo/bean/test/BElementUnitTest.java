package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;

public class BElementUnitTest {

    @Test
    public void testIs() {
        Assert.assertTrue(BObject.ofEmpty().isObject());
        Assert.assertTrue(BArray.ofEmpty().isArray());
        Assert.assertTrue(BValue.ofEmpty().isValue());
        Assert.assertTrue(BReference.ofEmpty().isReference());
    }

    @Test
    public void testAsThen() {
        Assert.assertEquals(1, BObject.of("k1", 1).isObjectThen(b -> (int) b.getInteger("k1")));
        Assert.assertEquals(1, BArray.ofSequence(1).isArrayThen(b -> (int) b.getInteger(0)));
        Assert.assertEquals(1, BValue.of(1).isValueThen(b -> (int) b.getInteger()));
        Assert.assertEquals(1, BReference.of(1).isReferenceThen(b -> (int) b.getReference()));

        var counter = new AtomicInteger(0);
        BObject.of("k1", 1).isObjectThen(b -> {
            counter.addAndGet(b.getInteger("k1"));
        });
        BArray.ofSequence(1).isArrayThen(b -> {
            counter.addAndGet(b.getInteger(0));
        });
        BValue.of(1).isValueThen(b -> {
            counter.addAndGet(b.getInteger());
        });
        BReference.of(1).isReferenceThen(b -> {
            counter.addAndGet((int) b.getReference());
        });
        Assert.assertEquals(4, counter.intValue());
    }

    @Test
    public void testNotAsThen() {
        Assert.assertNull(BObject.of("k1", 1).isArrayThen(b -> 1));
        Assert.assertNull(BArray.ofSequence(1).isValueThen(b -> 1));
        Assert.assertNull(BValue.of(1).isReferenceThen(b -> 1));
        Assert.assertNull(BReference.of(1).isObjectThen(b -> 1));

        var counter = new AtomicInteger(0);
        BArray.ofSequence(1).isObjectThen(b -> {
            counter.addAndGet(b.getInteger("k1"));
        });
        BValue.of(1).isArrayThen(b -> {
            counter.addAndGet(b.getInteger(0));
        });
        BReference.of(1).isValueThen(b -> {
            counter.addAndGet(b.getInteger());
        });
        BObject.of("k1", 1).isReferenceThen(b -> {
            counter.addAndGet((int) b.getReference());
        });
        Assert.assertEquals(0, counter.intValue());

    }
}
