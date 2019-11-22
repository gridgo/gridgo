package io.gridgo.utils.pojo.setter.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class TestKeyValueData {

    private KeyValueData data;
    private Map<String, GenericData> map;
    private GenericData testData;

    @Before
    public void setUp() {
        testData = new GenericData() {

            @Override
            public Object getInnerValue() {
                return null;
            }
        };

        map = new HashMap<String, GenericData>();
        map.put("test", testData);

        data = new KeyValueData() {

            @Override
            public Iterator<Entry<String, GenericData>> iterator() {
                return map.entrySet().iterator();
            }

            @Override
            public GenericData getOrTake(String key, Supplier<GenericData> onAbsentSupplier) {
                if (map.containsKey(key))
                    return map.get(key);
                return onAbsentSupplier.get();
            }

            @Override
            public GenericData getOrDefault(String key, GenericData def) {
                return map.getOrDefault(key, def);
            }

            @Override
            public GenericData get(String key) {
                return map.get(key);
            }
        };
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericData() {
        assertTrue(data.isKeyValue());
        assertFalse(data.isPrimitive());
        assertFalse(data.isReference());
        assertFalse(data.isSequence());
        assertFalse(data.isNull());

        assertTrue(data.getInnerValue() instanceof Map);
        assertEquals(1, ((Map) data.getInnerValue()).size());

        assertEquals(testData, data.get("test"));
        assertNull(data.get("not_exist"));
        assertEquals(data, data.getOrDefault("not_exist", data));
    }

    @Test
    public void testAsKeyValue() {
        assertTrue(data.asKeyValue() == data);
    }

    @Test(expected = RuntimeException.class)
    public void testAsPrimitive() {
        assertNull(data.asPrimitive());
    }

    @Test(expected = RuntimeException.class)
    public void testAsSequence() {
        assertNull(data.asSequence());
    }

    @Test(expected = RuntimeException.class)
    public void testAsReference() {
        assertNull(data.asReference());
    }
}
