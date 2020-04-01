package io.gridgo.utils.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class CaseInsensitiveHashMapTest {

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
