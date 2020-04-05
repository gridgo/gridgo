package io.gridgo.otac.code.block;

import static io.gridgo.otac.utils.OtacUtils.tabs;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.code.OtacCodeElement;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacBlock extends OtacCodeElement {

    public static final OtacBlock of(List<OtacCodeElement> lines) {
        return OtacBlock.builder().lines(lines).build();
    }

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

    public void writeBodyTo(StringBuilder sb, int numTabs, boolean wrapped) {
        if (wrapped)
            sb.append("{\n");
        if (!lines.isEmpty()) {
            var contentNumTabs = numTabs + (wrapped ? 1 : 0);
            sb.append(tabs(contentNumTabs, lines.get(0).toString()));
            for (int i = 1; i < lines.size(); i++)
                sb.append("\n").append(tabs(contentNumTabs, lines.get(i).toString()));
        }
        if (wrapped)
            sb.append(tabs(numTabs)).append("\n}");
    }

    @Override
    public String toString() {
        if (lines == null || lines.isEmpty())
            return "{}";
        var sb = new StringBuilder();
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
