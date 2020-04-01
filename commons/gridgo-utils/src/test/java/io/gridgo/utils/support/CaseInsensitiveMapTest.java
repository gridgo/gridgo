package io.gridgo.utils.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CaseInsensitiveMapTest {

    private CaseInsensitiveMap<String> map;

    @Before
    public void init() {
        map = new CaseInsensitiveMap<String>(new HashMap<>());
    }

    @Test
    public void testPutAndGet() {
        map.put("SoMEkEy", "value");
        Assert.assertEquals("value", map.get("someKey"));
        Assert.assertEquals("value", map.get("SomeKEY"));
        Assert.assertEquals("value", map.get("somekey"));
        Assert.assertEquals(1, map.size());
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(map.containsKey("SoMeKEY"));
        Assert.assertTrue(map.containsValue("value"));
        Assert.assertEquals(Set.of("somekey"), map.keySet());
        map.putAll(Map.of(
                "Key1", "value1",
                "Key2", "value2"));
        Assert.assertEquals(Set.of("somekey", "key1", "key2"), map.keySet());
        Assert.assertEquals(Set.of("value1", "value2", "value"), new HashSet<>(map.values()));
    }

    @Test
    public void testRemove() {
        map.put("SoMEkEy", "value");
        map.remove("somekeY");
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testCompute() {
        map.computeIfAbsent("SoMEkEy1", k -> "value");
        Assert.assertEquals("value", map.get("someKey1"));

        map.compute("SoMEkEy2", (k, v) -> "value");
        Assert.assertEquals("value", map.get("someKey2"));

        map.computeIfPresent("SoMEkEy2", (k, v) -> "value2");
        Assert.assertEquals("value2", map.get("someKey2"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetNull() {
        map.get(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetNull() {
        map.put(null, "value");
    }
}
