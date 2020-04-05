package io.gridgo.otac.code.line;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacType;
import io.gridgo.otac.exception.OtacException;
import io.gridgo.otac.value.OtacValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacDeclareVariable extends OtacLine {

    @Builder.Default
    private boolean isFinal = false;

    private OtacType type;

    private @NonNull String name;

    private OtacValue initValue;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (type != null)
            imports.addAll(type.requiredImports());
        if (initValue != null)
            imports.addAll(initValue.requiredImports());
        return imports;
    }

    @Override
    public String toStringWithoutSemicolon() {
        if (type == null && initValue == null)
            throw new OtacException("type and initValue cannot be null together");
        var sb = new StringBuilder();
        if (isFinal)
            sb.append("final ");
        sb.append(type == null ? "var" : type.getType().getSimpleName()).append(" ");
        sb.append(name);
        if (initValue != null)
            sb.append(" = ").append(initValue);
        return sb.toString();
    }
}