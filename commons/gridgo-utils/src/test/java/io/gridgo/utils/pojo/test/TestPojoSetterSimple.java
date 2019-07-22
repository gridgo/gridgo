package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestPojoSetterSimple {

    private final PrimitiveVO target = new PrimitiveVO();

    @Test
    public void testSimpleBoolean() throws Exception {
        String fieldName = "booleanValue";
        var value = true;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.isBooleanValue());
    }

    @Test
    public void testSimpleChar() throws Exception {
        String fieldName = "charValue";
        var value = 'z';

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getCharValue());
    }

    @Test
    public void testSimpleByte() throws Exception {
        String fieldName = "byteValue";
        byte value = 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getByteValue());
    }

    @Test
    public void testSimpleShort() throws Exception {
        String fieldName = "shortValue";
        var value = (short) 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getShortValue());
    }

    @Test
    public void testSimpleInt() throws Exception {
        String fieldName = "intValue";
        var value = 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getIntValue());
    }

    @Test
    public void testSimpleLong() throws Exception {
        String fieldName = "longValue";
        var value = (long) 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getLongValue());
    }

    @Test
    public void testSimpleFloat() throws Exception {
        String fieldName = "floatValue";
        var value = 10f;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getFloatValue(), 0.01);
    }

    @Test
    public void testSimpleDouble() throws Exception {
        String fieldName = "doubleValue";
        var value = (double) 10.1;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getDoubleValue(), 0.01);
    }

    @Test
    public void testSimpleString() throws Exception {
        String fieldName = "stringValue";
        String value = "test text";

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getStringValue());
    }
}
