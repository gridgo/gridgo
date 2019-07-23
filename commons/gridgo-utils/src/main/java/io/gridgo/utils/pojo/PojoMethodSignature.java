package io.gridgo.utils.pojo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.gridgo.utils.exception.RuntimeReflectiveOperationException;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PojoMethodSignature {

    private String fieldName;
    private Class<?> fieldType;
    private Method method;

    public String getMethodName() {
        return this.method.getName();
    }

    public String getMethodDescriptor() {
        String sig;

        StringBuilder sb = new StringBuilder("(");
        for (Class<?> c : method.getParameterTypes())
            sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
        return sb.append(')').append(method.getReturnType() == void.class ? "V"
                : (sig = Array.newInstance(method.getReturnType(), 0).toString()).substring(1, sig.indexOf('@')))
                .toString().replaceAll("\\.", "/");
    }

    public Class<?> getWrapperType() {
        if (this.fieldType.isPrimitive()) {
            switch (this.fieldType.getName()) {
            case "void": // this case may never reach...
                return Void.class;
            case "boolean":
                return Boolean.class;
            case "char":
                return Character.class;
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            }
        }
        return this.fieldType;
    }

    /**
     * use when the field is an array
     * 
     * @return
     */
    public Class<?> getComponentType() {
        if (this.fieldType.isArray()) {
            return this.fieldType.getComponentType();
        }
        return null;
    }

    /**
     * when field have generic type declaration
     * 
     * @return list of generic types belong to corresponding field
     * @throws RuntimeReflectiveOperationException if the corresponding field not
     *                                             found
     */
    public Class<?>[] getGenericTypes() {
        try {
            Class<?> clazz = method.getDeclaringClass();
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {
                throw new ReflectiveOperationException(
                        "Field not found: " + fieldName + " in type: " + clazz.getName());
            }
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            Class<?>[] results = new Class<?>[actualTypes.length];
            int i = 0;
            for (Type type : actualTypes) {
                results[i++] = Class.forName(type.getTypeName());
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeReflectiveOperationException(e);
        }
    }
}
