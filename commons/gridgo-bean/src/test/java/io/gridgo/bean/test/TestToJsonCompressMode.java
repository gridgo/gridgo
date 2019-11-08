package io.gridgo.bean.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;

public class TestToJsonCompressMode {

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
    public void testMaxCompress() {
        var json = toJson("jsonMaxCompress");
        var obj = BElement.ofJson(json).asObject();
        assertFalse(obj.containsKey("nullValue"));
    }

    @Test
    public void testLtCompress() {
        var json = toJson("jsonLtCompress");
        var obj = BElement.ofJson(json).asObject();
        assertTrue(obj.containsKey("nullValue"));
    }

    @Test
    public void testNoCompress() {
        var json = toJson("json");
        var obj = BElement.ofJson(json).asObject();
        assertTrue(obj.containsKey("nullValue"));
    }

    @Test
    public void testNormalCompress() {
        var json = toJson("jsonNormal");
        var obj = BElement.ofJson(json).asObject();
        assertFalse(obj.containsKey("nullValue"));
    }

}
