package io.gridgo.otac;

import java.util.Collections;
import java.util.Set;

import io.gridgo.otac.exception.OtacException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacGeneric implements OtacRequireImports {

    public static final OtacGeneric ANY = OtacGeneric.builder().build();

    public static OtacGeneric ofDeclared(String name) {
        return OtacGeneric.builder().name(name).build();
    }

    public static OtacGeneric generic(Class<?> type) {
        return generic(OtacType.typeOf(type));
    }

    public static OtacGeneric generic(OtacType type) {
        return builder().type(type).build();
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

    private final String name;

    @Builder.Default
    private @NonNull OtacInheritOperator operator = OtacInheritOperator.NONE;

    private final OtacType type;

    @Override
    public Set<Class<?>> requiredImports() {
        if (type == null)
            return Collections.emptySet();
        return type.requiredImports();
    }

    @Override
    public String toString() {
        if (name == null)
            return type == null ? "?" : type.toString().trim();

        if (operator == OtacInheritOperator.NONE)
            return name;

        if (type == null)
            throw new OtacException("[OTAC] type cannot be null while operator = " + operator);

        return name + " " + operator.getKeywork().trim() + " " + type.toString().trim();
    }

}
