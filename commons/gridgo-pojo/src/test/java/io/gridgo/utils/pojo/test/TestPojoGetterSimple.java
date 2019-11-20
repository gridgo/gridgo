package io.gridgo.utils.pojo.test;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;
import io.gridgo.utils.pojo.test.support.TransientVO;

public class TestPojoGetterSimple extends AbstractTest {

    private PrimitiveVO target = new PrimitiveVO();

    @Test
    public void testPrimitiveBoolean() {
        String fieldName = "booleanValue";
        boolean value = true;

        target.setBooleanValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveChar() {
        String fieldName = "charValue";
        char value = 'z';

        target.setCharValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveByte() {
        String fieldName = "byteValue";
        byte value = 10;

        target.setByteValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveShort() {
        String fieldName = "shortValue";
        short value = 10;

        target.setShortValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveInt() {
        String fieldName = "intValue";
        int value = 10;

        target.setIntValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveLong() {
        String fieldName = "longValue";
        long value = 10;

        target.setLongValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveFloat() {
        String fieldName = "floatValue";
        float value = 10f;

        target.setFloatValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveDouble() {
        String fieldName = "doubleValue";
        double value = 10;

        target.setDoubleValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testPrimitiveString() {
        String fieldName = "stringValue";
        String value = "test text";

        target.setStringValue(value);

        var got = PojoUtils.getValue(target, fieldName);
        assertEquals(value, got);
    }

    @Test
    public void testTransient() {
        var transientVO = new TransientVO(true, true);
        Assert.assertNull(PojoUtils.getValue(transientVO, "transientValue"));
        Assert.assertTrue((boolean) PojoUtils.getValue(transientVO, "booleanValue"));
    }
}
