package io.gridgo.pojo.reflect.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@EqualsAndHashCode
@Accessors(fluent = true)
public abstract class PojoType {

    public boolean isArray() {
        return false;
    }

    public boolean isSimple() {
        return false;
    }

    public boolean isParameterized() {
        return false;
    }

    public PojoSimpleType asSimple() {
        return (PojoSimpleType) this;
    }

    public PojoParameterizedType asParameterized() {
        return (PojoParameterizedType) this;
    }

    public PojoArrayType asArray() {
        return (PojoArrayType) this;
    }

    public abstract String getSimpleName();
    
    public abstract String getSimpleNameWithoutGeneric();
}
