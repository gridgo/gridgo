package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.ImmutableBObject;
import io.gridgo.bean.test.support.Bar;
import io.gridgo.bean.test.support.Foo;
import io.gridgo.utils.ObjectUtils;

public class TestImmutable {

    @Test
    public void testImmutableObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "GridGo");
        map.put("year", 2018);
        map.put("list", new Object[] { "str", 1, true });

        BElement obj = BElement.wrapAny(map);
        System.out.println(obj.toString());

        assertTrue(obj instanceof ImmutableBObject);

        assertEquals("GridGo", obj.asObject().getString("name"));
        assertEquals(Double.valueOf(2018), obj.asObject().getDouble("year"));

        BArray arr = obj.asObject().getArray("list");
        assertEquals("str", arr.getString(0));
        assertEquals(Integer.valueOf(1), arr.getInteger(1));
        assertTrue(arr.getBoolean(2));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void testImmutablePojo() {
        Map<String, Integer> map = new HashMap<>();
        map.put("0", 0);
        map.put("1", 1);
        Bar bar = Bar.builder().b(true).map(map).build();
        Foo foo = new Foo(0, new int[] { 1, 2, 3 }, 22.02, "this is test text", bar);

        Map<String, Object> fooMap = ObjectUtils.toMap(foo);
        BElement obj = BElement.wrapAny(fooMap);
        System.out.println(obj.toString());

        assertTrue(obj instanceof ImmutableBObject);
        assertEquals(Integer.valueOf(foo.getIntValue()), obj.asObject().getInteger("intValue"));
        assertTrue(obj.asObject().getArray("intArrayValue").equals(foo.getIntArrayValue()));
        assertEquals(Double.valueOf(foo.getDoubleValue()), obj.asObject().getDouble("doubleValue"));
        assertEquals(foo.getStringValue(), obj.asObject().getString("stringValue"));
        assertEquals(foo.getBarValue(), obj.asObject().getReference("barValue").getReference());

        assertTrue(obj.equals(fooMap));
    }
}
