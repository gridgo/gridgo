package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;

public class TestJsonSerializer {

    @Test
    public void testArray() {
        var arr = BArray.ofSequence(1, 2, 3, 4);
        var json = arr.toJson();
        var after = BElement.ofJson(json);
        Assert.assertEquals(arr, after);
    }

    @Test
    public void testRef() {
        var pojo = Foo.builder() //
                .intValue(1) //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .build();

        var ref = BReference.of(pojo);
        var json = ref.toJson();
        var after = BElement.ofJson(json).asObject();
        Assert.assertEquals(1, (int) after.getInteger("intValue"));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), after.getArray("intArrayValue").toList());

        ref = BReference.of(pojo.getIntArrayValue());
        json = ref.toJson();
        var arr = BElement.ofJson(json).asArray();
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), arr.toList());

        ref = BReference.of(pojo.getIntValue());
        json = ref.toJson();
        var val = BElement.ofJson(json).asValue();
        Assert.assertEquals(1, val.getData());

        var obj = BObject.of("ref", BReference.of(pojo));
        json = obj.toJson();
        after = BElement.ofJson(json).asObject();
        Assert.assertEquals(1, (int) after.getObject("ref").getInteger("intValue"));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), after.getObject("ref").getArray("intArrayValue").toList());
    }

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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        obj.writeBytes(out, "json");
        byte[] bytes = out.toByteArray();

        BElement unpackedEle = BElement.ofBytes(new ByteArrayInputStream(bytes), "json");
        assertNotNull(unpackedEle);
        assertTrue(unpackedEle.isObject());
        unpackedEle.asObject().getValue("raw").decodeHex();

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
        var json = new String(reference.toBytes("jsonMaxCompress"));
        var after = BElement.ofJson(json).asObject().toPojo(Foo.class);

        Assert.assertArrayEquals(foo.getIntArrayValue(), after.getIntArrayValue());
        Assert.assertEquals(foo.getDoubleValue(), after.getDoubleValue(), 0);
        Assert.assertEquals(foo.getBarValue().isB(), after.getBarValue().isB());
    }
}
