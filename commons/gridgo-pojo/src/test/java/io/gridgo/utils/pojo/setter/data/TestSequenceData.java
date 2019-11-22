package io.gridgo.utils.pojo.setter.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestSequenceData {

    private SequenceData data;
    private List<GenericData> list;
    private GenericData testData;

    @Before
    public void setUp() {
        testData = new GenericData() {

            @Override
            public Object getInnerValue() {
                return null;
            }
        };

        list = new ArrayList<GenericData>();
        list.add(testData);

        data = new SequenceData() {

            @Override
            public Iterator<GenericData> iterator() {
                return list.iterator();
            }

            @Override
            public GenericData get(int index) {
                return list.get(index);
            }
        };
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testGenericData() {
        assertFalse(data.isKeyValue());
        assertFalse(data.isPrimitive());
        assertFalse(data.isReference());
        assertTrue(data.isSequence());
        assertFalse(data.isNull());

        assertTrue(data.getInnerValue() instanceof List);
        assertEquals(1, ((List) data.getInnerValue()).size());

        assertEquals(testData, data.get(0));

        var set = data.toSet();
        assertTrue(set instanceof Set);
        assertEquals(1, set.size());
    }

    @Test
    public void testAsSequence() {
        assertEquals(data, data.asSequence());
    }
}
