package io.gridgo.pojo.reflect;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import io.gridgo.pojo.generic.PojoTypes;
import io.gridgo.pojo.test.support.Bar;
import io.gridgo.pojo.test.support.Primitive;

public class TestPojoReflectiveStruct {

    @Test
    @Ignore
    public void testCreateStruct() {
        var reflectiveStruct = PojoReflectiveStruct.of(Primitive.class);
        System.out.println(reflectiveStruct.getters());
        System.out.println(reflectiveStruct.setters());
    }

    @Test
    public void testGeneric() throws NoSuchMethodException, SecurityException {

        Method method = null;

//        method = Bar.class.getMethod("getData");
//        System.out.println(PojoTypes.extractTypeInfo(method.getGenericReturnType(), Bar.class));

        method = Bar.class.getMethod("setData", List.class);
        System.out.println(PojoTypes.extractTypeInfo(method.getGenericParameterTypes()[0], Bar.class));
    }
}
