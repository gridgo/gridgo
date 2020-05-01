package io.gridgo.pojo.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.pojo.support.PojoElementType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface PojoReflectiveElement {

    String name();

    PojoElementType type();

    Class<?> effectiveClass();

    Class<?> declaringClass();

    default boolean isField() {
        return type() == PojoElementType.FIELD;
    }

    default boolean isMethod() {
        return type() == PojoElementType.METHOD;
    }

    @SuppressWarnings("unchecked")
    default <T> T getElement() {
        switch (type()) {
        case FIELD:
            return (T) field();
        case METHOD:
            return (T) method();
        }
        throw new UnsupportedOperationException("Element type not supported: " + type());
    }

    default Field field() {
        throw new UnsupportedOperationException("not supported in " + getClass().getName());
    }

    default Method method() {
        throw new UnsupportedOperationException("not supported in " + getClass().getName());
    }

    static PojoReflectiveElement ofField(Field field, Class<?> effectiveClass) {
        return new PojoReflectiveField(field, effectiveClass);
    }

    static PojoReflectiveElement ofMethod(Method method, Class<?> effectiveClass) {
        return new PojoReflectiveMethod(method, effectiveClass);
    }
}

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractPojoReflectiveElement implements PojoReflectiveElement {
    private final @NonNull PojoElementType type;
    private final @NonNull Class<?> effectiveClass;
}

class PojoReflectiveMethod extends AbstractPojoReflectiveElement {

    @Getter
    private final @NonNull Method method;

    PojoReflectiveMethod(Method method, Class<?> effectiveClass) {
        super(PojoElementType.METHOD, effectiveClass);
        this.method = method;
    }

    @Override
    public String name() {
        return method.getName();
    }

    @Override
    public Class<?> declaringClass() {
        return method.getDeclaringClass();
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

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Class<?> declaringClass() {
        return field.getDeclaringClass();
    }
}