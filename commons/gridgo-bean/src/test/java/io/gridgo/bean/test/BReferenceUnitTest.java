package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import io.gridgo.bean.BReference;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.test.support.SimplePojo;

public class BReferenceUnitTest {

    @BeforeClass
    public static void init() {
        BFactory.DEFAULT.getSerializerRegistry().scan("io.gridgo.bean.test.support.supported");
    }

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
    public void testWriteByteBuffer() throws IOException {
        var ref = BReference.of(ByteBuffer.wrap("test".getBytes()));
        testTryWrite(ref);
    }

    @Test
    public void testWriteInputStream() throws IOException {
        try (var is = new ByteArrayInputStream("test".getBytes())) {
            var ref = BReference.of(is);
            testTryWrite(ref);
        }
    }

    @Test
    public void testWriteFile() throws IOException {
        File tempFile = File.createTempFile("gridgo_test", ".txt");
        tempFile.deleteOnExit();

        try (var fos = new FileOutputStream(tempFile)) {
            fos.write("test".getBytes());
            fos.flush();
        }

        var ref = BReference.of(tempFile);
        testTryWrite(ref);
    }

    private void testTryWrite(BReference ref) throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            var success = ref.tryWriteNativeBytes(baos);
            Assert.assertTrue(success);
            Assert.assertArrayEquals("test".getBytes(), baos.toByteArray());
        }
    }

    @Test
    public void testWriteWrongType() throws IOException {
        var ref = BReference.of(new Object());
        try (var baos = new ByteArrayOutputStream()) {
            var success = ref.tryWriteNativeBytes(baos);
            Assert.assertFalse(success);
        }
    }

    @Test
    public void testOfBytes() throws IOException {
        var ref = BReference.ofBytes("test".getBytes(), "simplepojo", SimplePojo.class);
        assertRef(ref);

        ref = BReference.ofBytes("test".getBytes(), "simplepojoobj", SimplePojo.class);
        assertRef(ref);
    }

    private void assertRef(BReference ref) {
        Assert.assertTrue(ref.getReference() instanceof SimplePojo);
        SimplePojo pojo = ref.getReference();
        Assert.assertEquals("test", pojo.getName());
    }

    @Test(expected = BeanSerializationException.class)
    public void testOfWrongType() throws IOException {
        BReference.ofBytes("test".getBytes(), "raw", SimplePojo.class);
    }
}
