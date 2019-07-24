package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.PrimitiveArrayVO2;

public class TestPojoGetterArray2 extends AbstractTest {

    private PrimitiveArrayVO2 target = new PrimitiveArrayVO2();

    @Test
    public void testPrimitiveBoolean() {
        String fieldName = "booleanValue";
        Boolean[] value = new Boolean[] { true, false };

        target.setBooleanValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveChar() {
        String fieldName = "charValue";
        Character[] value = new Character[] { 'a', 'z' };

        target.setCharValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveByte() {
        String fieldName = "byteValue";
        Byte[] value = new Byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setByteValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveShort() {
        String fieldName = "shortValue";
        Short[] value = new Short[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setShortValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveInt() {
        String fieldName = "intValue";
        Integer[] value = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setIntValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveLong() {
        String fieldName = "longValue";
        Long[] value = new Long[] { 0l, 1l, 2l, 3l, 4l, 5l, 6l, 7l };

        target.setLongValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveFloat() {
        String fieldName = "floatValue";
        Float[] value = new Float[] { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f };

        target.setFloatValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveDouble() {
        String fieldName = "doubleValue";
        Double[] value = new Double[] { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 };

        target.setDoubleValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveString() {
        String fieldName = "stringValue";
        String[] value = new String[] { "test", "text" };

        target.setStringValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }
}
