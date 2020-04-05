package io.gridgo.otac.value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.OtacType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacNewValue extends OtacValue {

    private @NonNull OtacType type;

    @Singular
    private List<OtacValue> parameters;

    public static OtacNewValue of(OtacType type) {
        return OtacNewValue.builder().type(type).build();
    }

    @Override
    public OtacType inferredType() {
        return type;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("new ");
        sb.append(type.toString().trim());
        sb.append("(");
        if (parameters != null && !parameters.isEmpty()) {
            sb.append(parameters.get(0));
            for (int i = 1; i < parameters.size(); i++)
                sb.append(", ").append(parameters.get(i).toString().trim());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        if (parameters != null)
            for (var p : parameters)
                imports.addAll(p.requiredImports());
        return imports;
    }
}