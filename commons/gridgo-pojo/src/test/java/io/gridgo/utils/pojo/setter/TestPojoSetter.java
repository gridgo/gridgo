package io.gridgo.utils.pojo.setter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.data.SimpleKeyValueData;
import io.gridgo.utils.pojo.support.PojoWithCollection;
import io.gridgo.utils.pojo.support.SimplePojo;

public class TestPojoSetter {

    @Test
    public void testNullPointer() {
        var src = new HashMap<String, Object>();
        var obj = createPojo(src);
        assertNull(obj.getNullPointer());
    }

    @Test
    public void testPrimitive() {
        var src = Map.of("intValue", 1);
        var obj = createPojo(src);
        assertEquals(1, obj.getIntValue());
    }

    @Test
    public void testListPrimitive() {
        var list = List.of("s1", "s2", "s3");
        var src = Map.of("list", list);
        var obj = createPojo(src);
        assertEquals(list, obj.getList());
    }

    @Test
    public void testNestedList() {
        var nestedList = List.of(List.of("s1", "s2"), List.of("s3"));
        var src = Map.of("nestedList", nestedList);
        var obj = createPojo(src);
        assertEquals(2, obj.getNestedList().size());
        assertArrayEquals(new String[] {"s1", "s2"}, obj.getNestedList().get(0));
        assertArrayEquals(new String[] {"s3"}, obj.getNestedList().get(1));
    }

    @Test
    public void testPojoListFromMap() {
        var pojoList = List.of(Map.of("name", "test1"), Map.of("name", "test2"));
        var src = Map.of("pojoList", pojoList);
        var obj = createPojo(src);
        assertEquals(2, obj.getPojoList().size());
        assertEquals("test1", obj.getPojoList().get(0).getName());
        assertEquals("test2", obj.getPojoList().get(1).getName());
    }

    @Test
    public void testPojoListFromRef() {
        var pojoList = List.of(SimplePojo.builder().name("test1").build());
        var src = Map.of("pojoList", pojoList);
        var obj = createPojo(src);
        assertEquals(1, obj.getPojoList().size());
        assertEquals("test1", obj.getPojoList().get(0).getName());
    }

    @Test
    public void testSetPrimitive() {
        var set = Set.of("s5", "s4", "s6");
        var src = Map.of("set", set);
        var obj = createPojo(src);
        assertEquals(set, obj.getSet());
    }

    @Test
    public void testMap() {
        var map = Map.of("k1", "v1", "k2", "v2");
        var src = Map.of("map", map);
        var obj = createPojo(src);
        assertEquals(map, obj.getMap());
    }

    @Test
    public void testArray() {
        var intArr = new int[] { 3, 4, 5, 6 };
        var objArr = new Object[] { "bach", 1, true, SimplePojo.builder().name("test").build() };
        var src = Map.of("intArr", intArr, "objArr", objArr);
        var obj = createPojo(src);
        assertArrayEquals(intArr, obj.getIntArr());
        assertArrayEquals(objArr, obj.getObjArr());
    }

    @Test
    public void testArrayPojoFromMap() {
        var pojoArr = List.of(Map.of("name", "test1"), Map.of("name", "test2"));
        var src = Map.of("pojoArr", pojoArr);
        var obj = createPojo(src);
        assertEquals(2, obj.getPojoArr().length);
        assertEquals("test1", obj.getPojoArr()[0].getName());
        assertEquals("test2", obj.getPojoArr()[1].getName());
    }

    @Test
    public void testArrayPojoFromRef() {
        var pojoArr = List.of(SimplePojo.builder().name("test1").build());
        var src = Map.of("pojoArr", pojoArr);
        var obj = createPojo(src);
        assertEquals(1, obj.getPojoArr().length);
        assertEquals("test1", obj.getPojoArr()[0].getName());
    }

    @Test
    public void testPojoFromMap() {
        var pojo = Map.of("name", "test1");
        var src = Map.of("pojo", pojo);
        var obj = createPojo(src);
        assertEquals("test1", obj.getPojo().getName());
    }

    @Test
    public void testPojoFromRef() {
        var pojo = SimplePojo.builder().name("test1").build();
        var src = Map.of("pojo", pojo);
        var obj = createPojo(src);
        assertEquals(pojo, obj.getPojo());
    }

    @Test(expected = UnsupportedTypeException.class)
    public void testArrayPojoFromRefWrongType() {
        var pojoArr = List.of(new Object());
        var src = Map.of("pojoArr", pojoArr);
        createPojo(src);
    }

    @Test(expected = PojoProxyException.class)
    public void testSequenceWrongType() {
        var pojoArr = new Object();
        var src = Map.of("list", pojoArr);
        createPojo(src);
    }

    @Test(expected = PojoProxyException.class)
    public void testPrimitiveWrongType() {
        var pojoArr = new Object();
        var src = Map.of("intValue", pojoArr);
        createPojo(src);
    }

    @Test(expected = PojoProxyException.class)
    public void testMapWrongType() {
        var pojoArr = new Object();
        var src = Map.of("map", pojoArr);
        createPojo(src);
    }

    private PojoWithCollection createPojo(Map<String, ?> src) {
        return (PojoWithCollection) PojoSetter.ofType(PojoWithCollection.class) //
                .from(new SimpleKeyValueData(src)) //
                .fill();
    }
}
