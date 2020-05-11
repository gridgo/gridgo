package io.gridgo.pojo.reflect.type;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Test;

import io.gridgo.pojo.test.support.Bar;
import io.gridgo.pojo.test.support.CombinedPojo;
import io.gridgo.pojo.test.support.Primitive;
import io.gridgo.pojo.test.support.Wrapper;
import io.gridgo.pojo.test.support.WrapperArray;
import io.gridgo.utils.StringUtils;

public class TestPojoTypes {

    private void verifyAllField(Class<?> effectiveClass) throws NoSuchMethodException, SecurityException {
        System.out.println(effectiveClass.getName());
        var clsCursor = effectiveClass;
        while (clsCursor != Object.class) {
            var fields = clsCursor.getDeclaredFields();
            for (var field : fields)
                verifyField(field, effectiveClass);
            clsCursor = clsCursor.getSuperclass();
        }

        System.out.println();
    }

    private void verifyField(Field field, Class<?> effectiveClass) throws NoSuchMethodException, SecurityException {
        var typeInfo = PojoTypes.extractFieldTypeInfo(field, effectiveClass);
        System.out.println("\t- " + field.getName() + " : " + typeInfo);

        String setterMethodName, getterMethodName;

        if (field.getType() == boolean.class) {
            if (field.getName().startsWith("is")) {
                setterMethodName = "set" + field.getName().substring(2);
                getterMethodName = field.getName();
            } else {
                var tmp = StringUtils.upperCaseFirstLetter(field.getName());
                setterMethodName = "set" + tmp;
                getterMethodName = "is" + tmp;
            }
        } else {
            var tmp = StringUtils.upperCaseFirstLetter(field.getName());
            setterMethodName = "set" + tmp;
            getterMethodName = "get" + tmp;
        }

        var getter = effectiveClass.getMethod(getterMethodName);
        assertEquals(typeInfo, PojoTypes.extractReturnTypeInfo(getter, effectiveClass));

        var setter = effectiveClass.getMethod(setterMethodName, field.getType());
        assertEquals(typeInfo, PojoTypes.extractFirstParamTypeInfo(setter, effectiveClass));
    }

    @Test
    public void testFieldType() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
        verifyAllField(Primitive.class);
        verifyAllField(Wrapper.class);
        verifyAllField(WrapperArray.class);
        verifyAllField(Bar.class);
        verifyAllField(CombinedPojo.class);
    }
}
