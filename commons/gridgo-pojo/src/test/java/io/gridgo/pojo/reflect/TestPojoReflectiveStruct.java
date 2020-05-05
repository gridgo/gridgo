package io.gridgo.pojo.reflect;

import org.junit.Test;

import io.gridgo.pojo.test.support.Primitive;

public class TestPojoReflectiveStruct {

    @Test
    public void testCreateStruct() {
        var reflectiveStruct = PojoReflectiveStruct.of(Primitive.class);
        System.out.println(reflectiveStruct.getters());
        System.out.println(reflectiveStruct.setters());
    }
}
