package io.gridgo.utils.pojo;

import static io.gridgo.utils.PrimitiveUtils.getPrimitiveFromWrapperType;
import static io.gridgo.utils.pojo.PojoUtils.extractGenericTypes;
import static io.gridgo.utils.pojo.PojoUtils.extractMethodDescriptor;
import static lombok.AccessLevel.PACKAGE;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.translator.ValueTranslator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter(PACKAGE)
@SuppressWarnings("rawtypes")
public final class PojoMethodSignature {

    private final @NonNull Method method;
    private final @NonNull String fieldName;
    private final @NonNull Class<?> fieldType;
    private final String transformedFieldName;
    private final String transformedOrDefaultFieldName;

    private PojoGetterProxy getterProxy;
    private PojoSetterProxy setterProxy;

    private PojoGetterProxy elementGetterProxy;
    private PojoSetterProxy elementSetterProxy;

    private final boolean isWrapperType;
    private final boolean isPrimitiveType;
    private final boolean isPrimitiveOrWrapperType;
    private final boolean isExtPrimitive;

    private final boolean isArrayType;
    private final boolean isListType;
    private final boolean isSetType;
    private final boolean isCollectionType;
    private final boolean isSequenceType;

    private final boolean isMapType;
    private final boolean isPojoType;

    private final boolean isMapOrPojoType;

    private final Class<?> wrapperType;
    private final Class<?> primitiveTypeFromWrapperType;
    private final Class<?> componentType;

    private final String methodDescriptor;
    private final String methodName;
    private final Class<?>[] genericTypes;

    private final ValueTranslator valueTranslator;
    private final boolean isSetter;
    private final boolean isGetter;

    @Builder
    private PojoMethodSignature(Method method, String fieldName, Class<?> fieldType, String transformedFieldName,
            ValueTranslator valueTranslator) {

        this.method = method;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.transformedFieldName = transformedFieldName;
        this.valueTranslator = valueTranslator;
        this.isSetter = method.getReturnType() == void.class;
        this.isGetter = !isSetter;

        this.isPrimitiveType = fieldType.isPrimitive();
        this.isWrapperType = PrimitiveUtils.isWrapperType(fieldType);
        this.isPrimitiveOrWrapperType = isPrimitiveType || isWrapperType;
        this.isExtPrimitive = PrimitiveUtils.isPrimitive(fieldType);

        this.isArrayType = fieldType.isArray();
        this.isSetType = Set.class.isAssignableFrom(fieldType);
        this.isListType = List.class.isAssignableFrom(fieldType);
        this.isCollectionType = isSetType || isListType;
        this.isSequenceType = isCollectionType || isArrayType;

        this.isMapType = Map.class.isAssignableFrom(fieldType);
        this.isPojoType = !(isPrimitiveOrWrapperType || isSequenceType || isMapType);
        this.isMapOrPojoType = isMapType || isPojoType;

        var wrapperType = PrimitiveUtils.getWrapperType(fieldType);
        this.wrapperType = wrapperType == null ? fieldType : wrapperType;

        this.primitiveTypeFromWrapperType = getPrimitiveFromWrapperType(this.wrapperType);
        this.componentType = fieldType.isArray() ? fieldType.getComponentType() : null;
        this.methodDescriptor = extractMethodDescriptor(method);
        this.methodName = method.getName();
        this.genericTypes = extractGenericTypes(method, fieldName);
        this.transformedOrDefaultFieldName = transformedFieldName == null ? fieldName : transformedFieldName;
    }
}
