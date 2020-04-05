package io.gridgo.otac.code.block;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacFor extends OtacLoop {

    private OtacLine init;
    private OtacValue condition;
    private OtacLine afterLoop;

    @Override
    public Set<Class<?>> requiredImports() {
        if (init == null && condition == null && afterLoop == null)
            return Collections.emptySet();
        var imports = new HashSet<Class<?>>();
        if (init != null)
            imports.addAll(init.requiredImports());
        if (condition != null)
            imports.addAll(condition.requiredImports());
        if (afterLoop != null)
            imports.addAll(afterLoop.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("for (") //
                .append(init == null ? "; " : init) //
                .append(condition == null ? "" : condition).append("; ") //
                .append(afterLoop == null ? ";" : afterLoop.toStringWithoutSemicolon()) //
                .append(") ");
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
