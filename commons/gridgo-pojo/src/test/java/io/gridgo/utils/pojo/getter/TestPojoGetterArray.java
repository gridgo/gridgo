package io.gridgo.utils.pojo.getter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.support.AbstractTest;
import io.gridgo.utils.pojo.support.PrimitiveArrayVO;

public class TestPojoGetterArray extends AbstractTest {

    private PrimitiveArrayVO target = new PrimitiveArrayVO();

    @Test
    public void testPrimitiveBoolean() {
        String fieldName = "booleanValue";
        boolean[] value = new boolean[] { true, false };

        target.setBooleanValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveChar() {
        String fieldName = "charValue";
        char[] value = new char[] { 'a', 'z' };

        target.setCharValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveByte() {
        String fieldName = "byteValue";
        byte[] value = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setByteValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveShort() {
        String fieldName = "shortValue";
        short[] value = new short[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setShortValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveInt() {
        String fieldName = "intValue";
        int[] value = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setIntValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveLong() {
        String fieldName = "longValue";
        long[] value = new long[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        target.setLongValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveFloat() {
        String fieldName = "floatValue";
        float[] value = new float[] { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f };

        target.setFloatValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveDouble() {
        String fieldName = "doubleValue";
        double[] value = new double[] { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 };

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
