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

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        imports.addAll(condition.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("if (").append(condition).append(") ");
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
