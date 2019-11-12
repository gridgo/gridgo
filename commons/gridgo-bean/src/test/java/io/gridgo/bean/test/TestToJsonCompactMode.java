package io.gridgo.bean.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;

public class TestToJsonCompactMode {

    private BObject bObject;

    @Before
    public void setUp() {
        bObject = BObject.ofEmpty() //
                .setAny("intValue", 0) //
                .setAny("nullValue", null) //
                .setAny("stringValue", "this is test text");
    }

    private String toJson(String serializerName) {
        var value = new String(bObject.toBytes(serializerName));
        System.out.println(serializerName + ": " + value);
        return value;
    }

    @Test
    public void testMaxCompact() {
        var json = toJson("jsonMaxCompact");
        var obj = BElement.ofJson(json).asObject();
        assertFalse(obj.containsKey("nullValue"));
    }

    @Test
    public void testLtCompact() {
        var json = toJson("jsonLtCompact");
        var obj = BElement.ofJson(json).asObject();
        assertTrue(obj.containsKey("nullValue"));
    }

    @Test
    public void testNoCompact() {
        var json = toJson("json");
        var obj = BElement.ofJson(json).asObject();
        assertTrue(obj.containsKey("nullValue"));
    }

    @Test
    public void testNormalCompact() {
        var json = toJson("jsonNormalCompact");
        var obj = BElement.ofJson(json).asObject();
        assertFalse(obj.containsKey("nullValue"));
    }

}
