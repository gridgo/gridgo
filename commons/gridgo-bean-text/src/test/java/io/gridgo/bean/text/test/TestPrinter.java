package io.gridgo.bean.text.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.serialization.text.BPrinter;
import io.gridgo.bean.text.test.support.Bar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPrinter {

    private BValue raw;
    private Bar bar;
    private BReference ref;
    private BObject obj;
    private BArray arr;

    @Before
    public void setUp() {
        this.raw = BValue.of(new byte[] { 1, 2, 3, 4, 5, 6 });
        this.bar = new Bar();
        this.ref = BReference.of(bar);
        this.obj = BObject.ofEmpty() //
                .setAny("ref", bar) //
                .setAny("bool", false) //
                .set("int", BValue.of(1)) //
                .setAny("long", 1L) //
                .setAny("char", 'a') //
                .setAny("str", "hello") //
                .setAny("double", 1.11) //
                .setAny("byte", (byte) 1) //
                .setAny("raw", raw) //
                .setAny("arr", new int[] { 1, 2, 3 }) //
                .setAny("emptyArray", BArray.ofEmpty()) //
                .set("obj", BObject.ofEmpty().setAny("int", 2)) //
        ;
        this.arr = BArray.ofSequence(obj, 1, true, new byte[] { 4, 5, 6, 7 }, bar);
    }

    @Test
    public void testPrint() {
        log.debug("{}", obj);
    }

    @Test
    public void testAll() {
        Assert.assertNotNull(raw.toString());
        Assert.assertNotNull(ref.toString());
        Assert.assertNotNull(obj.toString());
        Assert.assertNotNull(arr.toString());

        Assert.assertFalse(raw.toString().isEmpty());
        Assert.assertFalse(ref.toString().isEmpty());
        Assert.assertFalse(obj.toString().isEmpty());
        Assert.assertFalse(arr.toString().isEmpty());
    }

    @Test
    public void testOutputStream() throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            BPrinter.print(baos, raw);
            baos.flush();
            Assert.assertTrue(baos.toByteArray().length > 0);
        }
    }

    @Test
    public void testSerialization() throws IOException {
        Assert.assertEquals(raw.toString(), new String(raw.toBytes("print")));
        Assert.assertEquals(ref.toString(), new String(ref.toBytes("print")));
        Assert.assertEquals(obj.toString(), new String(obj.toBytes("print")));
        Assert.assertEquals(arr.toString(), new String(arr.toBytes("print")));
    }

    @Test
    public void testWriteString() {
        var arr = BArray.ofSequence(1, 2, 3);
        Assert.assertNotNull(arr.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupported() {
        BElement.ofBytes(raw.toBytes("print"), "print");
    }
}
