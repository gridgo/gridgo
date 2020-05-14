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
public class PojoArrayType extends PojoType {

    private final @NonNull PojoType componentType;

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public String getSimpleName() {
        return componentType.getSimpleName() + "[]";
    }

    @Override
    public String getSimpleNameWithoutGeneric() {
        return componentType.getSimpleNameWithoutGeneric() + "[]";
    }
}
