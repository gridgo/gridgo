package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.ArbitraryPrecisionNumerical;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.ChildFoo;
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
        Assert.assertArrayEquals(new Long[] { 1l, 2l, 3l, 4l },
                after.getArray("intArrayValue").toList().toArray(new Long[0]));

        ref = BReference.of(pojo.getIntArrayValue());
        json = ref.toJson();
        var arr = BElement.ofJson(json).asArray();
        Assert.assertArrayEquals(new Long[] { 1l, 2l, 3l, 4l }, arr.toList().toArray(new Long[0]));

        ref = BReference.of(pojo.getIntValue());
        json = ref.toJson();
        var val = BElement.ofJson(json).asValue();
        Assert.assertEquals(1l, val.getData());

        var obj = BObject.of("ref", BReference.of(pojo));
        json = obj.toJson();
        after = BElement.ofJson(json).asObject();
        Assert.assertEquals(1, (int) after.getObject("ref").getInteger("intValue"));
        Assert.assertArrayEquals(new Long[] { 1l, 2l, 3l, 4l },
                after.getObject("ref").getArray("intArrayValue").toList().toArray(new Long[0]));
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
                .set("obj", BObject.ofEmpty().setAny("int", 2));

        var out = new ByteArrayOutputStream();
        obj.writeBytes(out, "json");
        byte[] bytes = out.toByteArray();

        BElement unpackedEle = BElement.ofBytes(new ByteArrayInputStream(bytes), "json");
        assertNotNull(unpackedEle);
        assertTrue(unpackedEle.isObject());
        unpackedEle.asObject().getValue("raw").decodeHex();

        assertEquals(obj.toJson(), unpackedEle.toJson());
    }

    @Test
    public void testJsonValue() {
        var val = BValue.of(1);
        assertEquals("1", val.toJson());
    }

    @Test
    public void testPojoJsonCompact() {
        Foo foo = Foo.builder() //
                .intArrayValue(new int[] { 1, 2, 3, 4 }) //
                .doubleValue(0.123) //
                .barValue(Bar.builder() //
                        .bool(true) //
                        .build()) //
                .build();

        var reference = BReference.of(foo);
        var json = new String(reference.toBytes("jsonCompact"));
        var after = BElement.ofJson(json).asObject().toPojo(Foo.class);

        Assert.assertArrayEquals(foo.getIntArrayValue(), after.getIntArrayValue());
        Assert.assertEquals(foo.getDoubleValue(), after.getDoubleValue(), 0);
        Assert.assertEquals(foo.getBarValue().isBool(), after.getBarValue().isBool());
    }

    @Test
    public void testPojoJsonCompactAnnotation() {
        ChildFoo foo = ChildFoo.builder() //
                .stringValue(null) // should be serialized
                .ignoredString(null) // should be ignored
                .ignoredList(null) // should be ignored
                .build();

        var reference = BReference.of(foo);
        var json = new String(reference.toBytes("json"));
        System.out.println(json);
        var after = BElement.ofJson(json).asObject();

        assertFalse(after.containsKey("ignoredString"));
        assertFalse(after.containsKey("ignoredList"));
        assertTrue(after.containsKey("stringValue"));
    }

    @Test
    public void testArbitraryPrecisionNumerical() {
        double dValue = 3.01E+7;
        var decimal = new BigDecimal(dValue);
        var integer = BigInteger.valueOf(1232341234134513453l);

        double d1 = 0.03;
        double d2 = 0.02;
        double d = d1 - d2;

        var value = ArbitraryPrecisionNumerical.builder() //
                .decimal(decimal) //
                .integer(integer) //
                .d(d) //
                .build();

        var json = BReference.of(value).toJson();
        assertEquals(BReference.of(value).toBObject(), BElement.ofJson(json));
    }

    @Test
    public void testJsonFromString() {
        var orginalText = "this is test text";
        var value = BElement.ofJson(orginalText);
        assertTrue(value.isValue());
        assertEquals(orginalText, value.asValue().getString());
    }
}
