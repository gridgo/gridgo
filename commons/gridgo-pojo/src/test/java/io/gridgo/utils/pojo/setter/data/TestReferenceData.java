package io.gridgo.utils.pojo.setter.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestReferenceData {

    private ReferenceData data;

    private Integer testValue;

    @Before
    public void setUp() {
        testValue = 1;
        data = new ReferenceData() {

            @Override
            public Object getReference() {
                return testValue;
            }
        };
    }

    @Test
    public void testGenericData() {
        assertFalse(data.isKeyValue());
        assertFalse(data.isPrimitive());
        assertTrue(data.isReference());
        assertFalse(data.isSequence());
        assertFalse(data.isNull());

        assertEquals(testValue, data.getInnerValue());
        assertEquals(testValue, data.getReference());

        assertEquals(Integer.class, data.getReferenceClass());

        assertEquals(1l, ((Integer) data.getReference()).longValue());

        testValue = null;
        assertNull(data.getReferenceClass());
        assertTrue(data.isNull());
    }

    @Test
    public void testAsReference() {
        assertEquals(data, data.asReference());
    }
}
