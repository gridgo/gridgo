package io.gridgo.bean.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.support.BElementPojoHelper;
import io.gridgo.bean.test.support.PojoWithBElement;

public class TestPojoWithBElement {

    private PojoWithBElement pojo;

    @Before
    public void setup() {
        pojo = PojoWithBElement.builder() //
                .bValue(BValue.of("this is test text")) //
                .bObject(BObject.ofSequence("key1", "value", "key2", 1, "key3", true)) //
                .bArray(BArray.ofSequence("text", false, 1, 'z')) //
                .build();
        pojo.setBElement(BReference.of(TestPojoWithBElement.class));
    }

    @Test
    public void testToBelement() {
        var serialized = BElementPojoHelper.anyToBElement(pojo);
        System.out.println(serialized);

        var deserialized = BElementPojoHelper.bObjectToPojo(serialized.asObject(), PojoWithBElement.class);
        System.out.println(deserialized);

        assertEquals(pojo, deserialized);
    }
}
