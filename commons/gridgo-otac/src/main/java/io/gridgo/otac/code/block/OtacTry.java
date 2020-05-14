package io.gridgo.otac.code.block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.line.OtacLine;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacTry extends OtacBlock {

    @Singular
    private List<OtacLine> openResources;

    private OtacCodeElement finallyDo;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        for (var line : openResources)
            imports.addAll(line.requiredImports());
        if (finallyDo != null)
            imports.addAll(finallyDo.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("try ");
        if (!openResources.isEmpty()) {
            sb.append("(").append(openResources.get(0).toStringWithoutSemicolon());
            for (int i = 1; i < openResources.size(); i++)
                sb.append("; ").append(openResources.get(i).toStringWithoutSemicolon());
            sb.append(") ");
        }
        writeBodyTo(sb, 0, true);
        if (finallyDo != null) {
            sb.append(" finally ");
            writeBodyTo(List.of(finallyDo), sb, 0, true);
        }
        return sb.toString();
    }
}
