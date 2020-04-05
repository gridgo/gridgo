package io.gridgo.otac.code.block;

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
        return condition.requiredImports();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("if (").append(condition).append(") ");
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
