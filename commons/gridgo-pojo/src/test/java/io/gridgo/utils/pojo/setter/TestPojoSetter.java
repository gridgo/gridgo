package io.gridgo.utils.pojo.setter;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.gridgo.utils.pojo.setter.data.SimpleKeyValueData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TestPojoSetter {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimplePojo {
        private String name;
    }

    @Data
    public static class PojoWithCollection {

        private int intValue;

        private Object nullPointer;

        private List<String> list;

        private List<SimplePojo> pojoList;

        private List<SimplePojo> pojoList2;

        private List<List<String>> nestedList;

        private Set<String> set;

        private Map<String, Object> map;

        private int[] intArr;

        private Object[] objArr;
    }

    @Test
    public void testPojoSetterCollection() {
        var list = List.of("s1", "s2", "s3");
        var nestedList = List.of(List.of("s1", "s2"), List.of("s3"));
        var pojoList = List.of(Map.of("name", "test1"), Map.of("name", "test2"));
        var pojoList2 = List.of(SimplePojo.builder().name("test1").build());
        var set = Set.of("s5", "s4", "s6");
        var map = Map.of("k1", "v1", "k2", "v2");
        var intArr = new int[] { 3, 4, 5, 6 };
        var objArr = new Object[] { "bach", 1, true };

        var src = new HashMap<String, Object>();
        src.put("intValue", 1);
        src.put("list", list);
        src.put("nestedList", nestedList);
        src.put("pojoList", pojoList);
        src.put("pojoList2", pojoList2);
        src.put("set", set);
        src.put("map", map);
        src.put("intArr", intArr);
        src.put("objArr", objArr);

        var obj = (PojoWithCollection) PojoSetter.ofType(PojoWithCollection.class) //
                .from(new SimpleKeyValueData(src)) //
                .fill();

        assertNull(obj.getNullPointer());
        assertEquals(1, obj.getIntValue());
        assertEquals(list, obj.getList());
        assertEquals(nestedList, obj.getNestedList());
        assertEquals(set, obj.getSet());
        assertEquals(map, obj.getMap());
        assertArrayEquals(intArr, obj.getIntArr());
        assertArrayEquals(objArr, obj.getObjArr());

        assertEquals(2, obj.getPojoList().size());
        assertEquals("test1", obj.getPojoList().get(0).getName());
        assertEquals("test2", obj.getPojoList().get(1).getName());

        assertEquals(1, obj.getPojoList2().size());
        assertEquals("test1", obj.getPojoList2().get(0).getName());
    }
}
