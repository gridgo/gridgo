package io.gridgo.otac.code.block;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.value.OtacValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacCase extends OtacBlock {
    private @NonNull OtacValue value;

    @Builder.Default
    private boolean curlyBracketsWrapped = true;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        imports.addAll(value.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("case ").append(value.toString().trim()).append(": ");
        if (!curlyBracketsWrapped)
            sb.append("\n");
        writeBodyTo(sb, curlyBracketsWrapped ? 0 : 1, curlyBracketsWrapped);
        return sb.toString();
    }
}