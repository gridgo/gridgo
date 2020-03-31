package io.gridgo.otac;

import java.util.Collections;
import java.util.Set;

import io.gridgo.otac.exception.OtacException;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacGeneric implements OtacRequireImports {

    public static final OtacGeneric ANY = OtacGeneric.builder().build();

    public static OtacGeneric ofDeclared(String name) {
        return OtacGeneric.builder().name(name).build();
    }

    public static OtacGeneric of(String name, OtacInheritOperator operator, OtacType type) {
        return OtacGeneric.builder() //
                .name(name) //
                .operator(operator) //
                .type(type) //
                .build();
    }

    public static OtacGeneric genericDeclared(String name) {
        return ofDeclared(name);
    }

    public static OtacGeneric generic(String name, OtacInheritOperator operator, OtacType type) {
        return of(name, operator, type);
    }

    public static OtacGeneric genericSuper(String name, OtacType type) {
        return of(name, OtacInheritOperator.SUPER, type);
    }

    public static OtacGeneric genericExtends(String name, OtacType type) {
        return of(name, OtacInheritOperator.EXTENDS, type);
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
            throw new OtacException("[OTAC] type cannot be null while operator = " + operator);

        return name + " " + operator.getKeywork() + (type == null ? "" : type.toString());
    }

}
