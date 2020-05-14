package io.gridgo.pojo.reflect.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
@ToString(callSuper = false)
public class PojoSimpleType extends PojoType {

    public static final PojoSimpleType OBJECT = PojoSimpleType.of(Object.class);

    private @NonNull Class<?> type;

    public static PojoSimpleType of(Class<?> type) {
        return PojoSimpleType.builder().type(type).build();
    }

    public boolean isWrapper() {
        return type == Byte.class //
                || type == Short.class //
                || type == Integer.class //
                || type == Long.class //
                || type == Float.class //
                || type == Double.class//
                || type == Boolean.class //
                || type == Character.class;
    }

    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName();
    }

    @Override
    public String getSimpleNameWithoutGeneric() {
        return type.getSimpleName();
    }
}
