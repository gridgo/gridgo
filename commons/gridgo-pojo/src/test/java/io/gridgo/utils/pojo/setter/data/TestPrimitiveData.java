package io.gridgo.utils.pojo.setter.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestPrimitiveData {

    private PrimitiveData data;

    private Integer testValue;

    @Before
    public void setUp() {
        testValue = 1;
        data = new PrimitiveData() {

            @Override
            public Object getData() {
                return testValue;
            }
        };
    }

    @Test
    public void testGenericData() {
        assertFalse(data.isKeyValue());
        assertTrue(data.isPrimitive());
        assertFalse(data.isReference());
        assertFalse(data.isSequence());
        assertFalse(data.isNull());

        assertEquals(testValue, data.getInnerValue());
        assertEquals(testValue, data.getData());

        assertEquals(Integer.class, data.getDataClass());

        assertEquals(1l, data.getDataAs(Long.class).longValue());

        testValue = null;
        assertNull(data.getDataClass());
        assertNull(data.getDataAs(Long.class));
        assertTrue(data.isNull());
    }

    @Test
    public void testAsPrimitive() {
        assertEquals(data, data.asPrimitive());
    }
}
