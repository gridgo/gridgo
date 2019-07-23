package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.test.support.CollectionVO;
import io.gridgo.utils.pojo.test.support.PrimitiveArrayVO;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestPojoMethodSignature {

    @Test
    public void testMethodDescriptor() throws NoSuchMethodException, SecurityException {
        String fieldName = "listPrimitive";
        Method method = CollectionVO.class.getDeclaredMethod("getListPrimitive");
        var sign = PojoMethodSignature.builder() //
                .fieldType(method.getReturnType()) //
                .fieldName(fieldName) //
                .method(method) //
                .build();

        assertEquals("()Ljava/util/List;", sign.getMethodDescriptor());
    }

    @Test
    public void testMethodGenericType() throws NoSuchMethodException, SecurityException {
        String fieldName = "mapPrimitive";
        Method method = CollectionVO.class.getDeclaredMethod("getMapPrimitive");
        var sign = PojoMethodSignature.builder() //
                .fieldType(method.getReturnType()) //
                .fieldName(fieldName) //
                .method(method) //
                .build();

        Class<?>[] genericTypes = sign.getGenericTypes();
        assertEquals(String.class, genericTypes[0]);
        assertEquals(PrimitiveVO.class, genericTypes[1]);
    }

    @Test
    public void testMethodComponentType() throws NoSuchMethodException, SecurityException {
        String fieldName = "booleanValue";
        Method method = PrimitiveArrayVO.class.getDeclaredMethod("getBooleanValue");
        var sign = PojoMethodSignature.builder() //
                .fieldType(method.getReturnType()) //
                .fieldName(fieldName) //
                .method(method) //
                .build();

        Class<?> componentType = sign.getComponentType();
        assertEquals(Boolean.TYPE, componentType);

        fieldName = "stringValue";
        method = PrimitiveArrayVO.class.getDeclaredMethod("getStringValue");
        sign = PojoMethodSignature.builder() //
                .fieldType(method.getReturnType()) //
                .fieldName(fieldName) //
                .method(method) //
                .build();

        componentType = sign.getComponentType();
        assertEquals(String.class, componentType);
    }

}
