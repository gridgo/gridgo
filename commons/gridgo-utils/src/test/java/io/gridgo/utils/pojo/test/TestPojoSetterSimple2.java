package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.PrimitiveVO2;

public class TestPojoSetterSimple2 {

    private final PrimitiveVO2 target = new PrimitiveVO2();

    @Test
    public void testSimpleBoolean() throws Exception {
        String fieldName = "booleanValue";
        var value = true;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getBooleanValue());
    }

    @Test
    public void testSimpleChar() throws Exception {
        String fieldName = "charValue";
        Character value = 'z';

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getCharValue());
    }

    @Test
    public void testSimpleByte() throws Exception {
        String fieldName = "byteValue";
        Byte value = 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getByteValue());
    }

    @Test
    public void testSimpleShort() throws Exception {
        String fieldName = "shortValue";
        Short value = (short) 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getShortValue());
    }

    @Test
    public void testSimpleInt() throws Exception {
        String fieldName = "intValue";
        Integer value = 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getIntValue());
    }

    @Test
    public void testSimpleLong() throws Exception {
        String fieldName = "longValue";
        Long value = (long) 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getLongValue());
    }

    @Test
    public void testSimpleFloat() throws Exception {
        String fieldName = "floatValue";
        Float value = 10f;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getFloatValue(), 0.01);
    }

    @Test
    public void testSimpleDouble() throws Exception {
        String fieldName = "doubleValue";
        Double value = (double) 10.1;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getDoubleValue(), 0.01);
    }
}
