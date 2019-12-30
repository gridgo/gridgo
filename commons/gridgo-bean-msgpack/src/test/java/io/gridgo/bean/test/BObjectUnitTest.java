package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;

public class BObjectUnitTest {

    @Test
    public void testSetAny() {
        var obj = BObject.ofEmpty() //
                .set("int", BValue.of(1)) //
                .setAny("long", 1L) //
                .setAny("char", 'a') //
                .setAny("str", "hello") //
                .setAny("double", 1.11) //
                .setAny("byte", (byte) 1) //
                .setAny("arr", new int[] { 1, 2, 3 }) //
                .set("obj", BObject.ofEmpty().setAny("int", 2));
        assertObject(obj);
        assertObject(obj.deepClone());
        obj.setAnyIfAbsent("arr", 1);
        assertObject(obj);
        Assert.assertTrue(obj.getBoolean("bool", true));
        obj.setAny("bool", true);
        Assert.assertTrue(obj.getBoolean("bool", false));

        var map = obj.toMap();
        Assert.assertEquals(1, ((Number) map.get("int")).intValue());
        Assert.assertEquals("hello", map.get("str"));
        Assert.assertEquals(1l, map.get("long"));
        Assert.assertEquals('a', map.get("char"));
        Assert.assertEquals(1.11, ((Number) map.get("double")).doubleValue(), 0.001);
        var list = (List<?>) map.get("arr");
        Assert.assertArrayEquals(new Integer[] { 1, 2, 3 }, list.toArray());

        obj = BElement.ofBytes(obj.toBytes());
        assertObject(obj);

        byte[] raw = obj.getRaw("str", new byte[0]);
        Assert.assertEquals("hello", new String(raw));

        obj.setAny("short", (short) 1);
        Assert.assertEquals((short) 1, (short) obj.getShort("short", (short) -1));

        obj = BObject.ofSequence("int", 1, "str", "hello", "long", 1, "char", 'a', "double", 1.11, "arr",
                new int[] { 1, 2, 3 }, "byte", 1, "short", 1, "obj", Collections.singletonMap("int", 2));
        assertObject(obj);
    }

    private void assertObject(BObject obj) {
        Assert.assertEquals(Integer.valueOf(1), obj.getInteger("int", -1));
        Assert.assertEquals("hello", obj.getString("str", null));
        Assert.assertArrayEquals(new Integer[] { 1, 2, 3 }, //
                obj.getArray("arr", BArray.ofEmpty()).stream() //
                        .map(BElement::asValue) //
                        .map(BValue::getInteger) //
                        .toArray(size -> new Integer[size]));
        Assert.assertEquals(Long.valueOf(1L), obj.getLong("long", -1));
        Assert.assertEquals(Character.valueOf('a'), obj.getChar("char", '\0'));
        Assert.assertEquals(1.11, obj.getDouble("double", -1), 0);
        Assert.assertEquals(1.11, obj.getFloat("double", -1), 0.001);
        Assert.assertEquals(Byte.valueOf((byte) 1), obj.getByte("byte", -1));
        Assert.assertEquals(Integer.valueOf(2), obj.getObject("obj", null).getInteger("int"));
    }

    @Test
    public void testPojoRecursive() {
        var bar = Bar.builder().bool(true).build();
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").barValue(bar).build();
        BObject bObject = BObject.ofPojo(pojo);
        var deserialized = bObject.toPojo(Foo.class);
        Assert.assertEquals(pojo.getDoubleValue(), deserialized.getDoubleValue(), 0.0);
        Assert.assertEquals(pojo.getIntValue(), deserialized.getIntValue());
        Assert.assertEquals(pojo.getStringValue(), deserialized.getStringValue());
        Assert.assertEquals(pojo.getBarValue().isBool(), deserialized.getBarValue().isBool());
        var wrapped = BObject.ofEmpty().setAnyPojo("k1", pojo);
        Assert.assertEquals(pojo.getDoubleValue(), wrapped.getObject("k1").getDouble("doubleValue"), 0);
        Assert.assertEquals(pojo.getIntValue(), (int) wrapped.getObject("k1").getInteger("intValue"));
    }

    @Test
    public void testPojo() {
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").build();
        var deserialized = BObject.ofPojo(pojo).toPojo(Foo.class);
        Assert.assertEquals(pojo.getDoubleValue(), deserialized.getDoubleValue(), 0.0);
        Assert.assertEquals(pojo.getIntValue(), deserialized.getIntValue());
        Assert.assertEquals(pojo.getStringValue(), deserialized.getStringValue());
    }

    @Test
    public void testNullOrMissing() {
        Assert.assertNull(BObject.ofPojo(null));
        var obj = BObject.ofEmpty();
        Assert.assertEquals(1, (int) obj.getInteger("k1", 1));
        Assert.assertNull(obj.getInteger("k1", null));
        Assert.assertEquals(1.1f, obj.getFloat("k1", 1.1f), 0);
        Assert.assertNull(obj.getFloat("k1", null));
        Assert.assertEquals(1L, (long) obj.getLong("k1", 1));
        Assert.assertNull(obj.getLong("k1", null));
        Assert.assertEquals(1.1, obj.getDouble("k1", 1.1f), 0.001);
        Assert.assertNull(obj.getDouble("k1", null));
    }

    @Test
    public void testBytes() {
        var obj = BObject.of("id", 1) //
                .setAny("abc", null) //
                .setAny("_id", new int[] { 1, 2, 3 }) //
                .setAny("byteArr", BValue.of(new byte[] { 1, 2, 3, 4 })) //
        ;
        var clone = BElement.ofBytes(obj.toBytes());
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone.isObject());
        Assert.assertEquals(1, clone.asObject().getInteger("id").intValue());
        Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, clone.asObject().getRaw("byteArr"));
        Assert.assertEquals(obj, clone);
    }
}
