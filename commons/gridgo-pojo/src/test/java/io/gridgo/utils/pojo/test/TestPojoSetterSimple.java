package io.gridgo.utils.pojo.test;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.test.support.AbstractTest;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;
import io.gridgo.utils.pojo.test.support.TransientVO;
import io.gridgo.utils.pojo.test.support.ValueTranslatedVO;

public class TestPojoSetterSimple extends AbstractTest {

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
        Integer value = 10;

        PojoUtils.setValue(target, fieldName, value);
        assertEquals(value.longValue(), target.getLongValue());
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
        var value = 10.1;

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

    @Test
    public void testTransient() {
        var transientVO = new TransientVO(false, false);
        PojoUtils.setValue(transientVO, "transientValue", true);
        PojoUtils.setValue(transientVO, "booleanValue", true);
        Assert.assertFalse(transientVO.isTransientValue());
        Assert.assertTrue(transientVO.isBooleanValue());
    }

    @Test
    public void testValueTranslator() {
        var translatedVO = new ValueTranslatedVO(null, null);
        PojoUtils.setValue(translatedVO, "translatedValue", "1");
        PojoUtils.setValue(translatedVO, "translatedValue2", "2");
    }
}
