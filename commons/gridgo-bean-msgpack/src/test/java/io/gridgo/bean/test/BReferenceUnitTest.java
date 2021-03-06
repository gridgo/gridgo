package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import io.gridgo.bean.BReference;
import io.gridgo.bean.test.support.Foo;

public class BReferenceUnitTest {

    @Test
    public void testOfBytes() {
        var pojo = Foo.builder() //
                .doubleValue(1.0) //
                .intValue(1) //
                // .byteValue((byte) -33) //
                .byteArrayValue(new byte[] { 1, 2, 3, 4 }) //
                .shortValue((short) 128) //
                .floatValue(1.1f) //
                .stringValue("hello") //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .longArrayValue(new long[] { 1, 2, 3, 4, (long) Math.pow(2, 33) }) //
                .longMap(Map.of("k1", 1L, "k2", 2L)) //
                .build();
        var ref = BReference.of(pojo);
        var bytes = ref.toBytes();
        var after = BReference.ofBytes(bytes, "raw", Foo.class);
        Foo foo = after.getReference();
        Assert.assertEquals(1.0, foo.getDoubleValue(), 0);
        Assert.assertEquals(1, foo.getIntValue());
        // Assert.assertEquals(-33, foo.getByteValue());
        Assert.assertEquals(128, foo.getShortValue());
        Assert.assertEquals(1.1f, foo.getFloatValue(), 0);
        Assert.assertEquals("hello", foo.getStringValue());
        Assert.assertArrayEquals(new int[] { 1, 2, 3, 4 }, foo.getIntArrayValue());
        Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, foo.getByteArrayValue());
        Assert.assertArrayEquals(new long[] { 1, 2, 3, 4, (long) Math.pow(2, 33) }, foo.getLongArrayValue());
        Assert.assertEquals(1, (long) foo.getLongMap().get("k1"));
        Assert.assertEquals(2, (long) foo.getLongMap().get("k2"));
    }
}
