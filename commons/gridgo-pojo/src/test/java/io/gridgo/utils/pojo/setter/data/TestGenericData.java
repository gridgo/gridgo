package io.gridgo.utils.pojo.setter.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class TestGenericData {

    private GenericData genericData;

    @Before
    public void setUp() {
        genericData = new GenericData() {

            @Override
            public Object getInnerValue() {
                return null;
            }
        };
    }

    @Test
    public void testGenericData() {
        assertFalse(genericData.isKeyValue());
        assertFalse(genericData.isPrimitive());
        assertFalse(genericData.isReference());
        assertFalse(genericData.isSequence());
        assertFalse(genericData.isNull());

        assertNull(genericData.getInnerValue());
    }
}
