package io.gridgo.otac.code.block;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacType;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacForeach extends OtacLoop {

    private OtacType type;
    private @NonNull String variableName;
    private @NonNull OtacValue sequence;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        if (type != null)
            imports.addAll(type.requiredImports());
        imports.addAll(sequence.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("for (") //
                .append(type == null ? "var" : type) //
                .append(" ").append(variableName).append(" : ") //
                .append(sequence) //
                .append(") ");
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
