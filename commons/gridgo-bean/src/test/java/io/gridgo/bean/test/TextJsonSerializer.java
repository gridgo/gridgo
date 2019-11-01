package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;

public class TextJsonSerializer {

    @Test
    public void testJsonSerializer() {
        var obj = BObject.ofEmpty() //
                .setAny("bool", false) //
                .set("int", BValue.of(1)) //
                .setAny("long", 1L) //
                .setAny("char", 'a') //
                .setAny("str", "hello") //
                .setAny("double", 1.11) //
                .setAny("byte", (byte) 1) //
                .setAny("raw", new byte[] { 1, 2, 3, 4, 5, 6 }) //
                .setAny("arr", new int[] { 1, 2, 3 }) //
                .set("obj", BObject.ofEmpty().setAny("int", 2)) //
        ;

        // System.out.println("origin object: " + obj);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        obj.writeBytes(out, "json");
        byte[] bytes = out.toByteArray();

        BElement unpackedEle = BElement.ofBytes(new ByteArrayInputStream(bytes), "json");
        assertNotNull(unpackedEle);
        assertTrue(unpackedEle.isObject());
        unpackedEle.asObject().getValue("raw").decodeHex();

        // System.out.println("unpacked object: " + unpackedEle);
        assertEquals(obj, unpackedEle);
    }

    @Test
    public void testJsonValue() {
        var val = BValue.of(1);
        assertEquals("1", val.toJson());
    }

    @Test
    public void testSerializePojo() {
        Foo foo = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .b(true) //
                        .build()) //
                .build();

        var reference = BReference.of(foo);
        var json = reference.toJson();
        var after = BElement.ofJson(json).asObject().toPojo(Foo.class);

        Assert.assertArrayEquals(foo.getIntArrayValue(), after.getIntArrayValue());
        Assert.assertEquals(foo.getDoubleValue(), after.getDoubleValue(), 0);
        Assert.assertEquals(foo.getBarValue().isB(), after.getBarValue().isB());
    }
}
