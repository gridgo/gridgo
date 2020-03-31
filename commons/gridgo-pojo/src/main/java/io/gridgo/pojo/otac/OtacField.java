package io.gridgo.pojo.otac;

import java.util.HashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacField extends OtaNamedElement implements OtacRequireImports {

    @Builder.Default
    private boolean isVolatile = false;

    @Builder.Default
    private boolean isTransient = false;

    private @NonNull OtacType type;

    private OtacValue initValue;

    @Builder.Default
    private boolean generateSetter = false;

    @Builder.Default
    private boolean generateGetter = false;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString()) //
                .append(isTransient() ? "transient " : "") //
                .append(isVolatile() ? "volatile " : "") //
                .append(type.toString()) //
                .append(getName()) //
                .append(initValue != null ? (" = " + initValue.toString().trim()) : "") //
                .append(";\n");
        return sb.toString();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        if (initValue != null)
            imports.addAll(initValue.requiredImports());
        return imports;
    }
}
