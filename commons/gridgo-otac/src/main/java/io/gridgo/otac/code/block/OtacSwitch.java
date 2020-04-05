package io.gridgo.otac.code.block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacSwitch extends OtacCodeElement {

    private @NonNull OtacValue key;

    @Singular("addCase")
    private final List<OtacCase> cases;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        for (var c : cases)
            imports.addAll(c.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("switch (").append(key.toString().trim()).append(") {\n");
        for (var c : cases)
            sb.append(c).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
