package io.gridgo.bean.test;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.BeanWithDate;
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

        var json = "{\"arr\":[1,2,3],\"bool\":true,\"byte\":1,\"char\":\"a\",\"double\":1.11,\"int\":1,\"long\":1,\"obj\":{\"int\":2},\"str\":\"hello\"}";
        Assert.assertEquals(json, obj.toJson());
        obj = BElement.ofJson(json);
        assertObject(obj);

        var xml = "<object><array name=\"arr\"><integer value=\"1\"/><integer value=\"2\"/><integer value=\"3\"/></array><string name=\"str\" value=\"hello\"/><boolean name=\"bool\" value=\"true\"/><integer name=\"byte\" value=\"1\"/><double name=\"double\" value=\"1.11\"/><object name=\"obj\"><integer name=\"int\" value=\"2\"/></object><string name=\"char\" value=\"a\"/><integer name=\"int\" value=\"1\"/><integer name=\"long\" value=\"1\"/></object>";

        Assert.assertEquals(xml, obj.toXml());
        obj = BElement.ofXml(xml);
        assertObject(obj);

        var map = obj.toMap();
        Assert.assertEquals(1, map.get("int"));
        Assert.assertEquals("hello", map.get("str"));
        Assert.assertEquals(1, map.get("long"));
        Assert.assertEquals("a", map.get("char"));
        Assert.assertEquals(1.11, map.get("double"));
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
                        .map(e -> e.asValue().getData()) //
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
        var bar = Bar.builder().b(true).build();
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).stringValue("hello").barValue(bar).build();
        BObject bObject = BObject.ofPojo(pojo);
        var deserialized = bObject.toPojo(Foo.class);
        Assert.assertEquals(pojo.getDoubleValue(), deserialized.getDoubleValue(), 0.0);
        Assert.assertEquals(pojo.getIntValue(), deserialized.getIntValue());
        Assert.assertEquals(pojo.getStringValue(), deserialized.getStringValue());
        Assert.assertEquals(pojo.getBarValue().isB(), deserialized.getBarValue().isB());
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
    public void testWriteString() {
        var pojo = Foo.builder().doubleValue(1.0).intValue(1).intArrayValue(new int[] { 1, 2 }).stringValue("hello")
                .build();
        var obj = BObject.ofPojo(pojo);
        Assert.assertNotNull(obj.toString());
    }

    @Test
    public void testBytes() {
        var obj = BObject.of("id", 1).setAny("_id", new int[] { 1, 2, 3 }).setAny("abc", null);
        var clone = BElement.ofBytes(obj.toBytes());
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone.isObject());
        Assert.assertEquals(1, clone.asObject().getInteger("id").intValue());
        Assert.assertEquals(obj, clone);
    }

    @Test
    public void testJsonWithNullFields() {
        var json = "{\"id\": null}";
        var obj = BElement.ofJson(json);
        Assert.assertTrue(obj.isObject());
        Assert.assertNotNull(obj.asObject().get("id"));
    }

    @Test
    public void testDate() {
        long time = 1555411032310L;
        var beanWithDate = new BeanWithDate();
        beanWithDate.setDate(new Date(time));

        beanWithDate = BObject.ofPojo(beanWithDate).toPojo(BeanWithDate.class);
        assertEquals("2019-04-16", beanWithDate.getDate().toString());

        var bObj = BObject.ofEmpty().setAny("date", beanWithDate.getDate());
        Date date = bObj.getReference("date").getReference();
        Assert.assertEquals(time, date.getTime());
    }

    @Test
    public void testWrapAndHolder() {
        var map = Map.of("k1", "v1");
        var obj = BObject.wrap(map);
        Assert.assertEquals(map, obj.toMap());

        obj = BObject.withHolder(new EmptyMap<>());
        obj.setAny("k1", "v1");
        Assert.assertNull(obj.getString("k1", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutSequenceLengthMismatch() {
        var obj = BObject.ofEmpty();
        obj.putAnySequence(1);
    }

    @Test
    public void testGetNullReference() {
        var obj = BObject.ofEmpty().setAny("null", null);
        Assert.assertNull(obj.getReference("dummy"));
        Assert.assertNull(obj.getReference("null"));
    }

    @Test(expected = InvalidTypeException.class)
    public void testGetInvalidReference() {
        var obj = BObject.ofEmpty().setAny("k1", "v1").setAny("ref", new Object());
        Assert.assertNotNull(obj.getReference("ref"));
        obj.getReference("k1");
    }

    class EmptyMap<K, V> extends HashMap<K, V> {

        private static final long serialVersionUID = -1298127233576511932L;

        @Override
        public V put(K key, V value) {
            return value;
        }
    }
}
