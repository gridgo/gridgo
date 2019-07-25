package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.PrimitiveArrayVO;

public class TestPojoSetterArray extends AbstractTest {

    private final PrimitiveArrayVO target = new PrimitiveArrayVO();

    @Test
    public void testSimpleBoolean() throws Exception {
        String fieldName = "booleanValue";
        boolean[] value = new boolean[] { true, false };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getBooleanValue());
    }

    @Test
    public void testSimpleChar() throws Exception {
        String fieldName = "charValue";
        char[] value = new char[] { 'a', 'z' };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getCharValue());
    }

    @Test
    public void testSimpleByte() throws Exception {
        String fieldName = "byteValue";
        byte[] value = new byte[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getByteValue());
    }

    @Test
    public void testSimpleShort() throws Exception {
        String fieldName = "shortValue";
        short[] value = new short[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getShortValue());
    }

    @Test
    public void testSimpleInt() throws Exception {
        String fieldName = "intValue";
        int[] value = new int[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getIntValue());
    }

    @Test
    public void testSimpleLong() throws Exception {
        String fieldName = "longValue";
        long[] value = new long[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getLongValue());
    }

    @Test
    public void testSimpleFloat() throws Exception {
        String fieldName = "floatValue";
        float[] value = new float[] { 0.0f, 0.1f, 0.2f, 0.3f };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getFloatValue());
    }

    @Test
    public void testSimpleDouble() throws Exception {
        String fieldName = "doubleValue";
        double[] value = new double[] { 0.0, 0.1, 0.2, 0.3 };

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value, target.getDoubleValue());
    }

    @Test
    public void testSimpleString() throws Exception {
        String fieldName = "stringValue";
        String[] value = new String[] { "test", "text" };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getStringValue());
    }
}
