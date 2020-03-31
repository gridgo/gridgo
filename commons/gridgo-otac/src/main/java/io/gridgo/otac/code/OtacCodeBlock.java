package io.gridgo.otac.code;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.utils.OtacUtils;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacCodeBlock extends OtacCodeElement {

    @Singular("addLine")
    private List<OtacCodeElement> lines;

    @Override
    public Set<Class<?>> requiredImports() {
        if (lines == null || lines.isEmpty())
            return Collections.emptySet();
        var imports = new HashSet<Class<?>>();
        for (var e : lines)
            imports.addAll(e.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        if (lines == null || lines.isEmpty())
            return "{}";
        var sb = new StringBuilder();
        sb.append("{\n");
        var tabs = OtacUtils.tabs(1);
        for (var l : lines)
            sb.append(tabs).append(l.toString()).append(";\n");
        sb.append("}");
        return sb.toString();
    }
}
