package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.PrimitiveArrayVO2;

public class TestPojoSetterArray2 extends AbstractTest {

    private final PrimitiveArrayVO2 target = new PrimitiveArrayVO2();

    @Test
    public void testSimpleBoolean() throws Exception {
        String fieldName = "booleanValue";
        Boolean[] value = new Boolean[] { true, false };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getBooleanValue());
    }

    @Test
    public void testSimpleChar() throws Exception {
        String fieldName = "charValue";
        Character[] value = new Character[] { 'a', 'z' };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getCharValue());
    }

    @Test
    public void testSimpleByte() throws Exception {
        String fieldName = "byteValue";
        Byte[] value = new Byte[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getByteValue());
    }

    @Test
    public void testSimpleShort() throws Exception {
        String fieldName = "shortValue";
        Short[] value = new Short[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getShortValue());
    }

    @Test
    public void testSimpleInt() throws Exception {
        String fieldName = "intValue";
        Integer[] value = new Integer[] { 0, 1, 2, 3 };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getIntValue());
    }

    @Test
    public void testSimpleLong() throws Exception {
        String fieldName = "longValue";
        Long[] value = new Long[] { 0l, 1l, 2l, 3l };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getLongValue());
    }

    @Test
    public void testSimpleFloat() throws Exception {
        String fieldName = "floatValue";
        Float[] value = new Float[] { 0.0f, 0.1f, 0.2f, 0.3f };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getFloatValue());
    }

    @Test
    public void testSimpleDouble() throws Exception {
        String fieldName = "doubleValue";
        Double[] value = new Double[] { 0.0, 0.1, 0.2, 0.3 };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getDoubleValue());
    }

    @Test
    public void testSimpleString() throws Exception {
        String fieldName = "stringValue";
        String[] value = new String[] { "test", "text" };

        PojoUtils.setValue(target, fieldName, value);
        assertArrayEquals(value, target.getStringValue());
    }
}
