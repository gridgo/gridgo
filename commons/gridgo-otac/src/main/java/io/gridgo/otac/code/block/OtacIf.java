package io.gridgo.otac.code.block;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacIf extends OtacBlock {

    private @NonNull OtacValue condition;

    private OtacElse orElse;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        imports.addAll(condition.requiredImports());
        if (orElse != null)
            imports.addAll(orElse.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("if (").append(condition).append(") ");
        writeBodyTo(sb, 0, true);
        if (orElse != null)
            sb.append(orElse);
        return sb.toString();
    }
}
