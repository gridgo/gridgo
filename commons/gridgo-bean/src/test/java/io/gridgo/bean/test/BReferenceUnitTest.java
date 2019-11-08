package io.gridgo.bean.test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BReference;
import io.gridgo.bean.serialization.json.writer.CompositeJsonWriter;
import io.gridgo.bean.test.support.Foo;

public class BReferenceUnitTest {

    private static final CompositeJsonWriter JSON_WRITER = CompositeJsonWriter.getNoCompressInstance();

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

    @Test
    public void testToJson() {
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").build();
        var ref = BReference.of(pojo);
        @SuppressWarnings("unchecked")
        var jsonElement = (Map<String, Object>) JSON_WRITER.getRefJsonWriter().toJsonElement(ref);
        Assert.assertEquals(1.0, jsonElement.get("doubleValue"));
        Assert.assertEquals(1, jsonElement.get("intValue"));
        Assert.assertEquals("hello", jsonElement.get("stringValue"));
        Assert.assertEquals(pojo, ref.getInnerValue());
    }

    @Test
    public void testOfBytes() {
        var pojo = Foo.builder() //
                .doubleValue(1.0) //
                .intValue(1) //
                .byteValue((byte) -33) //
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
        Assert.assertEquals(-33, foo.getByteValue());
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
