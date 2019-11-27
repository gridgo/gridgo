package io.gridgo.utils.pojo.setter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import io.gridgo.utils.pojo.setter.data.SimpleKeyValueData;
import lombok.Data;

public class TestPojoSetter {

    @Data
    public static class PojoWithCollection {

        private int intValue;

        private Object nullPointer;

        private List<String> list;

        private Set<String> set;

        private Map<String, Object> map;

        private int[] intArr;

        private Object[] objArr;
    }

    @Test
    public void testPojoSetterCollection() {
        var list = List.of("s1", "s2", "s3");
        var set = Set.of("s5", "s4", "s6");
        var map = Map.of("k1", "v1", "k2", "v2");
        var intArr = new int[] { 3, 4, 5, 6 };
        var objArr = new Object[] { "bach", 1, true };

        var src = new HashMap<String, Object>();
        src.put("intValue", 1);
        src.put("list", list);
        src.put("set", set);
        src.put("map", map);
        src.put("intArr", intArr);
        src.put("objArr", objArr);

        var obj = (PojoWithCollection) PojoSetter.ofType(PojoWithCollection.class) //
                .from(new SimpleKeyValueData(src)) //
                .fill();

        assertNull(obj.getNullPointer());
        assertEquals(1, obj.getIntValue());
        assertTrue(list.equals(obj.getList()));
        assertTrue(set.equals(obj.getSet()));
        assertTrue(map.equals(obj.getMap()));
        assertArrayEquals(intArr, obj.getIntArr());
        assertArrayEquals(objArr, obj.getObjArr());
    }
}
