package io.gridgo.pojo.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.pojo.annotation.FieldTag;
import io.gridgo.pojo.support.PojoElementType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface PojoReflectiveElement {

    PojoElementType elementType();

    Class<?> effectiveClass();

    Class<?> declaringClass();

    FieldTag[] tags();

    default boolean isField() {
        return elementType() == PojoElementType.FIELD;
    }

    default boolean isMethod() {
        return elementType() == PojoElementType.METHOD;
    }

    @SuppressWarnings("unchecked")
    default <T> T getElement() {
        switch (elementType()) {
        case FIELD:
            return (T) field();
        case METHOD:
            return (T) method();
        }
        throw new UnsupportedOperationException("Element type not supported: " + elementType());
    }

    default Field field() {
        throw new UnsupportedOperationException("not supported in " + getClass().getName());
    }

    default Method method() {
        throw new UnsupportedOperationException("not supported in " + getClass().getName());
    }

    static PojoReflectiveElement ofField(Field field) {
        return new PojoReflectiveField(field);
    }

    static PojoReflectiveElement ofField(Field field, Class<?> effectiveClass) {
        return new PojoReflectiveField(field, effectiveClass);
    }

    static PojoReflectiveElement ofMethod(Method method) {
        return new PojoReflectiveMethod(method);
    }

    static PojoReflectiveElement ofMethod(Method method, Class<?> effectiveClass) {
        return new PojoReflectiveMethod(method, effectiveClass);
    }
}

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractPojoReflectiveElement implements PojoReflectiveElement {
    private final @NonNull PojoElementType elementType;
    private final @NonNull Class<?> effectiveClass;
}

@Getter
class PojoReflectiveMethod extends AbstractPojoReflectiveElement {

    private final @NonNull Method method;

    PojoReflectiveMethod(Method method, Class<?> effectiveClass) {
        super(PojoElementType.METHOD, effectiveClass);
        this.method = method;
    }

    PojoReflectiveMethod(Method method) {
        this(method, method.getDeclaringClass());
    }

    @Override
    public Class<?> declaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public FieldTag[] tags() {
        return method.getAnnotationsByType(FieldTag.class);
    }
}

@Getter
@Accessors(fluent = true)
class PojoReflectiveField extends AbstractPojoReflectiveElement {

    private final @NonNull Field field;

    PojoReflectiveField(Field field, Class<?> effectiveClass) {
        super(PojoElementType.METHOD, effectiveClass);
        this.field = field;
    }

    PojoReflectiveField(Field field) {
        this(field, field.getDeclaringClass());
    }

    @Override
    public Class<?> declaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public FieldTag[] tags() {
        return field.getAnnotationsByType(FieldTag.class);
    }
}