package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.factory.BFactory;

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
        Assert.assertEquals(1, (int) BObject.of("k1", 1).isObjectThen(b -> {
            return (int) b.getInteger("k1");
        }));
        Assert.assertEquals(1, (int) BArray.ofSequence(1).isArrayThen(b -> {
            return (int) b.getInteger(0);
        }));
        Assert.assertEquals(1, (int) BValue.of(1).isValueThen(b -> {
            return (int) b.getInteger();
        }));
        Assert.assertEquals(1, (int) BReference.of(1).isReferenceThen(b -> {
            return (int) b.getReference();
        }));

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
        Assert.assertNull(BObject.of("k1", 1).isArrayThen((Function<BArray, Integer>) b -> 1));
        Assert.assertNull(BArray.ofSequence(1).isValueThen((Function<BValue, Integer>) b -> 1));
        Assert.assertNull(BValue.of(1).isReferenceThen((Function<BReference, Integer>) b -> 1));
        Assert.assertNull(BReference.of(1).isObjectThen((Function<BObject, Integer>) b -> 1));

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

    @Test(expected = BeanSerializationException.class)
    public void testSerializerNotFound() {
        var val = BValue.of(1);
        val.toBytes("test");
    }

    @Test
    public void testMockJson() {
        BFactory.DEFAULT.getSerializerRegistry().scan("io.gridgo.bean.test.support");
        var element = BElement.ofJson("some_random_string");
        Assert.assertTrue(element != null && element.isValue());
        Assert.assertEquals("test", element.asValue().getString());
    }
}
