package io.gridgo.otac.value;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacCastedValue extends OtacValue {
    private @NonNull OtacType castTo;
    private @NonNull OtacValue target;

    @Builder.Default
    private boolean forceArray = false;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(castTo.requiredImports());
        imports.addAll(target.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        return new StringBuilder() //
                .append("(") //
                .append(castTo.toString().trim()) //
                .append(forceArray ? "[]" : "") //
                .append(") ") //
                .append(target.toString()) //
                .toString();
    }

    @Override
    public boolean needParenthesesOnInvoke() {
        return true;
    }
}