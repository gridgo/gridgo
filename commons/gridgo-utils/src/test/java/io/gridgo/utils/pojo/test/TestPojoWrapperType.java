package io.gridgo.utils.pojo.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.gridgo.utils.pojo.getter.PojoGetterGenerator;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.getter.PojoGetterSignatures;
import io.gridgo.utils.pojo.test.support.PrimitiveVO;

public class TestPojoWrapperType {

    @Test
    public void testWrapperType() {
        PojoGetterRegistry registry = new PojoGetterRegistry(PojoGetterGenerator.newAsm());

        PojoGetterSignatures getterSignatures = registry.getGetterSignatures(PrimitiveVO.class);

        assertEquals(Boolean.class, getterSignatures.getMethodSignature("booleanValue").getWrapperType());
        assertEquals(Character.class, getterSignatures.getMethodSignature("charValue").getWrapperType());
        assertEquals(Byte.class, getterSignatures.getMethodSignature("byteValue").getWrapperType());
        assertEquals(Short.class, getterSignatures.getMethodSignature("shortValue").getWrapperType());
        assertEquals(Integer.class, getterSignatures.getMethodSignature("intValue").getWrapperType());
        assertEquals(Long.class, getterSignatures.getMethodSignature("longValue").getWrapperType());
        assertEquals(Float.class, getterSignatures.getMethodSignature("floatValue").getWrapperType());
        assertEquals(Double.class, getterSignatures.getMethodSignature("doubleValue").getWrapperType());
        assertEquals(String.class, getterSignatures.getMethodSignature("stringValue").getWrapperType());
    }
}
