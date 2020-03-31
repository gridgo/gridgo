package io.gridgo.pojo.otac;

import java.util.Collections;
import java.util.Set;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacGeneric implements OtacRequireImports {

    public static final OtacGeneric ANY = OtacGeneric.builder().build();

    public static OtacGeneric of(String name) {
        return OtacGeneric.builder().name(name).build();
    }

    @Builder.Default
    private final String name = "?";

    @Builder.Default
    private final OtacInheritOperator operator = OtacInheritOperator.NONE;

    private final OtacType type;

    @Override
    public Set<Class<?>> requiredImports() {
        if (type == null)
            return Collections.emptySet();
        return type.requiredImports();
    }

    @Override
    public String toString() {
        if (operator == null || operator == OtacInheritOperator.NONE)
            return name;

        if (type == null)
            throw new PojoException("[OTAC] type cannot be null while operator = " + operator);

        return name + " " + operator.getKeywork() + (type == null ? "" : type.toString());
    }

}
