package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.test.support.PojoWithBElement;

public class TestPojoWithBElement {

    private PojoWithBElement pojo;

    private Object obj;

    @Before
    public void setup() {
        obj = new Object();
        pojo = PojoWithBElement.builder() //
                .bValue(BValue.of("this is test text")) //
                .bObject(BObject.ofSequence("key1", "value", "key2", 1, "key3", true)) //
                .bArray(BArray.ofSequence("text", false, 1, 'z')) //
                .bContainer(BObject.ofSequence("key", true)) //
                .bReference(BReference.of(obj)) //
                .build();
        pojo.setBElement(BReference.of(TestPojoWithBElement.class));
    }

    @Test
    public void testToBelement() {
        var serialized = BObject.ofPojo(pojo);
        var deserialized = serialized.asObject().toPojo(PojoWithBElement.class);
        assertEquals(pojo, deserialized);
    }
}
