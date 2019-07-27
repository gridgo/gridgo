package io.gridgo.utils.pojo;

import static lombok.AccessLevel.PACKAGE;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.RuntimeReflectiveOperationException;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public final class PojoMethodSignature {

    private Method method;
    private String fieldName;
    private Class<?> fieldType;
    private String transformedFieldName;

    @Setter(PACKAGE)
    private PojoGetterProxy getterProxy;

    @Setter(PACKAGE)
    private PojoSetterProxy setterProxy;

    @Setter(PACKAGE)
    private PojoGetterProxy elementGetterProxy;

    @Setter(PACKAGE)
    private PojoSetterProxy elementSetterProxy;

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
        var result = PrimitiveUtils.getWrapperType(this.fieldType);
        return result == null ? this.fieldType : result;
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
        Class<?> clazz = method.getDeclaringClass();

        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeReflectiveOperationException(
                    "Error while get declared field name `" + fieldName + "` in type: " + clazz.getName(), e);
        }

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            Class<?>[] results = new Class<?>[actualTypes.length];
            int i = 0;
            for (Type type : actualTypes) {
                if (type instanceof Class<?>) {
                    results[i++] = (Class<?>) type;
                } else {
                    try {
                        results[i++] = Class.forName(type.getTypeName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeReflectiveOperationException("Cannot get class for type: " + type);
                    }
                }
            }
            return results;
        }
        return null;
    }

    public boolean isCollectionType() {
        return Collection.class.isAssignableFrom(fieldType);
    }

    public boolean isListType() {
        return List.class.isAssignableFrom(this.fieldType);
    }

    public boolean isSetType() {
        return Set.class.isAssignableFrom(this.fieldType);
    }

    public boolean isMapType() {
        return Map.class.isAssignableFrom(this.fieldType);
    }

    public boolean isArrayType() {
        return this.fieldType.isArray();
    }

    public boolean isPrimitiveType() {
        return this.fieldType.isPrimitive();
    }

    public boolean isWrapperType() {
        return PrimitiveUtils.isWrapperType(fieldType);
    }

    public Class<?> getPrimitiveTypeFromWrapperType() {
        return PrimitiveUtils.getPrimitiveFromWrapperType(this.fieldType);
    }

    public boolean isPrimitiveOrWrapperType() {
        return this.isPrimitiveType() || this.isWrapperType();
    }

    public boolean isPojoType() {
        return !this.isPrimitiveOrWrapperType() && //
                !this.isArrayType() && //
                !this.isCollectionType() && //
                !this.isMapType();
    }

    /**
     * @return whether this fieldType is Map or Pojo
     */
    public boolean isMapOrPojoType() {
        return this.isMapType() || this.isPojoType();
    }

    /**
     * @return whether this fieldType is Collection or Array
     */
    public boolean isSequenceType() {
        return this.isCollectionType() || this.isArrayType();
    }

    public String getTransformedOrDefaultFieldName() {
        return transformedFieldName == null || transformedFieldName.isBlank() //
                ? fieldName //
                : transformedFieldName;
    }
}
